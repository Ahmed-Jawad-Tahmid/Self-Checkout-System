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

package com.thelocalmarketplace.software;
import java.util.Scanner;

/**
 * This method is the start screen of the self checkout system
 * @author Ronaar Qureshi, 
 * @UCID 30147045
 * @group Steel
 */
public class ShowScreen {

    private boolean sessionAlreadyActive=false;// This is flag that is raised to show that a session has been activated
    private final Scanner scanner;
    private final SelectLanguage selectLanguage;
    private final SelectAudioIO selectAudioIO;

     public ShowScreen(Scanner scanner) {
        this.scanner = scanner;
        this.selectLanguage = new SelectLanguage(scanner);
        this.selectAudioIO = new SelectAudioIO(scanner);
    }
/**
 * This method is the process behind the start screen of the self checkout system.
 * This method uses a mock user interaction to demonstrate that a user is interacting with the system
 * @return A string that contains the message whether or not the system has been interacted with
 */
    public String startScreen() {
        if (isSessionAlreadyActive()) {
            return "ERROR! SESSION IS ALREADY ACTIVE FOR THE SYSTEM";
        } else {
            System.out.println("Enter 'Y' to start a session");
            String userInput = scanner.nextLine();

            if ("Y".equalsIgnoreCase(userInput)) {
                setSessionAlreadyActive(true);
                selectLanguage.selectLanguage();
                selectAudioIO.selectAudioIO();
                return "THE SYSTEM HAS NOW STARTED A SESSION! PLEASE SCAN YOUR ITEM!";
            } else {
                return "SESSION HAS NOT STARTED";
            }
        }
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ShowScreen screen = new ShowScreen(scanner);
        System.out.println(screen.startScreen());
    }

    public boolean isSessionAlreadyActive() {
        return sessionAlreadyActive;
    }

    public void setSessionAlreadyActive(boolean sessionAlreadyActive) {
        this.sessionAlreadyActive = sessionAlreadyActive;
    }
}


