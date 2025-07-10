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

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Allows the customer to select a language for system interaction.
 */
public class SelectLanguage {
    private final Scanner scanner;
    private final Map<String, String> languages;
    private String defaultLanguage = "English";

    public SelectLanguage(Scanner scanner) {
        this.scanner = scanner;
        // Initialize available languages. This can be expanded as needed.
        this.languages = new HashMap<>();
        this.languages.put("1", "English");
        this.languages.put("2", "Spanish");
        this.languages.put("3", "French");
        // Additional languages can be added here.
    }
    
    //

    /**
     * Displays language options and processes customer selection.
     */
    public void selectLanguage() {
        System.out.println("Please select a language (or press 'C' to cancel):");
        // Display language options
        for (Map.Entry<String, String> entry : languages.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("C: Cancel");

        String choice = scanner.nextLine().trim();

        if ("C".equalsIgnoreCase(choice)) {
            System.out.println("Language selection canceled. Continuing in default language: " + defaultLanguage);
            // Continue with default or previously selected language.
            return;
        }

        String selectedLanguage = languages.get(choice);
        if (selectedLanguage != null) {
            System.out.println("You have selected " + selectedLanguage + ".");
            // System is now ready for further interaction in the selected language.
        } else {
            System.out.println("Invalid selection. Continuing in default language: " + defaultLanguage);
            // Handle the case where an invalid option is selected.
        }
    }

    // Optional setters and getters for defaultLanguage could be added here,
    // allowing system configuration to set the default language on installation.

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SelectLanguage languageSelector = new SelectLanguage(scanner);
        languageSelector.selectLanguage();
    }
}
