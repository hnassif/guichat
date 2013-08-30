package tests;

public class GuiTest {
	
/**
 * @category no_didit
 * GUI Testing
Before starting GUI tests, we will look aesthetically to make sure that no elements overlap or coincide.
In testing the GUI, we will click every JButton and make sure that they trigger  the appropriate eventListeners. 
We will also make sure that these eventListeners correspond to the right following actions. 
Finally, we will make sure that each screen has either a logout, close, or back button in order to allow for users to exit or go back a step. 
We decided not to allow for window resizability for the login and lobby screens.


Actions:
We will make sure that appropriate logins work and inappropriate logins don’t (i.e. test an invalid username, invalid port, invalid ip, and taken username). We should clear the input JTextField and then return a warning message. 

When a user opens multiple chat windows, we will make sure that the newest chat window does not overwrite the existing chat window. It will create a new window.

Sequence-oriented GUI tests (tested manually):
•	Login with invalid username (return a warning)
•	Login, then force close, then login again
•	Login, create a chatroom, force close the chatroom, and then create the same chatroom
•	Login, create chatrooms, logout (which should disable saying anything in any chatroom), then login with same username and then create the same chatrooms
•	Login, create chatroom, get history of chatroom
•	Login, create chatroom, leave chatroom, get history of chatroom


 */
}
