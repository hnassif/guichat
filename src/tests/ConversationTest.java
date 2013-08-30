package tests;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import server.*;

/**
 * The test strategy for the Conversation class is relatively simple: we will test every method that is callable by a conversation.
 * This includes adding a user, getting the user list, making a user go offline, checking if a user if offline, checking if a user 
 * is active, checking if a user is contained, initializing a conversation with a two input constructor, initializing a conversation
 * with a three-input constructor, and getting the id of a conversation.
 *
 */

public class ConversationTest {
	
	/**
	 * tests adding a user to the list of users in a conversation. 
	 * Also tests the two-input constructor
	 * @throws IOException
	 */
	@Test
	public void testAddUser() throws IOException {
		ArrayList<String> users = new ArrayList<String>();
		users.add("USER_A");
		users.add("USER_B");
		Conversation conv = new Conversation(3, users);
		conv.addUser("USER_C");
		ArrayList<String> expectedList = new ArrayList<String>();
		expectedList.addAll(users);
		assertEquals(conv.getUserList(),expectedList);
	}
	
	/**
	 * tests goOffline(), isOffline(), isActive(), and isContained() methods
	 * @throws IOException
	 */
	@Test
	public void testStatusMethods() throws IOException {
		ArrayList<String> users = new ArrayList<String>();
		users.add("USER_A");
		users.add("USER_B");
		Conversation conv = new Conversation(3, users);
		assertTrue(conv.isActive("USER_B"));
		assertTrue(conv.isActive("USER_A"));
		conv.goOffline("USER_A");
		assertTrue(conv.isActive("USER_B"));
		assertFalse(conv.isActive("USER_A"));
		assertTrue(conv.isOffline("USER_A"));
		assertFalse(conv.isOffline("USER_B"));
		assertTrue(conv.isContained("USER_A"));
		assertTrue(conv.isContained("USER_B"));
	}
	
	/**
	 * tests the getID() method
	 * @throws IOException
	 */
	
	@Test
	public void testGetID() throws IOException {
		ArrayList<String> users = new ArrayList<String>();
		users.add("USER_A");
		users.add("USER_B");
		Conversation conv = new Conversation(3, users);
		assertEquals(conv.getID(),3);
	}
}
