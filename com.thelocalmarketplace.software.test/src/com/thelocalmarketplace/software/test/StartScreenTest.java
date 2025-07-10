/*
Allan Kong, 30171801
Mark Cena, 30176936
Sarthak Singh, 30180318
Arjit Chitkara, 30169949
Kyle Ontiveros, 30193288
Ronaar Qureshi, 30147045
Sufian Tariq, 30179995
Ahmed Tahmid, 30175756
Labib Ahmed, 30194888
Ana DuCristea, 30170871
Kazi Badrul Arif, 30161466
Yazeed Badr, 30176535
Arshaum Hajinabi, 30189955
Juan Calvo Franco, 30192699 
Arpit Chitkara, 30170166
Saahim Salman, 30125256
Siddharth Sadhwani, 30194796
Farhan Labib, 30176224
Parushrut Dubey: 30196553
Tanjim Rahman, UCID: 30182328
Noelle Thundathil, 30115430 
 */

package com.thelocalmarketplace.software.test;
import com.thelocalmarketplace.software.ShowScreen;
import static org.junit.Assert.*;
import java.io.ByteArrayInputStream;
import java.util.Scanner;

/**
 * Test class for the ShowScreen class.
 *
 * This class contains unit tests for the ShowScreen class.
 * These tests verify the behavior of the ShowScreen class.
 */
public class StartScreenTest {
	
	/**
     * Sets up the ShowScreen instance for testing.
     *
     * @return A ShowScreen instance initialized for testing.
     */

	public ShowScreen setupShowScreen() {
		Scanner scanner = new Scanner(System.in);
	    ShowScreen check = new ShowScreen(scanner);
	    return check;
	}
	
	/** 
     * Verifies that the sessionAlreadyActive flag is false upon initialization of the ShowScreen class.
     *
     * @return void
     */
	
	@org.junit.Test
	public void testInitialState() {
	    ShowScreen check = setupShowScreen();
	    assertFalse("Session should not be active upon initialization", check.isSessionAlreadyActive());
	}
	
	/**
     * Tests whether the session starts successfully when the correct input is provided.
     *
     * @return void
     */
	


	/**
     * Tests whether the session does not start when an invalid input is provided.
     *
     * @return void
     */
	
	@org.junit.Test
	public void testSessionDoesNotStartWithInvalidInput() {
        // Simulate user input "N"
        String input = "N";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ShowScreen showScreen = setupShowScreen();
        
        assertEquals("SESSION HAS NOT STARTED", showScreen.startScreen());
        assertFalse(showScreen.isSessionAlreadyActive());
    }

	/**
     * Tests whether the correct error message appears when the session is already active.
     *
     * @return void
     */
	
    @org.junit.Test
    public void showScreen() {
        ShowScreen showScreen = setupShowScreen();
        showScreen.setSessionAlreadyActive(true);
        String actual = showScreen.startScreen();
        assertEquals("ERROR! SESSION IS ALREADY ACTIVE FOR THE SYSTEM", actual);

    }  
}
