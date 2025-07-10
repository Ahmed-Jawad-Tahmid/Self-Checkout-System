package com.thelocalmarketplace.software.panels;
import javax.swing.*;

public class TestPanel extends JPanelRefreshable implements IPanel{
	
	public TestPanel() {
		setLayout(null);
		
		// create the main text of the page
		JLabel label = new JLabel("Session");
		label.setBounds(50, 50, 150, 20);
		add(label);
	}

}
