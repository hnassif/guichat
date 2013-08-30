
package server;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Conversation is thread safe because no more than thread can access any of its
 * variable at the same time by either read or write.
 *
 * conversationID is immutable.
 *
 * usersList and chatHistory are private to each Conversation object so they cannot
 * be modified or read outside the class.
 * Moreover, once they got accessed by a method, they are always protected by they own locks.
 *
 */

public class Conversation{
    private ArrayList<String> activeUsers;
    private ArrayList<String> offlineUsers;
    private final int conversationID;
    private StringBuffer sb;
  
    /**
     * constructor that can take in a convID and a userList
     * @param conversationID
     * @param usersList
     * @throws IOException
     */
    public Conversation(int conversationID, ArrayList<String> usersList) throws IOException{
        this.conversationID = conversationID;
        this.activeUsers = usersList;
        this.offlineUsers = new ArrayList<String> ();
        this.sb = new StringBuffer();

    }
  
    /**
     * constructor that can take in a convID and a userList and chatHistory
     * @param conversationID
     * @param usersList
     * @param chatHistory
     * @throws IOException
     */
  
    public Conversation(int conversationID, ArrayList<String> usersList, ArrayList<String> chatHistory) throws IOException{
        this.conversationID = conversationID;
        this.activeUsers = usersList;
        this.offlineUsers = new ArrayList<String> ();
        this.sb = new StringBuffer();
    }
  
    /**
     * @return the list of active users in the conversation (accessor)
     */
    public ArrayList<String> getUserList(){
        synchronized(activeUsers) {
            return this.activeUsers;
        }
    }
  
    /**
     * adds a new user to the conversation's active users
     * @param newUser
     */
    public void addUser(String newUser){
        synchronized(activeUsers) {
            activeUsers.add(newUser);
        }
    }
  
    /**
     * changes a user's status from active in the conversation to offline when he leaves
     * @param user
     */
    public void goOffline(String user){
        synchronized(activeUsers) {
            synchronized(offlineUsers) {
                if(activeUsers.contains(user)) {
                    activeUsers.remove(user);
                    offlineUsers.add(user);
                }
            }
        }
    }
  
    /**
     *
     * @param user
     * @return whether the user is offline or not
     */
  
    public boolean isOffline(String user) {
        synchronized(offlineUsers) {
            return offlineUsers.contains(user);
        }
    }
  
    /**
     *
     * @param user
     * @return whether the user is active or not
     */
  
    public boolean isActive(String user) {
        synchronized(activeUsers) {
            return activeUsers.contains(user);
        }
    }
  
    /**
     *
     * @param user
     * @return whether the user is active or was once active (and is now offline)
     * this will be used in getting histories
     */
  
    public boolean isContained(String user) {
        synchronized(activeUsers) {
            synchronized(offlineUsers) {
                return activeUsers.contains(user) || offlineUsers.contains(user);
            }
        }
    }

    /**
     * @return history of the conversation (accessor)
     */
  
    public String getHistory(){
        synchronized(sb) {
            return sb.toString();
        }
    }
  
    /**
     * adds to the chat history of a conversation
     * @param string
     */
  
    public void addChatHistory(String string){
        synchronized(sb) {
            this.sb.append(string + "\n");
        }
    }
  
    /**
     * @return the conversationID of a conversation (accessor)
     */
    public int getID(){
        return conversationID;
    }
  
}

