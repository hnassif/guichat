
package client;

import gui.LoginGUI;
import gui.MainGUI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import server.Encoding;

public class Model {

    private int currentView;
    private final int LOGIN = 0;
    private final int MAIN = 1;

    private final Listener listener;
    private LoginGUI login;
    private MainGUI main;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private final String username;

    public Model(String username, LoginGUI login) throws IOException {
        this.username = username;
        this.currentView = 0;
        this.login = login;
        this.main = null;

        this.listener = new Listener(this);
        this.socket = null;
    }

    public Model(String username, Socket socket, LoginGUI login) throws IOException {
        this.username = username;
        this.currentView = 0;
        this.login = login;
        this.main = null;

        this.listener = new Listener(this);
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), /* autoFlush */ true);
    }


    public synchronized void sendRequest(String req) throws IOException {
        System.out.println("CLIENT sent " + req);
        out.println(req); // = in.readln()
    }

    public synchronized String readReply() throws IOException {
        return readNonEmptyLine(in); // = in.readln()
    }

    /**
     * Connect to the server on the specified host and port
     * Require the unconnected socket
     *
     * @param ip
     * @param port
     * @return Socket that connects to a given ip and port
     * @throws UnknownHostException
     * @throws IOException
     */
    public synchronized void connect(String ip, int port) throws UnknownHostException, IOException {
        /*  
         * final int MAX_ATTEMPTS = 10;              
         * int attempts = 0;
            do {
                try {
                    socket = new Socket(ipVal, port);
                } catch (ConnectException ce) {
                    try {
                        if (++attempts > MAX_ATTEMPTS)
                            throw new IOException("Exceeded max connection attempts", ce);
                        Thread.sleep(300);
                    } catch (InterruptedException ie) {
                        throw new IOException("Unexpected InterruptedException", ie);
                    }
                    catch (UnknownHostException he) {
                        throw new IOException("Connection Error! Host unknown!", he);
                    }
                }
            } while (socket == null);
            socket.setSoTimeout(4000);

         */              
        socket = new Socket(ip, port);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), /* autoFlush */ true);
        listener.start();
    }

    public synchronized void disconnect() throws IOException {
        if (isConnected()) {
            socket.close();
            socket = null;
        }
    }



    public synchronized void handleWelcome() throws IOException {
        if(currentView == LOGIN) {
            sendRequest("login " + username);
        }
    }

    public synchronized void handleLogin(String username) throws IOException {
        if(currentView == LOGIN) {
            login.login(username);
            currentView = MAIN;
        }
    }
    public synchronized void handleAdduser(String name) {
        if(currentView == MAIN) {
            main.addUser(name);
        }
    }

    public synchronized void handleDeluser(String name) {
        if(currentView == MAIN) {
            main.deleteUser(name);
        }
    }

    public synchronized void handleCreate(int id, String name) throws IOException {
        if(currentView == MAIN) {
            main.create(id, name);
        }
    }

    public synchronized void handleHistory(int id, String msg) {
        if(currentView == MAIN) {
            main.displayHistory(id, msg);
        }
    }

    public synchronized void handleLeave(int id, String name) {
        if(currentView == MAIN) {
            main.leave(id, name);
        }

    }

    public synchronized void handleSay(String time, int id, String username,
            String msg) {
        if(currentView == MAIN) {
            main.say(time, id, username, msg);
        }

    }


    public synchronized void handleBye() {
        if(currentView == MAIN) {
            main.backToLogin();
            currentView = LOGIN;
        }
    }

    private final int INVALID_COMMAND = 0; // somthing's wrong
    private final int MULTIPLE_LOGINS = 1; // somthing's wrong
    private final int NOT_LOGIN = 2; // Something wrong
    private final int USERNAME_TAKEN = 3;
    private final int CONVERSATION_NOT_EXIST = 4;
    private final int INVALID_CONVERSATIONID = 5;
    private final int USER_NOT_EXIST = 6;
    private final int DIFFERENT_USERID = 8; // Something wrong
    private final int USER_HAS_NO_ACCESS = 9;
    private final int USER_ALREADY_IN =10;
    private final int UNKNOWN = 11; // Something wrong


    public synchronized void handleError(int code, String error) {
        if(currentView == LOGIN) {
            switch (code) {
            case INVALID_COMMAND: 
                login.displayError("Invalid command");
                break;
            case MULTIPLE_LOGINS: 
                login.displayError("Multiple logins");
                break;
            case NOT_LOGIN: 
                login.displayError("Not logged in");
                break;
            case USERNAME_TAKEN:  
                login.displayError("Username taken");
                break;
            case USER_ALREADY_IN:
                login.displayError("Already logged in");
                break;
            case UNKNOWN:
                login.displayError("Unknown error code: " + code);
                break;
            default:
                login.displayError("Bug in model: " + code) ;
                break;
            }
        }
        else if(currentView == MAIN) {
            switch (code) {
            case INVALID_COMMAND: 
                main.displayError("Invalid command");
                break;
            case CONVERSATION_NOT_EXIST: 
                main.displayError("CONVERSATION_NOT_EXIST");
                break;
            case INVALID_CONVERSATIONID: 
                main.displayError("INVALID_CONVERSATIONID");
                break;
            case USER_NOT_EXIST:  
                main.displayError("USER_NOT_EXIST");
                break;
            case DIFFERENT_USERID:
                main.displayError("DIFFERENT_USERID");
                break;
            case USER_HAS_NO_ACCESS:
                main.displayError("USER_HAS_NO_ACCESS");
                break;

            case UNKNOWN:
                main.displayError("Unknown error code: " + code);
                break;
            default:
                main.displayError("Bug in model: " + code) ;
                break;
            }
        }

        /*
    }
        switch (code) {
        case INVALID_COMMAND:  monthString = "January";
                 break;
        case MULTIPLE_LOGINS:  monthString = "February";
                 break;
        case NOT_LOGIN:  monthString = "March";
                 break;
        case USERNAME_TAKEN:  monthString = "April";
                 break;
        case CONVERSATION_NOT_EXIST:  monthString = "May";
                 break;
        case INVALID_CONVERSATIONID:  monthString = "June";
                 break;
        case USER_NOT_EXIST:  monthString = "July";
                 break;
        case DIFFERENT_USERID:  monthString = "August";
                 break;
        case USER_HAS_NO_ACCESS:  monthString = "September";
                 break;
        case USER_ALREADY_IN: monthString = "October";
                 break;
        case UNKNOWN: monthString = "November";
                 break;
        case 12: monthString = "December";
                 break;
        default: monthString = "Invalid month";
                 break;
    }*/

    }


    public synchronized boolean isConnected(){
        return !(socket == null);
    }

    public synchronized String getUsername(){
        return this.username;
    }

    public synchronized static String readNonEmptyLine(BufferedReader in) throws IOException {
        while (true) {
            String line = in.readLine();
            if (line == null || !line.equals("")) {
                System.out.println("ERROR! CLIENT got " + line);
                return Encoding.decode(line);
            }
        }
    }

    public synchronized Socket getSocket() {
        return socket;
    }

    public synchronized void handleGetusers(String[] tokens) {
        if (currentView == MAIN) {
            main.getUsers(tokens);
        }

    }

    public synchronized void switchToMain(MainGUI gui) {
        currentView = MAIN;
        this.main = gui;
    }
    public synchronized void switchToMain() {
        currentView = MAIN;
    }
    public synchronized void switchToLogin() {
        currentView = LOGIN;
    }
}
