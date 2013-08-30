
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import server.Encoding;

/**
 * listens to changes in the client through a socket and then parses them accordingly by calling the model
 */

public class Listener extends Thread {
    private final Model model;
    public Listener(Model model) throws UnknownHostException, IOException {
        this.model = model;
    }

    @Override
    public void run() {
        try {
            connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * handles a connection to a socket by listening for changes in its input stream
     * @throws IOException
     */

    private void connect() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(model.getSocket().getInputStream()));
        try {
            for(String re = (in.readLine().trim()); re != null; re = (in.readLine().trim())) {
                String response = Encoding.decode(re);
                handleResponse(response);
            }
        }
        catch(NullPointerException e) {
        }
        finally {
            in.close();
            model.disconnect();
        }
    }

    //all the possible regex commands
    private static final String regex = "(welcome)|" +
            "(login [a-zA-Z0-9_]+)|" +

            "(bye)|" +
            "(adduser [a-zA-Z0-9_]+)|" +
            "(deluser [a-zA-Z0-9_]+)|" +
            "(create [0-9]+ [a-zA-Z0-9_]+)|" +
            "(history [0-9]+ .+)|" +
            "(getusers( [a-zA-Z0-9_]+)+)|" +
            "(leave [0-9]+ [a-zA-Z0-9_]+)|" +
            "(say [0-2][0-9]:[0-5][0-9]:[0-5][0-9] [0-9]+ [a-zA-Z0-9_]+ .+)|" +

            "(error [0-9]+ .+)";
   
    private static final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);

    /**
     * handleResponse will take in an input and match it with a regex and then give it to the Model to handle
     * @param input
     * @throws IOException
     */
    private void handleResponse(String input) throws IOException {
        System.out.println("LISTENER "+ input);
        Matcher matcher = pattern.matcher(input);
        if(!matcher.matches()) {
            System.out.println("Bug in server message/Listener regex");
            return;
        }
        /*
        String[] tokens = input.split(" ", 3);
        int id = Integer.parseInt(tokens[1]);
        String msg = tokens[2];
         */

        // Reorganize/reimplement all the conditions
        // history, say, and error need to be split with limited number of time
        // you an use startsWith to check first
      
        String[] tokens = input.split(" ");
      
        if(tokens[0].matches("welcome")){
            model.handleWelcome();
        }
      
        else if(tokens[0].matches("login")){
            String username = tokens[1];
            model.handleLogin(username);
        }
      
        else if(tokens[0].matches("bye")){
            model.handleBye();
        }

        else if(tokens[0].matches("adduser")){
            String username = tokens[1];
            model.handleAdduser(username);
        }
      
        else if(tokens[0].matches("deluser")){
            String username = tokens[1];
            model.handleDeluser(username);
        }
      
        else if(tokens[0].matches("create")){
            int ID = Integer.parseInt(tokens[1]);
            String username = tokens[2];
            model.handleCreate(ID, username);
        }
      
        else if(tokens[0].matches("history")) {
                String[] histokens = input.split(" ", 3);
                int ID = Integer.parseInt(histokens[1]);
                String message = histokens[2];
                model.handleHistory(ID, message);
        }
      
        else if(tokens[0].matches("leave")) {
            int ID = Integer.parseInt(tokens[1]);
            String username = tokens[2];
            model.handleLeave(ID, username);
        }
      
        else if(tokens[0].matches("say")) {
            String[] saytokens = input.split(" ", 5);
            String time = saytokens[1];
            int ID = Integer.parseInt(saytokens[2]);
            String username = saytokens[3];
            String message = saytokens[4];
          
            /*
             * returns everything after the first four words
             * ^ - first char of string only
             * \s - whitespace
             * \\S - all non-whitespace characters
             * {1,4} - replace first 4 spaces
             */
          
            model.handleSay(time, ID, username, message);
        }
      
        else if(tokens[0].matches("getusers")) {
            //remember to kill the first few tokens
            model.handleGetusers(tokens);
        }
      
        else {
            String[] errortokens = input.split(" ", 3);
            // error_code, error_message
            System.out.println("#############" + errortokens[1]);
            int errorNum = Integer.parseInt(errortokens[1]);
            // remember to delete the first two tokens in the arraylist
            model.handleError(errorNum, errortokens[2]);
        }
    }
}
