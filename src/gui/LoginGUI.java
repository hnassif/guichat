
package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.channels.UnsupportedAddressTypeException;
import java.security.InvalidParameterException;
import java.util.regex.PatternSyntaxException;

import javax.swing.*;

import client.Model;

@SuppressWarnings("serial")
public class LoginGUI extends JFrame implements ActionListener {
    //GUI Components
    private final JLabel welcomeMessage;
   
    private final JLabel username;
    private final JTextField usernameIn;
   
    private final JLabel ip;
    private final JTextField ipIn;
   
    private final JLabel port;
    private final JTextField portIn;
   
    private final JButton submit;
   
    private Model model;

    public LoginGUI() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
       
        welcomeMessage = new JLabel("Welcome to GUIChat! Log in to continue.");
        welcomeMessage.setName("welcomeMessage");
        welcomeMessage.setMinimumSize(getMinimumSize());
       
        username = new JLabel("Username: ");
        username.setName("username");
       
        usernameIn = new JTextField();
        usernameIn.setName("usernameIn");
        usernameIn.addActionListener(this);
       
        ip = new JLabel("IP Address: ");
        ip.setName("ip");
       
        ipIn = new JTextField();
        ipIn.setName("ipIn");
        ipIn.addActionListener(this);
       
        port = new JLabel("Port Number: ");
        port.setName("port");
       
        portIn = new JTextField();
        portIn.addActionListener(this);
        portIn.setName("portIn");
       
        submit = new JButton("Log In");
        submit.addActionListener(this);
        submit.setName("submit");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateContainerGaps(true);
        layout.setAutoCreateGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(welcomeMessage, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                //automatically determine size
                               
                        .addComponent(username, 290, 290, 290)
                        .addComponent(usernameIn, 120, 120, 120) //numbers found through experimentation
                       
                        .addComponent(ip)
                        .addComponent(ipIn, 120, 120, 120) //numbers found through experimentation
                       
                        .addComponent(port)
                        .addComponent(portIn, 120, 120, 120) //numbers found through experimentation
                       
                        .addComponent(submit)
                        )
                );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(welcomeMessage, GroupLayout.PREFERRED_SIZE,
                                GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                               
                        .addComponent(username)
                        .addComponent(usernameIn)
                       
                        .addComponent(ip)
                        .addComponent(ipIn)    
                       
                        .addComponent(port)
                        .addComponent(portIn)
                       
                        .addComponent(submit)
                        )
                );  
        this.setTitle("Login - GUIChat");
        //window is not resizable
        this.setResizable(false);
        this.pack();     
    }

    /**
     *
     * @return whether every field is valid or not
     * @throws PatternSyntaxException
     * @throws UnsupportedAddressTypeException
     * @throws InvalidParameterException
     */
    private boolean verifyFields() throws PatternSyntaxException,UnsupportedAddressTypeException,InvalidParameterException
    {  
        if (!usernameIn.getText().trim().matches("[A-Za-z0-9_]+")) {
            welcomeMessage.setText("Please enter a valid username (6 to 18 characters).");
            return false;
        }
       
        if(!(ipIn.getText().trim().matches("[0-9]+.[0-9]+.[0-9]+.[0-9]+"))){
            if (!(ipIn.getText().trim().matches("localhost"))){
                welcomeMessage.setText("Please enter a valid IP address.");
                return false;
            }
        }

        if (!portIn.getText().trim().matches("[0-9]{1,5}") || !(Integer.parseInt(portIn.getText())<65536)) {
            welcomeMessage.setText("Please enter a valid port number.");
            return false;
        }

        return true;
    }

    /**
     * call this method upon an actionListener (pressing enter on any JTextField or pressing the submit button)
     * this method will either change the welcomeMessage to reflect an error, or will call the login method
     * to login the user if nothing goes wrong
     */
   
    public synchronized void actionPerformed(ActionEvent e)
    {
        String username = usernameIn.getText().trim();
        String portVal = portIn.getText().trim();
        String ipVal = ipIn.getText().trim();
        boolean allcorrect = verifyFields(); //true if valid inputs to fields

        if (allcorrect){
            int port = Integer.parseInt(portVal); //we know this will parse correctly because it's a precondition to entering this if-block
            try {
                // initialize a model
                model = new Model(username, this);
                model.connect(ipVal, port);
            } catch (UnknownHostException e1) { //happens when there's a weird request for a host or port
                welcomeMessage.setText("Invalid hostname or port number");
            } catch (IOException e1) { //happens when server is busy or full
                welcomeMessage.setText("Cannnot connect to the server.");
            }
        }
    }

    /**
     * login a user and hide the current window
     * @param username
     * @throws IOException
     */
    public synchronized void login(String username) throws IOException{
        this.setVisible(false);
        MainGUI gui = new MainGUI(model);
        model.switchToMain(gui);
        gui.setVisible(true);
    }

    /**
     * Initialize a LoginGUI
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                LoginGUI main = new LoginGUI();
                main.setVisible(true);
            }
        });
    }

    public synchronized void displayError(String error) {
        welcomeMessage.setText(error);
       
    }

}