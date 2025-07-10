package com.thelocalmarketplace.software.test.panelstest;

import static org.junit.Assert.assertEquals;

import javax.swing.*;


import com.thelocalmarketplace.software.CustomerView;
import com.thelocalmarketplace.software.panels.AddBagPanel;

import shopping.manager.ShoppingManager;

import org.junit.Test;


public class TestAddBagPanel {

    @Test
    public void testBagCountIncreases(){
    
        SwingUtilities.invokeLater(() -> {
            
            ShoppingManager manager = new ShoppingManager("Bronze");
            CustomerView view = new CustomerView(manager);

            
            AddBagPanel panel = new AddBagPanel();
            panel.setView(view);

            // create JFrame to host panel for test
            JFrame testFrame = new JFrame("Test Frame");
            testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            testFrame.add(panel);
            testFrame.pack();
            testFrame.setVisible(true);

            // get initial bag count
            int initialBagCount = manager.getBagCount();

            // simulate button click
            JButton addBagButton = panel.getAddBagButton();
            addBagButton.doClick();

            // check bag count after the click
            int finalBagCount = manager.getBagCount();

            // verify bag count increased by 1

            assertEquals("bag count should increase by 1 upon clicking add bag button", initialBagCount + 1, finalBagCount);

            
            testFrame.dispose();
        });
    }
}



