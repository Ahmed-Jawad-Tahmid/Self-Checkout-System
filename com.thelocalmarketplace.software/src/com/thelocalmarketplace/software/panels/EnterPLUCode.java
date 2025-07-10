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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.CustomerView;

import shopping.manager.logic.PLUCodedProductLogic;

public class EnterPLUCode extends JPanelRefreshable implements ActionListener {

    private CustomerView view;
    private JLabel PLUCode;
    private JButton[] numberButtons;
    private JButton deleteButton, backButton, enterButton;
    private JLabel notificationLabel; // Notification label for product addition

    public EnterPLUCode(CustomerView view) {
        this.view = view;
        this.setLayout(new GridLayout(0, 3, 10, 10)); // Set layout to grid with 3 columns
        PLUCode = new JLabel();
        JLabel label = new JLabel("Enter PLU Code:");
        enterButton = new JButton("Enter");
        backButton = new JButton("Back");

        notificationLabel = new JLabel();
        notificationLabel.setHorizontalAlignment(JLabel.CENTER);
        notificationLabel.setForeground(Color.BLUE); 
        add(notificationLabel, BorderLayout.NORTH);

        // Add components to panel
        add(label);
        add(PLUCode);
        add(new JPanel()); // Empty panel for spacing

        numberButtons = new JButton[10];
        for (int i = 0; i < 10; i++) {
            numberButtons[i] = new JButton(String.valueOf(i));
            numberButtons[i].addActionListener(this);
            add(numberButtons[i]);
        }

        add(new JPanel()); // Empty panel for spacing
        deleteButton = new JButton("Delete");
        add(deleteButton);
        add(enterButton);
        add(backButton);

        // Action listeners for buttons
        enterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Your code here
                // add a product by PLU code
            	addPluProduct();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String currentText = PLUCode.getText();
                if (!currentText.isEmpty()) {
                    PLUCode.setText(currentText.substring(0, currentText.length() - 1));
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if (view.getManager().getCurrentPendingProduct() == null && 
            			view.getManager().getLogic() instanceof PLUCodedProductLogic)
            	{
            		view.getManager().setDefaultLogic(true);
            	}
                view.switchToPanel("CartPanel");
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < 10; i++) {
            if (e.getSource() == numberButtons[i]) {
            	if (PLUCode.getText().length() < 5)
            	{
	                PLUCode.setText(PLUCode.getText() + i);
            	}
        	}
        }
    }
    
    
    private void addPluProduct() {
    	String rawStringCode = PLUCode.getText();
    	if (rawStringCode.length() < 4) 
    	{
    		notificationLabel.setText("Product PLU cannot contain less than four digits");
    		return;
    	}
    	
    	PriceLookUpCode code = new PriceLookUpCode(rawStringCode);
    	
    	PLUCodedProduct product = ProductDatabases.PLU_PRODUCT_DATABASE.get(code);
    	
    	if (product == null)
    	{
    		notificationLabel.setText("Product PLU code does not exist");
    		return;
    	}
    	
    	
    	try {
        	view.getManager().addItemFromPLUCOde(product);
    		notificationLabel.setText(CartPanel.getNotificationString());	
    	}
    	catch (Exception error)
    	{		    		
    		notificationLabel.setText("Error: " + error.getMessage());
    	}
    }	
}
