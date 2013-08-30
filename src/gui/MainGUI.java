package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


import javax.swing.*;

import client.Model;

@SuppressWarnings("serial")
public class MainGUI extends JFrame implements ActionListener {
    private final Model model;
    private final String username;


    protected final JLabel convoID;
    private final JTextField convoIDIn;

    private final JButton submit;
    private final JButton logout;

    private final JButton history;
    private final JLabel historyLabel;
    private final JTextField historyIn;

    private int historyID; //holds the value from historyIn. Used to make connections to the server
    private int conversationID; //holds the value from convoIDIn. Used to make connections to the server

    /** Record of all the conversations the user has participated */
    private Map<Integer, ConversationGUI> conversations;

    public MainGUI(final Model model) throws IOException  {
        this.model = model;
        this.username = model.getUsername();
        //this.onlineUsers = sendGetusers(username);

       
        /**
         * on force-close event, make sure to close all chatrooms and then logout
         */
      
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    disposeAll();
                    model.sendRequest("logout");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
       
        conversations = new HashMap<Integer, ConversationGUI>();

        String welcome = "Welcome, %s! Enter a conversation ID number. " +
                "If the conversation exists, you will join it. If not, you will be the " +
                "first participant! There are %d users logged in.";
        this.convoID = new JLabel(String.format(welcome, username, 50));
        this.convoID.setName("convoID");

        this.convoIDIn = new JTextField();
        this.convoIDIn.setName("convoIDIn");
        this.convoIDIn.addActionListener(new SubmitListener());

        this.submit = new JButton("submit");
        this.submit.setName("submit");       
        this.submit.addActionListener(new SubmitListener());

        this.logout = new JButton("logout");
        this.logout.setName("logout");
        this.logout.addActionListener(this);

        this.historyLabel = new JLabel("Enter a conversation ID to get the previous history " +
                "of that conversation: ");
        this.historyLabel.setName("historyLabel");

        this.historyIn = new JTextField();
        this.historyIn.setName("historyIn");
        this.historyIn.addActionListener(new HistoryListener()); //upon enter

        this.history = new JButton("Get History");
        this.history.setName("history");
        this.history.addActionListener(new HistoryListener()); //upon click


        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(convoID)
                        .addComponent(convoIDIn)
                        )
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(historyLabel)
                                .addComponent(historyIn)
                                .addComponent(history)                       
                                )
                                .addGroup(layout.createParallelGroup()
                                        .addComponent(submit)
                                        .addComponent(logout)
                                        )
                );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(convoID)
                        .addComponent(convoIDIn)
                        .addComponent(submit)
                        .addComponent(logout)
                        )
                        .addGroup(layout.createParallelGroup()
                                .addComponent(historyLabel)
                                .addComponent(historyIn)
                                .addComponent(history)
                                )
                                .addGroup(layout.createSequentialGroup()
                                        .addComponent(submit)
                                        .addComponent(logout)
                                        )
                );

        this.setTitle("Lobby - GUIChat");
        this.setResizable(false); //don't let user resize window
        this.pack(); 
    }

    /**
     * @return true if the convoID inputted was a valid integer, false otherwise
     */
    private synchronized boolean verifyField() {
        try{
            conversationID = Integer.parseInt(convoIDIn.getText());
            if(conversationID>0) //we only allow conversationIDs > 0
                return true;
            else
                return false;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }


    /**
     * @return true if the historyIn inputted was a valid integer, false otherwise
     */
    private synchronized boolean verifyInteger() {
        try{
            historyID = Integer.parseInt(historyIn.getText());
            if(historyID > 0) //we only allow conversationIDs > 0
                return true;
            else
                return false;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }


    /**
     * outputs the history of a conversation that the user was in in a new window
     */
    private class HistoryListener implements ActionListener{
        public synchronized void actionPerformed(ActionEvent e) {
            boolean isNumber = verifyInteger();
            if(isNumber) {
                try {
                    historyID = Integer.parseInt(historyIn.getText());
                    model.sendRequest(String.format(GETHISTORY, historyID, username));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                historyIn.setText(""); //clear
            }
            else {
                historyLabel.setText("Invalid input specified for history. Please enter an integer.");
            }
        }
    }

    /**
     * creates a new conversation based on the conversationID
     *
     */
    private class SubmitListener implements ActionListener{
        public synchronized void actionPerformed(ActionEvent arg0) {
            boolean allCorrect = verifyField();
            if(allCorrect){
                if(!conversations.containsKey(conversationID)){
                    try {
                        model.sendRequest(String.format(CREATE, conversationID, username));
                        convoIDIn.setText("");
                    } catch (IOException e) {
                        displayError("Cannot connect to the server");
                    }
                }
                else{ //if convos.contains(conversationID)
                    convoID.setText("You're already in that chatroom! To join another chatroom, "
                            +"enter a different conversation ID below.");
                    convoIDIn.selectAll();
                }
            }
            else{
                convoID.setText("Invalid conversation ID specified. Please enter a number.");
                convoIDIn.selectAll();
            }
        }
    }

    private String LOGOUT = "logout";
    private String CREATE = "create %d %s";
    private String GETHISTORY = "gethistory %s %s";

    /**
     * action class that triggers when the back button is pressed
     */

    @Override
    public synchronized void actionPerformed(ActionEvent arg0) {
        try {
            disposeAll();
            model.sendRequest(LOGOUT);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void backToLogin() {
        //this.setVisible(false);
        this.dispose();
        LoginGUI login = new LoginGUI();
        login.setVisible(true);
    }

    public synchronized void displayError(String string) {

    }

    public void addUser(String name) {
        //onlineUsers.addElement(name);

    }

    public void deleteUser(String name) {
        //onlineUsers.removeElement(name);

    }

    public synchronized void create(int id, String name) throws IOException {
        if (conversations.containsKey(id)) {
            return;
        }
        if (name.matches(username)) {
            System.out.println("concersation created");
            String welcome = "Congratulations, %s! You just joined chatoom %s. " +
                    "To join another chatroom, enter a conversation ID below."; //success
            convoID.setText(String.format(welcome, username, id));
            createConversation(id);
        }
        else {
            joinConversation(id, name);
        }

    }


    /**
     * create a new conversation and add the user to the list of activeUsers in that conversation
     * @param convID is the conversationID of the newly-created conversation
     * @throws IOException
     */
    private synchronized void createConversation(int id) throws IOException {
        ConversationGUI convo = new ConversationGUI(model, id, this);
        conversations.put(id, convo);
        convo.setVisible(true);
    }

    private synchronized void joinConversation(int id, String name) throws IOException {
        if (name!=username)
            conversations.get(id).addMessage(name + " has joined the conversation");
        else
            conversations.get(id).setVisible(true);
    }

    public synchronized void displayHistory(int id, String msg) {
        HistoryGUI history = new HistoryGUI(id, msg);
        history.setVisible(true);
    }

    public synchronized void removeConversation(int id) {
        if (conversations.containsKey(id)) {
            conversations.remove(id);
        }
    }
   
    public synchronized void leave(int id, String name) {
        if (conversations.containsKey(id)) {
            conversations.get(id).addMessage(username + " has left the conversation");
        }

    }
   
    public synchronized void say(String time, int id, String user, String msg) {
        if (!user.matches(username)){
            String say = "[%s] %s says %s";
            conversations.get(id).addMessage(String.format(say, time, user, msg));
        }
    }

    public void getUsers(String[] tokens) {
        //for (int i = 1; i < tokens.length; i++)
        // onlineUsers.addElement(tokens[i]);

    }
    public synchronized void disposeAll() {
        for(Entry<Integer, ConversationGUI> entry: conversations.entrySet()) {
            entry.getValue().dispose();
            conversations.remove(entry.getKey());
        }
    }
}






