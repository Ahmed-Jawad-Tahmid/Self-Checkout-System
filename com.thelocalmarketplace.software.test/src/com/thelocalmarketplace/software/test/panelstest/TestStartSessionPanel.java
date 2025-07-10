package com.thelocalmarketplace.software.test.panelstest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import javax.swing.*;
import java.awt.*;


import com.thelocalmarketplace.software.panels.StartSessionPanel;
import com.thelocalmarketplace.software.CustomerView;
import shopping.manager.ShoppingManager;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.thelocalmarketplace.software.CustomerView;
import shopping.manager.ShoppingManager;

public class TestStartSessionPanel {

    private CustomerView view;

    @Before
    public void setUp() {
        ShoppingManager manager = new ShoppingManager("Bronze");
        view = new CustomerView(manager);
    }

    @Test
    public void testStartButtonSwitchesPanel() {
        // Simulate the action the Start button would trigger
        view.switchToPanel("CartPanel");
        
        // Verify the panel switched to CartPanel
        assertEquals("The current panel should be CartPanel", "CartPanel", view.getCurrentPanelId());
    }
}



