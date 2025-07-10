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

package com.thelocalmarketplace.software.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.thelocalmarketplace.software.AttendantView;
import com.thelocalmarketplace.software.CustomerView;


public class DashboardPanel extends JPanelRefreshable{
	private CustomerView[] customerViews;
	private JPanel loginPanel;
	private CustomerView currentView;
	private AttendantView view;
	
	// Messages for errors and such
	JLabel messages = new JLabel("Latest notification will appear here");
	int sameMessageCount = 1;
	
	public DashboardPanel(CustomerView[] customerViews, JPanel loginPanel, AttendantView view) {
		this.customerViews = customerViews;
		this.loginPanel = loginPanel;
		this.view = view;
		
		setLayout(new GridLayout(2, 2));
		
		// left panel
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		JLabel leftTitle = new JLabel("Station overview");
		leftPanel.add(leftTitle);
		
		
		
		// functionality for controlling customer views
		String[] stationNames = {"Bronze", "Silver", "Gold"};
		boolean[] isVisible = new boolean[3];
		for (int i = 0; i < customerViews.length; i++) {
			JButton button = new JButton(stationNames[i] + " Station");
			int index = i;
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (isVisible[index]) {
						customerViews[index].setVisible(false);
						isVisible[index] = false;
						currentView = null;
					} else {
						currentView = customerViews[index];
						customerViews[index].setVisible(true);
						isVisible[index] = true;
					}
					CartPanel.newInstance();
					CartPanel.setView(currentView);
					
				}
			});
			leftPanel.add(button);
		}
		
		// Sign-out functionality
		JButton signOutButton = new JButton("Sign Out");
		signOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (CustomerView view : customerViews) {
					view.setVisible(false);
				}
				DashboardPanel.this.setVisible(false);
				loginPanel.setVisible(true);
				currentView = null;
			}
		});
		leftPanel.add(signOutButton);
		add(leftPanel);
		
		// Right panel
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		JLabel rightTitle = new JLabel("Control panel");
		rightPanel.add(rightTitle);
		
		String[] buttonNames = {"Disable/Enable", "Lookup Product", "Remove Product",  "Refill BankNotes", "Refill Coins", "Refill Ink", "Refill Paper", "Approve Override", "Approve Bulky Item", "Unapprove Bulky Item"};
		for (String buttonName : buttonNames) {
			JButton button = new JButton(buttonName);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// different actions for each button
					switch (buttonName) {
						case "Disable/Enable":
							// block if not already blocked, unblock otherwise
							if (currentView.getCurrentPanel().getClass().getSimpleName().equals("BlockedPanel")) {
								currentView.switchToPanel(currentView.getPreviousPanel());
							} else {
								currentView.switchToPanel("BlockedPanel"	);
							}
							break;
						case "Refill BankNotes":
							if (currentView != null) {
								currentView.getManager().getRefillLogic().refillAllBanknoteDispensersMax();
								updateMessage("All banknote dispensers are now full", false);
							}
							else {
								updateMessage("Select a station to visualize first", true);
								
							}
							break;
						case "Refill Coins":
							if (currentView != null) {
								currentView.getManager().getRefillLogic().refillAllCoinDispensersMax();
								updateMessage("All coin dispensers are now full", false);
							}
							else {
								updateMessage("Select a station to visualize first", true);
							}
							break;
						case "Refill Ink":
							if (currentView != null) {
								currentView.getManager().getRefillLogic().refillPrinterInk();
								updateMessage("The ink is now full", false);

							}
							else {
								updateMessage("Select a station to visualize first", true);
							}
							break;
						case "Refill Paper":
							if (currentView != null) {
								currentView.getManager().getRefillLogic().refillPrinterPaper();
								updateMessage("The paper is now full", false);
							}
							else {
								updateMessage("Select a station to visualize first", true);
							}
							break;
						case "Override":
							currentView.getManager().attendantOverride();
							break;
						case "Lookup Product":
							break;
						case "Remove Product":
							break;
						case "Approve Bulky Item":
							currentView.getManager().setAttendantApproved(true);
							break;
						case "Unapprove Bulky Item":
							currentView.getManager().setAttendantApproved(false);
							break;
					}
				}
			});
			rightPanel.add(button);
		}
		add(rightPanel);
		
		// Bottom Panel
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		JLabel bottomTitle = new JLabel("Console Messages");
		bottomTitle.setFont(new Font("Arial", Font.PLAIN, 20));
		bottomTitle.setHorizontalAlignment(SwingConstants.RIGHT);
		bottomPanel.add(bottomTitle);	
		
		bottomPanel.add(messages);
		
		add(bottomPanel);
		
		
		
	}
	
	public CustomerView getCurrentView() {
		return currentView;
	}
	
	private void updateMessage(String text, Boolean isError) {
		String tmpString = messages.getText();
		if (isError) {
			messages.setForeground(Color.red);
		} else {
			messages.setForeground(Color.green);
		}
		if (tmpString.equals(text) || tmpString.equals(text + "(x" + sameMessageCount + ")")) {
			sameMessageCount++;
			messages.setText(text + "(x" + sameMessageCount + ")");
		} else {
			sameMessageCount = 1;
			messages.setText(text);
		}
	}
	
	@Override
	public void refreshUI() {
		
	}

}
