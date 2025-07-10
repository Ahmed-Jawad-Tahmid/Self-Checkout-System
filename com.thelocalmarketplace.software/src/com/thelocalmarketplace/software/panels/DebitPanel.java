package com.thelocalmarketplace.software.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.InvalidPINException;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.CustomerView;

import payment.PayWithCredit;
import payment.PayWithDebit;
import shopping.manager.ShoppingManager;
class PayWithDebitGUI extends PayWithDebit {

	public PayWithDebitGUI(ShoppingManager sm, CardIssuer issuer) {
		super(sm, issuer, null);
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
        this.amountDue = cost;
    }

	public String getGroceriesCost() {
		// TODO Auto-generated method stub
		return groceriesCost.toString();
	}

}

public class DebitPanel extends JPanelRefreshable implements ActionListener  {
	private CustomerView view;
	private String name;
	private JLabel label1 = new JLabel();
	private JLabel label2 = new JLabel();
	private JLabel errorLabel = new JLabel();
	private JButton exitButton = new JButton();
	PayWithDebitGUI payWithDebit;
	String message;
	
	public DebitPanel(CustomerView view) {
        this.view = view;
        this.name = name;
        
        
        Calendar current = Calendar.getInstance();
        Calendar expiryDate = (Calendar) current.clone();
		expiryDate.set(Calendar.YEAR, 2025);
		expiryDate.set(Calendar.MONTH, Calendar.AUGUST);
		expiryDate.set(Calendar.DAY_OF_MONTH, 22);
        
        CardIssuer cardIssuer = new CardIssuer("Debit", 1000);
        cardIssuer.addCardData("123456789", "Joe", expiryDate, "375", 1000);
        Card JoeCard = new Card("Debit", "123456789", "Joe", "375", "1234", true, true);
        payWithDebit = new PayWithDebitGUI(view.getManager(), cardIssuer);

        setLayout(null);

        JButton tap = new JButton();
        JButton swipe = new JButton();
        JButton insert = new JButton();
		insert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
		            try {
		            	payWithDebit.setGroceriesCost(view.getManager().getGroceriesCost());
		                message = payWithDebit.InsertCard(JoeCard);
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
		            if(payWithDebit.isPaidFull()) {
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
		            	payWithDebit.setGroceriesCost(view.getManager().getGroceriesCost());
		                message = payWithDebit.SwipeCard(JoeCard);
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
		            if(payWithDebit.isPaidFull()) {
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
		            	payWithDebit.setGroceriesCost(view.getManager().getGroceriesCost());
		                message = payWithDebit.TapCard(JoeCard);
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
		            if(payWithDebit.isPaidFull()) {
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
        label4.setText("Debit Options:- ");
        
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
		label2.setText("Total: $" + payWithDebit.getAmountDue());
	}

	public void notifyPaidAmountChanged() {
	    refreshUI();
	}



		
	

}



