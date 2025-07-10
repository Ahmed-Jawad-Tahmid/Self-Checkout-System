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

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.software.panels.*;

import powerutility.PowerGrid;
import shopping.manager.ShoppingManager;

public class AttendantView {
	private JFrame attendantFrame;
	private JPanel loginPanel;
	private JPanel dashboardPanel;
	private CustomerView[] customerViews;
	private AttendantStation attendantStation;
	private CardLayout cardlayout;
	
	public AttendantView() {
		// create the attendantFrame
		//attendantFrame = new JFrame("Attendant Dashboard");
		
		// Additional panels
		
		// create and setup attendantStation
		attendantStation = new AttendantStation();
		attendantStation.plugIn(PowerGrid.instance());
		attendantStation.turnOn();
		attendantStation.screen.getFrame().setVisible(true);
		attendantStation.keyboard.enable();
		
		// set the station's frame to be this view's frame
		attendantFrame = attendantStation.screen.getFrame();
		attendantFrame.setSize(1000, 700);
		attendantFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		cardlayout = new CardLayout();
		attendantFrame.setLayout(cardlayout);
		
		// create the login panel
		loginPanel = new JPanel();
		JTextField passwordField = new JPasswordField(10);
		JButton loginButton = new JButton("Login");
		JLabel errorLabel = new JLabel();
		errorLabel.setForeground(Color.RED);
		loginPanel.add(passwordField);
		loginPanel.add(loginButton);
		loginPanel.add(errorLabel);

		
		// create the three different customer views
		customerViews = new CustomerView[3];
		customerViews[0] = new CustomerView(new ShoppingManager("Bronze"));
		customerViews[1] = new CustomerView(new ShoppingManager("Silver"));
		customerViews[2] = new CustomerView(new ShoppingManager("Gold"));
		
		// create the dashboard panel which doesn't show up until you login properly
		// remember we are instantiating a specific panel that we made a class for
		dashboardPanel = new DashboardPanel(customerViews, loginPanel, this);
		dashboardPanel.setVisible(false);
		
		// add login functionality
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (passwordField.getText().equals("0000")) {
					passwordField.setText("");
					loginPanel.setVisible(false);
					dashboardPanel.setVisible(true);
				} else {
					errorLabel.setText("Incorrect password");
				}
			}
		});
		
		// add end session functionality
		JButton endSessionButton = new JButton("End session");
		endSessionButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        attendantFrame.dispose();
		        System.exit(0);
		    }
		});
		loginPanel.add(endSessionButton);
		
		// add panels to the frame
		attendantFrame.add(loginPanel);
		attendantFrame.add(dashboardPanel);
		loginPanel.setVisible(true);
		attendantFrame.setVisible(true);


		
	}
	
	public void switchToPanel(String panelId) {
		cardlayout.show(attendantFrame.getContentPane(), panelId);
        System.out.println("Now visible: " + panelId);
	}
	
}
