
package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Chat server runner.
 */
public class Server {

    private final ServerSocket serverSocket;

    /** username : socket the client connected to */
    private Map<String, Socket> activeUsers;
    /** conversation id: conversation */
    private Map<Integer, Conversation> allConversations;

    /**
     * Start a chat server.
     *
     * @param debug
     * @param port
     * @throws IOException
     */
    public Server(boolean debug, int port) throws IOException {
        this.serverSocket = new ServerSocket(port);

        this.activeUsers = new HashMap<String, Socket>();
        this.allConversations = new HashMap<Integer, Conversation>();
    }

    private void serve() throws IOException {
        while (true) {
            final Socket socket = serverSocket.accept();
            // start a new thread to handle the connection
            Client client = new Client(socket, this);
            client.start();
        }
    }

    protected synchronized void createConversation(int ID, String username) throws IOException{
        if (allConversations.containsKey(ID))
            allConversations.get(ID).addUser(username);
        else{
            ArrayList<String> user = new ArrayList<String>();
            user.add(username);
            allConversations.put(ID, new Conversation(ID, user));
        }
    }

    /**
     * Send a message to a user
     *
     * @param username
     * @param id
     * @param message
     * @throws IOException
     */
    protected synchronized void sendMessage(String username, String message) throws IOException{
        System.out.println(message);
        String emessage = Encoding.encode(message);
        synchronized(activeUsers) {
            Socket socket = activeUsers.get(username);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), /* autoFlush */ true);
            out.println(emessage);
        }

    }

    /**
     * Send a message to a user
     *
     * @param username
     * @param id
     * @param message
     * @throws IOException
     */
    protected synchronized void sendMessage(Socket socket, String message) throws IOException{
        String emessage = Encoding.encode(message);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), /* autoFlush */ true);
            out.println(emessage);

    }
   
    /**
     * Broadcast a message to all participants in a conversation
     *
     * @param id
     * @param message
     * @throws IOException
     */
    protected synchronized void broadcast(int id, String message) throws IOException{
        String emessage = Encoding.encode(message);
        for (String u: allConversations.get(id).getUserList()){
            Socket socket = activeUsers.get(u);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), /* autoFlush */ true);
            out.println(emessage);
        }
    }

    /**
     * Broadcast the message to everyone
     * @param message
     * @throws IOException
     */
    protected synchronized void broadcast(String message) throws IOException{
        String emessage = Encoding.encode(message);
        for (String u: activeUsers.keySet()){
            Socket socket = activeUsers.get(u);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), /* autoFlush */ true);
            out.println(emessage);
        }
    }

    public synchronized boolean containsConversation(int id) {
            return allConversations.containsKey(id);
       
    }

    public synchronized boolean isActiveIn(int id, String user) {
            return allConversations.get(id).isActive(user);
    }

    public synchronized boolean isContainedIn(int id, String user) {
            return allConversations.get(id).isContained(user);
    }

    public synchronized String allActiveUsers(){
        StringBuffer buffer = new StringBuffer();
        for (String user: activeUsers.keySet()){
            buffer.append(" ");
            buffer.append(user);         
        }
        return buffer.toString();     
    }



    /**
     * Start a Server running on the default port (4444).
     *
     * Usage: Server [DEBUG [-p PORT]]
     *
     * The DEBUG argument should be either 'true' or 'false'.
     *
     * PORT is an optional integer argument specifying a listening port other than
     * the default.
     */
    public static void main(String[] args) {
        boolean debug = false;
        int port = 4444;

        try {
            if (args.length != 0 && args.length != 1 && args.length != 3)
                throw new IllegalArgumentException();

            // if the command line argument is supplied
            if (args.length >= 1) {
                // parse debug
                if (args[0].equals("true")) {
                    debug = true;
                }
                else if (args[0].equals("false")) {
                    debug = false;
                }
                else {
                    throw new IllegalArgumentException();
                }
                // parse port
                if (args.length == 3) {
                    port = Integer.parseInt(args[1]);
                }
            }
        } catch (IllegalArgumentException e) {
            System.err.println("usage: Server [DEBUG [-p PORT]]");
            return;
        }

        try {
            //System.out.println("Start a guichat server running on port " + port);
            runServer(debug, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void runServer(boolean debug, int port) throws IOException
    {
        Server server = new Server(debug, port);
        server.serve();
    }

    // Replies
    private final String LOGIN = "login %s";
    private final String BYE = "bye";
    private final String ADDUSER = "adduser %s";
    private final String DELUSER = "deluser %s";
    private final String CREATE = "create %d %s";
    private final String HISTORY = "history %d %s";
    private final String LEAVE = "leave %d %s";
    private final String SAY = "say %s %d %s %s";
    // error type message
    private final String ERROR = "error %d %s";

    // Error type
    private final int USERNAME_TAKEN = 3;
    private final int CONVERSATION_NOT_EXIST = 4;
    private final int USER_NOT_EXIST = 6;
    private final int USER_HAS_NO_ACCESS = 9;
    private final int USER_ALREADY_IN =10;

    public synchronized void handleCreate(int id, String username, String input) throws IOException {
        if(allConversations.containsKey(id)) {
            if(allConversations.get(id).isActive(username))
                sendMessage(username, String.format(ERROR, USER_ALREADY_IN, input));
            else {
                allConversations.get(id).addUser(username);
                broadcast(id, String.format(CREATE, id, username));
            }
        }
        else{
            ArrayList<String> user = new ArrayList<String>();
            user.add(username);
            allConversations.put(id, new Conversation(id, user));
            sendMessage(username, String.format(CREATE, id, username));
        }
    }

    public synchronized void handleGethistory(int id, String username, String input) throws IOException {
        if(allConversations.containsKey(id)) {
            if(allConversations.get(id).isContained(username)){
                String history = allConversations.get(id).getHistory();
                sendMessage(username, String.format(HISTORY, id, history));
            }
            else
                sendMessage(username, String.format(ERROR, USER_HAS_NO_ACCESS, input));
        }
        else
            sendMessage(username, String.format(ERROR, CONVERSATION_NOT_EXIST, input));
    }

    public synchronized boolean handleLogout(String username, String input) throws IOException {
        //System.out.println(username);
        if(activeUsers.containsKey(username)) {
            //printUsers();
            sendMessage(username, BYE);
            activeUsers.remove(username);
            System.out.println("After removed");
            //printUsers();
            // iterate through all the active conversations and remove the user from each conversation
            Iterator<Integer> intArray = allConversations.keySet().iterator();

            while(intArray.hasNext()) {
                int id = intArray.next();
                // check whether user is contained and delete
                allConversations.get(id).goOffline(username);
            }
            broadcast(String.format(DELUSER, username));
            return true;
        }
        sendMessage(username, String.format(ERROR, USER_NOT_EXIST, input));
        return false;
    }

    public synchronized void handleLeave(int id, String leaver, String input) throws IOException {
        if  (allConversations.containsKey(id)) {
            if(!allConversations.get(id).isActive(leaver))
                sendMessage(leaver, String.format(ERROR, USER_NOT_EXIST, input));
            else {
                broadcast(id, String.format(LEAVE, id, leaver));
                allConversations.get(id).goOffline(leaver);
            }
        }
        else
            sendMessage(leaver, String.format(ERROR, CONVERSATION_NOT_EXIST, input));

    }

    public synchronized boolean handleLogin(String username, String input, Socket socket) throws IOException {
        if(!activeUsers.containsKey(username)) {
            broadcast(String.format(ADDUSER, username));
            activeUsers.put(username, socket);
            sendMessage(socket, String.format(LOGIN, username));
            return false;
        }
        else{
            sendMessage(socket, String.format(ERROR, USERNAME_TAKEN, input));
            return true;
        }
    }

    public synchronized void handleSay(String username, int id, String msg, String input) throws IOException {
        if(allConversations.containsKey(id)) {
            //if the username tries to send a message to a chatroom that he's not currently in
            if(!isActiveIn(id, username))
                sendMessage(username, String.format(ERROR, USER_NOT_EXIST, input));
            else{
                String time = Time.main();
                String message = String.format(SAY, time, id, username, msg);
                String hismessage = "[" + time + "] " + username + " says " + msg;
                allConversations.get(id).addChatHistory(hismessage);
                broadcast(id, message);
            }
        }
        else
            sendMessage(username, String.format(ERROR, CONVERSATION_NOT_EXIST, input));
    }
    public synchronized void printUsers() {
        StringBuffer au = new StringBuffer();
        for (String u: activeUsers.keySet()) {
            au.append(u);
        }
        System.out.println(au.toString());
    }
}

