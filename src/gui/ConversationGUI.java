
package gui;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.*;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import server.Time;

import client.Model;

@SuppressWarnings("serial")
public class ConversationGUI extends JFrame implements ActionListener{
 
    private Model model;
    private MainGUI main;
    private JButton leave;
    private JScrollPane allMessagescrollTab;
    private JTextField inputMessage;
    private JButton sendMessage;
    private final int convID;
    private final String username;
    private JTextArea allMessages; //main text box
   
    private final String SAY = "say %d %s";
   
    public ConversationGUI(Model model, int id, MainGUI main){
        super("Conversation "+ id);
        this.convID = id;
        this.username = model.getUsername();
        this.model = model;
        this.main = main;
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
       
        Container container=this.getContentPane();
        GroupLayout layout=new GroupLayout(container);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        container.setLayout(layout);
       
        inputMessage=new JTextField();
        inputMessage.setName("inputMessage");
       
        sendMessage=new JButton("SEND");
        sendMessage.setName("send");
       
       
        allMessages=new JTextArea("",30,20);
        allMessages.setName("allMessages");
        allMessages.setEditable(false);
       
        allMessagescrollTab=new JScrollPane(allMessages);
        allMessagescrollTab.setName("allMessages");
       
        leave=new JButton("leave");
        leave.setName("leave");
       
       
        ParallelGroup paraGroup=layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        SequentialGroup settingsS=layout.createSequentialGroup();
        settingsS.addComponent(leave);

        SequentialGroup messageManagement=layout.createSequentialGroup();
        messageManagement.addComponent(inputMessage);
        messageManagement.addComponent(sendMessage);
        paraGroup.addGroup(settingsS);
        paraGroup.addComponent(allMessages);
        paraGroup.addGroup(messageManagement);
       
        SequentialGroup seqGroup=layout.createSequentialGroup();
        ParallelGroup userManagement=layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        userManagement.addComponent(leave);
        ParallelGroup messagingGroup=layout.createParallelGroup(GroupLayout.Alignment.LEADING);
        messagingGroup.addComponent(inputMessage);
        messagingGroup.addComponent(sendMessage);
        seqGroup.addGroup(userManagement);
        seqGroup.addComponent(allMessages);
        seqGroup.addGroup(messagingGroup);
       
       

        layout.setHorizontalGroup(paraGroup);
        layout.setVerticalGroup(seqGroup);
       
        this.pack();
       
        /**
         * sends a message to the chatroom
         */
        sendMessage.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                sendMessage();
            }
          
        });
       
        leave.addActionListener(this);
       
        /**
         * method to handle the instance of a force-close
         */
        super.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent we){
                try {
                    leaveConversation();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
       
        /**
         * method to handle input messages
         */
       
        inputMessage.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                sendMessage();
                }           
         });       
    }
   
    public synchronized void sendMessage() {
        String input = inputMessage.getText();
        try {
            model.sendRequest(String.format(SAY, convID, input));
            inputMessage.setText("");

            String message = "[%s] %s says %s";
            //TODO Fix time!
            allMessages.append(String.format(message, Time.main(), username, input) + "\n");

            //Make sure the new text is visible, even if there
            //was a selection in the text area.
            allMessages.setCaretPosition(allMessages.getDocument().getLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   
    public synchronized void addMessage(String msg) {
        allMessages.append(msg + "\n");
 
        //Make sure the new text is visible, even if there
        //was a selection in the text area.
        allMessages.setCaretPosition(allMessages.getDocument().getLength());
    }
   
    private String LEAVE = "leave %d %s";
    /**
     * disconnects the user and cleans up (removes him from the current conversation)
     * @throws IOException
     */
    public synchronized void leaveConversation() throws IOException{
        main.removeConversation(convID);
        model.sendRequest(String.format(LEAVE, convID, username));
    }
 
   
    /**
     * closes the current window and disconnects the user from the current chatroom
     */
    @Override
    public synchronized void actionPerformed(ActionEvent arg0) {
        this.setVisible(false);
        try {
            leaveConversation();
        } catch (IOException e) {
            e.printStackTrace();
        }
       
    }
}