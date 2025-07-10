package com.thelocalmarketplace.software.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import payment.PayWithCredit;


import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.InvalidPINException;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.CustomerView;


import shopping.manager.ShoppingManager;

class PayWithCreditGUI extends PayWithCredit {

	public PayWithCreditGUI(ShoppingManager sm, CardIssuer issuer) {
		super(sm, issuer);
		this.groceriesCost = sm.getGroceriesCost();
	}

	@Override
	protected boolean getSignature(String cardholder) {
		String signature = JOptionPane.showInputDialog("Enter your signature:");
		return signature != null && signature.equals(cardholder);
	}

	@Override
	protected String getPin() {
		String pin = JOptionPane.showInputDialog("Enter your PIN:");
		return pin;
	}
	
	public void setGroceriesCost(BigDecimal cost) {
        this.groceriesCost = cost;
    }

	public String getGroceriesCost() {
		// TODO Auto-generated method stub
		return groceriesCost.toString();
	}

}

public  class CreditPanel extends JPanelRefreshable implements ActionListener {
	private CustomerView view;
	private String name;
	private JLabel label1 = new JLabel();
	private JLabel label2 = new JLabel();
	private JLabel errorLabel = new JLabel();
	private JButton exitButton = new JButton();
	PayWithCreditGUI payWithCredit;
	String message;
	
	

    public CreditPanel(CustomerView view) {
        this.view = view;
        this.name = name;
        
        
        Calendar current = Calendar.getInstance();
        Calendar expiryDate = (Calendar) current.clone();
		expiryDate.set(Calendar.YEAR, 2025);
		expiryDate.set(Calendar.MONTH, Calendar.AUGUST);
		expiryDate.set(Calendar.DAY_OF_MONTH, 22);
        
        CardIssuer cardIssuer = new CardIssuer("Visa", 100);
        cardIssuer.addCardData("123456789", "Joe", expiryDate, "375", 100);
        Card JoeCard = new Card("Visa", "123456789", "Joe", "375", "1234", true, true);
        payWithCredit = new PayWithCreditGUI(view.getManager(), cardIssuer);

        setLayout(null);

        JButton tap = new JButton();
        JButton swipe = new JButton();
        JButton insert = new JButton();
		insert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		            try {
		            	payWithCredit.setGroceriesCost(view.getManager().getGroceriesCost());
		                message = payWithCredit.InsertCard(JoeCard);
		                errorLabel.setText(message);
		                repaint();
		                
		            } catch (IOException e1) {
		                errorLabel.setText("Error: " + e1.getMessage());
		                repaint();
		            } catch (InvalidPINException e2) {
		                errorLabel.setText("Invalid PIN: " + e2.getMessage());
		                repaint();
		            }
		            notifyPayment();
		            if(payWithCredit.isPaymentMade()) {
		            	exitButton = new JButton("Exit");
		        		exitButton.addActionListener(new ActionListener() {
		        			@Override
		        			public void actionPerformed(ActionEvent e) {
		        				view.switchToPanel("Success12");
		        			}
		        		});
		                exitButton.setBounds(10, 350, 100, 50); // Set the position and size of the button
		                exitButton.setFocusable(false);
		                add(exitButton);
		                validate(); // Refresh the panel to show the new button
		                repaint();
		            }
			}
		});
		
		swipe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		            try {
		            	payWithCredit.setGroceriesCost(view.getManager().getGroceriesCost());
		                message = payWithCredit.SwipeCard(JoeCard);
		                errorLabel.setText(message);
		                repaint();

		                
		            } catch (IOException e1) {
		                errorLabel.setText("Error: " + e1.getMessage());
		                repaint();
		            } catch (InvalidPINException e2) {
		                errorLabel.setText("Invalid PIN: " + e2.getMessage());
		                repaint();
		            }
		            notifyPayment();
		            if(payWithCredit.isPaymentMade()) {
		            	exitButton = new JButton("Exit");
		        		exitButton.addActionListener(new ActionListener() {
		        			@Override
		        			public void actionPerformed(ActionEvent e) {
		        				view.switchToPanel("Success12");
		        			}
		        		});
		                exitButton.setBounds(10, 350, 100, 50); // Set the position and size of the button
		                exitButton.setFocusable(false);
		                add(exitButton);
		                validate(); // Refresh the panel to show the new button
		                repaint();
		            }
			}
		});
		
		tap.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		            try {
		            	payWithCredit.setGroceriesCost(view.getManager().getGroceriesCost());
		                message = payWithCredit.TapCard(JoeCard);
		                errorLabel.setText(message);
		                repaint();
		            } catch (IOException e1) {
		                errorLabel.setText("Error: " + e1.getMessage());
		                repaint();
		            } catch (InvalidPINException e2) {
		                errorLabel.setText("Invalid PIN: " + e2.getMessage());
		                repaint();
		            }
		            notifyPayment();
		            if(payWithCredit.isPaymentMade()) {
		            	exitButton = new JButton("Exit");
		        		exitButton.addActionListener(new ActionListener() {
		        			@Override
		        			public void actionPerformed(ActionEvent e) {
		        				view.switchToPanel("Success12");
		        			}
		        		});
		                exitButton.setBounds(10, 350, 100, 50); // Set the position and size of the button
		                exitButton.setFocusable(false);
		                add(exitButton);
		                validate(); // Refresh the panel to show the new button
		                repaint();
		            }
			}
		});
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.switchToPanel("Success12");
			}
		});

        label1.setBounds(210, 0, 100, 100);
        label2.setBounds(210, 400, 100, 100);
        errorLabel.setBounds(210, 600, 800, 100);


        tap.setBounds(10, 50, 100, 50);
        swipe.setBounds(10, 150, 100, 50);
        insert.setBounds(10, 250, 100, 50);

        JLabel label4 = new JLabel();
        label4.setBounds(10, 0, 120, 30);
        label4.setText("Credit Options:- ");
        
        label2.setText("Total: $" + view.getManager().getGroceriesCost());
        refreshUI();

        tap.setFocusable(false);
        swipe.setFocusable(false);
        insert.setFocusable(false);

        tap.setText("Tap");
        swipe.setText("Swipe");
        insert.setText("Insert");

        add(label4);
        add(tap);
        add(swipe);
        add(insert);

        add(label1);
        add(label2);
        add(errorLabel);
    }

	
	private void setPaymentLabels() {
		label1.setText("Amount Paid: " + view.getManager().getPayWithCoin().getPaymentReceived());

	}
	
	
	@Override
	public void refreshUI() {
		setPaymentLabels();
		label2.setText("Total: $" + view.getManager().getGroceriesCost());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	}

	public void notifyPayment() {
		label2.setText("Total: $" + payWithCredit.getGroceriesCost());
	}

	public void notifyPaidAmountChanged() {
	    refreshUI();
	}



		
	

}
