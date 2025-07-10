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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.thelocalmarketplace.software.CustomerView;

import shopping.manager.RecieptPrinter;

public class Success12 extends JPanelRefreshable implements ActionListener{
	private CustomerView view;
	private String name;
	private JTextArea receiptArea;
	
	public Success12 (CustomerView customerView) {
		this.view=customerView;
		this.name=name;
		

        // Create a JTextArea to display the receipt
        receiptArea = new JTextArea();
        receiptArea.setBounds(10, 500, 400, 200); // Adjust the bounds as needed
        receiptArea.setEditable(false); // Make it non-editable
		
		JButton finish = new JButton();
		JLabel denied= new JLabel();
				
		denied.setText("Payment Accepted");
		denied.setBounds(180,150, 200,50);
		
		finish.setText("FINISH");
		finish.setBounds(180, 200, 110, 50);		
		finish.setFocusable(false);
		
		finish.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.getManager().reset();
				view.switchToPanel("CartPanel");
			}
		});

		setLayout(null);
		add(denied);
		add(finish);
		
		// Call the method to display the receipt
        displayReceipt();

	}
	
	private void displayReceipt() {        
        // Get the receipt text from the RecieptPrinter class
		String receiptText = RecieptPrinter.getReciept(view.getManager().getGroceries(), view.getManager().getGroceriesCost()); 
	    receiptArea.setText(receiptText); // Set the text in JTextArea
	
	    add(receiptArea); // Add JTextArea to the panel
    }
	
	@Override
	public void refreshUI() {
		displayReceipt();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
