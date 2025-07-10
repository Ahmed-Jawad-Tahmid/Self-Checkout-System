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
 * Enables the customer to select auditory interaction with the system.
 */
public class SelectAudioIO {
    private final Scanner scanner;

    public SelectAudioIO(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Asks the customer if they wish to use auditory interaction.
     */
    public void selectAudioIO() {
        System.out.println("Do you want to use voice interaction? (Y for yes, N for no):");
        String choice = scanner.nextLine().trim();

        if ("Y".equalsIgnoreCase(choice)) {
            System.out.println("Voice interaction enabled. You can now speak to the system.");
            // Here, additional logic could be implemented to switch the system to auditory mode.
        } else if ("N".equalsIgnoreCase(choice)) {
            System.out.println("Continuing with visual/touch interaction.");
            // Keeps or switches back to the default interaction mode.
        } else {
            System.out.println("Invalid choice. Assuming no voice interaction.");
            // Default to no voice interaction on invalid input.
        }
    }
}
