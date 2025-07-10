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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.software.SelectAudioIO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class SelectAudioIOTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private ByteArrayInputStream inContent;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(System.in);
    }

    private void provideInput(String data) {
        inContent = new ByteArrayInputStream(data.getBytes());
        System.setIn(inContent);
    }

    @Test
    public void testVoiceInteractionEnabled() {
        provideInput("Y\n");
        SelectAudioIO selectAudioIO = new SelectAudioIO(new Scanner(System.in));
        selectAudioIO.selectAudioIO();
        assertTrue("Voice interaction message not found", outContent.toString().contains("Voice interaction enabled. You can now speak to the system."));
    }

    @Test
    public void testVisualTouchInteractionContinued() {
        provideInput("N\n");
        SelectAudioIO selectAudioIO = new SelectAudioIO(new Scanner(System.in));
        selectAudioIO.selectAudioIO();
        assertTrue("Continuing with visual/touch interaction message not found", outContent.toString().contains("Continuing with visual/touch interaction."));
    }

    @Test
    public void testInvalidChoiceAssumesNo() {
        provideInput("Invalid\n");
        SelectAudioIO selectAudioIO = new SelectAudioIO(new Scanner(System.in));
        selectAudioIO.selectAudioIO();
        assertTrue("Invalid choice handling message not found", outContent.toString().contains("Invalid choice. Assuming no voice interaction."));
    }
}
