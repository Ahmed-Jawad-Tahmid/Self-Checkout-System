package com.thelocalmarketplace.software.test.panelstest;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import com.thelocalmarketplace.software.CustomerView;
import com.thelocalmarketplace.software.panels.IRLPanel;

import shopping.manager.ConfigCheckoutStation;
import shopping.manager.ShoppingManager;

import static org.junit.Assert.*;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.Currency;

import javax.swing.*;

public class TestIRLPanel {
    private JFrame frame;
    private IRLPanel panel;
    private CustomerView view; 
    private ShoppingManager manager;

    @Before
    public void setUp() {

        ConfigCheckoutStation config = new ConfigCheckoutStation();
        config.setCurrency("CAD");
            
        manager = new ShoppingManager("Bronze", config);
        view = new CustomerView(manager); 
        panel = new IRLPanel(view);
        frame = new JFrame("Test Frame");
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
        
    }

    // method to close dialog by simulating click on the "ok" button
    private void closeDialog() {
    
        SwingUtilities.invokeLater(() -> {
            Window[] windows = Window.getWindows();
            for (Window window : windows) {
                if (window instanceof JDialog) {
                    JDialog dialog = (JDialog) window;
                    if (dialog.getContentPane().getComponentCount() > 0 && dialog.getTitle().equals("Please add item to bagging area after its scanned")) { 
                    // find ok button, or simply dispose of dialog
                        JButton okButton = findButton(dialog, "OK"); 
                        if (okButton != null) {
                            okButton.doClick();
                        } else {
                            dialog.dispose(); 
                        }
                    }
                }
            }
        });
    }


    @Test
    public void addItemIncreasesCount() {
        
        JButton addButton = findButton(panel, "Add");
        assertNotNull("Add button should not be null.", addButton);

        int initialCount = panel.getAddedItemCount();
        addButton.doClick();

        int newCount = panel.getAddedItemCount();
        assertEquals("Adding an item should increase item count by 1.", initialCount + 1, newCount);

        closeDialog();
        
    }

    @Test
    public void removeItemDecreasesCount() {
        
        JButton addButton = findButton(panel, "Add");
        addButton.doClick(); 

        JButton removeButton = findButton(panel, "Remove");
        assertNotNull("Remove button should not be null.", removeButton);

        int initialCount = panel.getAddedItemCount();
        removeButton.doClick();

        int newCount = panel.getAddedItemCount();
        assertEquals("Removing an item should decrease item count by 1.", initialCount - 1, newCount);
        
     
    }

    @Test
    public void scanButtonIncreasesScannedItemCount() {
        JButton scanButton = findButton(panel, "Scan");
        assertNotNull("Scan button should not be null.", scanButton);

        int initialCount = panel.getScannedItemCount();
        scanButton.doClick();

        int newCount = panel.getScannedItemCount();
        assertEquals("Scanning an item should increase scanned item count by 1.", initialCount + 1, newCount);
    }

    private JButton findButton(Component component, String buttonText) {
        if (component instanceof JButton) {
            JButton button = (JButton) component;
            if (buttonText.equals(button.getText())) {
                return button;
            }
        } else if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                JButton foundButton = findButton(child, buttonText);
                if (foundButton != null) {
                    return foundButton;
                }
            }
        }
        return null;
    }
    

    @After
    public void tearDown() throws Exception {
        frame.dispose();
    }
}




