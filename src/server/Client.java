
package server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;



/**
 * GUI chat client runner.
 */

public class Client extends Thread {

    private Server server;
    private String username;
    private Socket socket;
    private PrintWriter out;

    public Client(Socket socket, Server server) throws IOException{
        this.server = server;
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), /* autoFlush */ true);
    }


    private void handleConnection(Socket socket) throws IOException, InterruptedException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        boolean terminate;

        try {
            String helloMessage = String.format(WELCOME, 10);
            out.println(helloMessage);
            for (String line = in.readLine().trim(); !(line == null); line = (in.readLine()).trim()) {
                terminate = handleRequest(line);
                if (terminate) {
                    break;
                }
            }
        } finally {
            out.close();
            in.close();
            socket.close();

        }
    }

    private static final String regex = "(login .+)|" +
            "(logout)|" +
            "(create [a-zA-Z0-9]+ [a-zA-Z0-9_]+)|" +
            "(gethistory [a-zA-Z0-9]+ [a-zA-Z0-9_]+)|" +
            "(getusers)|" +
            "(leave [0-9]+ [a-zA-Z0-9_]+)|" +
            "(say [0-9]+ .+)";

    // Replies
    private final String WELCOME = "welcome";
    private final String GETUSERS = "getusers%s";
    // error type message
    private final String ERROR = "error %d %s";

    // Error type
    private final int INVALID_COMMAND = 0; // somthing's wrong
    private final int MULTIPLE_LOGINS = 1; // somthing's wrong
    private final int NOT_LOGIN = 2; // Something wrong
    private final int INVALID_CONVERSATIONID = 5;
    private final int DIFFERENT_USERID = 8; // Something wrong
    private final int UNKNOWN = 11; // Something wrong



    private boolean handleRequest(String input) throws NumberFormatException, IOException {
        System.out.println(input);
       
        //if the command is different from the allowed commands above
        if(!input.toLowerCase().matches(regex)) {
            server.sendMessage(username, String.format(ERROR, INVALID_COMMAND, input));
            return false;
        }
       
        if((!input.toLowerCase().startsWith("login")) && username == null) {
            server.sendMessage(username, String.format(ERROR, NOT_LOGIN, input));
            return false;
        }

        if (!input.toLowerCase().startsWith("say")){
            String[] tokens = input.split(" "); 
            String command = tokens[0].toLowerCase();

            if (tokens.length == 3) { // create, gethistory, leave
                if(!tokens[1].matches("[0-9]+")){
                    server.sendMessage(username, String.format(ERROR, INVALID_CONVERSATIONID, input));
                    return false;
                }
               
                int id = Integer.parseInt(tokens[1]);
                String username = tokens[2];

                if(!username.equals(this.username)) {
                    server.sendMessage(this.username, String.format(ERROR, DIFFERENT_USERID, input));
                    return false;
                }
               
                //Create a chat room
                if(command.equals("create")) {
                   server.handleCreate(id, username, input);
                   return false;
                }
                //Get chat history.
                else if(command.equals("gethistory")) {
                    server.handleGethistory(id, username, input);
                    return false;
                }
                //Leave the room
                else if(command.equals("leave")) {
                    server.handleLeave(id, username, input);
                    return false;
                }
            }
            else if (command.equals("login")) {
                if(!(username == null)) {
                    //if a user tries to log in on the same socket multiple times, don't let him
                    server.sendMessage(username, String.format(ERROR, MULTIPLE_LOGINS, input));
                    return false;
                }
                else {
                    if(tokens.length > 2 || !tokens[1].matches("[A-Za-z0-9_]+{6,18}")) {
                        System.out.println("Error: invalid username");
                        socket.close();
                    }
                    else {
                        username = tokens[1];
                        return server.handleLogin(username, input, socket);
                    }
                }
            }
            else if(command.equals("logout")) {
                String name = username;
                username = null;
                return server.handleLogout(name, input); //remove the previous username from the hashmap of active users
            }
            //getUser
            else if(command.equals("getusers")) {
                server.sendMessage(username, String.format(GETUSERS, server.allActiveUsers()));
                return false;
            }
        }
        else { // say
            //This will avoid the situation where in the message there are spaces
            String[] tokens = input.split(" ", 3);
            int id = Integer.parseInt(tokens[1]);
            String message = tokens[2];

            server.handleSay(username, id, message, input);
            return false;
        }
        System.out.println(String.format(ERROR, UNKNOWN, input));
        return false;
    }


    /**
     * Start a GUI chat client.
     */
    public static void main(String[] args) {
        // YOUR CODE HERE
        // It is not required (or recommended) to implement the client in
        // this runner class.
    }

    @Override
    public void run() {
        try {
            handleConnection(socket);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
