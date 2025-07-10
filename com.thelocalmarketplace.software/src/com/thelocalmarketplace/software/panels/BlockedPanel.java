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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.thelocalmarketplace.software.CustomerView;

import shopping.manager.ShoppingManager;

public class BlockedPanel extends JPanelRefreshable implements IPanel{
	private CustomerView view;
	
	public BlockedPanel(CustomerView view) {
		this.view = view;
		// for absolute positioning
		setLayout(null);
		
		// create the main text of the page
		JLabel label = new JLabel("Session blocked");
		label.setBounds(50, 50, 150, 20);
		add(label);
		
		// create the button to signal attendant
		JButton button = new JButton("Attendant Override");
		button.setBounds(50, 80, 200, 30);
		
		// add button functionality
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.getManager().attendantOverride();
				view.switchToPanel("CartPanel");
			}
		});
		
		add(button);
	}
	
}
