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

import com.thelocalmarketplace.software.SelectLanguage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class SelectLanguageTest {

    private final ByteArrayOutputStream outputContent = new ByteArrayOutputStream();
    private final PrintStream originalOutput = System.out;
    private ByteArrayInputStream testInput;

    @Before
    public void setupIOStreams() {
        System.setOut(new PrintStream(outputContent));
    }

    @After
    public void resetIOStreams() {
        System.setOut(originalOutput);
        System.setIn(System.in);
    }

    private void simulateInput(String data) {
        testInput = new ByteArrayInputStream(data.getBytes());
        System.setIn(testInput);
    }

    @Test
    public void testCancellationOfLanguageSelection() {
        simulateInput("C\n");
        SelectLanguage languageSelector = new SelectLanguage(new Scanner(System.in));
        languageSelector.selectLanguage();
        assertTrue("Cancellation of language selection did not work as expected", outputContent.toString().contains("Language selection canceled. Continuing in default language: English"));
    }

    @Test
    public void testSelectionHandlingInvalidInput() {
        simulateInput("Invalid\n");
        SelectLanguage languageSelector = new SelectLanguage(new Scanner(System.in));
        languageSelector.selectLanguage();
        assertTrue("Invalid input was not handled correctly", outputContent.toString().contains("Invalid selection. Continuing in default language: English"));
    }

    @Test
    public void testLanguageSelectionForSpanish() {
        simulateInput("2\n");
        SelectLanguage languageSelector = new SelectLanguage(new Scanner(System.in));
        languageSelector.selectLanguage();
        assertTrue("Spanish was not selected correctly", outputContent.toString().contains("You have selected Spanish."));
    }

    @Test
    public void testLanguageSelectionForEnglish() {
        simulateInput("1\n");
        SelectLanguage languageSelector = new SelectLanguage(new Scanner(System.in));
        languageSelector.selectLanguage();
        assertTrue("English was not selected correctly", outputContent.toString().contains("You have selected English."));
    }
}
