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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Currency;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.software.CustomerView;

import shopping.interfaces.IPaymentListener;

public class InsertCoins extends JPanelRefreshable implements ActionListener, IPaymentListener {
	private CustomerView view;
	private String name;
	Currency currency = Currency.getInstance("CAD");

	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JLabel label5;
	private JLabel errorLabel;
	private JButton exitButton;
	
	public InsertCoins(CustomerView view) {
		this.view = view;
		this.name = name;
		
		view.getManager().getPayWithCoin().addListener(this);

		setLayout(null);

		label1 = new JLabel();
		label2 = new JLabel();
		label3 = new JLabel();
		JLabel label4 = new JLabel();
		label5 = new JLabel();
		errorLabel = new JLabel();

		JButton coin1c = new JButton();
		JButton coin5c = new JButton();
		JButton coin10c = new JButton();
		JButton coin25c = new JButton();
		JButton coin1d = new JButton();

		addActionInsertCoinUpdateLabels(coin1c, new BigDecimal("0.01"), view);
		addActionInsertCoinUpdateLabels(coin5c, new BigDecimal("0.05"), view);
		addActionInsertCoinUpdateLabels(coin10c, new BigDecimal("0.10"), view);
		addActionInsertCoinUpdateLabels(coin25c, new BigDecimal("0.25"), view);
		addActionInsertCoinUpdateLabels(coin1d, new BigDecimal("1.00"), view);

		label1.setBounds(210, 0, 200, 100);
		label2.setBounds(210, 400, 100, 100);
		label3.setBounds(210, 500, 100, 100);
		label5.setBounds(210, 550, 300, 100);
		label5.setFont(new Font("Arial", Font.PLAIN, 20));
		errorLabel.setBounds(210, 600, 800, 100);

		label2.setText("Total: $" + view.getManager().getGroceriesCost());
		setPaymentLabels();

		coin1c.setBounds(10, 50, 100, 30);
		coin5c.setBounds(10, 150, 100, 30);
		coin10c.setBounds(10, 250, 100, 30);
		coin25c.setBounds(10, 350, 100, 30);
		coin1d.setBounds(10, 450, 100, 30);

		label4.setBounds(10, 0, 120, 30);
		label4.setText("Insert Coins:- ");

		coin1c.setFocusable(false);
		coin5c.setFocusable(false);
		coin10c.setFocusable(false);
		coin25c.setFocusable(false);
		coin1d.setFocusable(false);

		coin1c.setText("$0.01");
		coin5c.setText("$0.05");
		coin10c.setText("$0.10");
		coin25c.setText("$0.25");
		coin1d.setText("$1.00");

		add(label4);
		add(coin1c);
		add(coin5c);
		add(coin10c);
		add(coin25c);
		add(coin1d);

		add(label1);
		add(label2);
		add(label3);
		add(label5);
		add(errorLabel);
	}

	private void addActionInsertCoinUpdateLabels(JButton button, BigDecimal amount, CustomerView view) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Coin coin = new Coin(currency, amount);
				try {
					view.getManager().getStation().getCoinSlot().receive(coin);
					setPaymentLabels();
				} catch (DisabledException e1) {
					e1.printStackTrace();
					errorLabel.setText("Error: " + e1.getMessage() + ". Please call an attendant.");
				} catch (CashOverloadException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					errorLabel.setText("Error: " + e1.getMessage() + ". Please call an attendant.");
				} catch (RuntimeException re) {
					re.printStackTrace();
					errorLabel.setText("Error: " + re.getMessage() + ". Please call an attendant.");
				}
			}
		});
	}

	private void setPaymentLabels() {
		label1.setText("Amount Paid: " + view.getManager().getPayWithCoin().getPaymentReceived());
		BigDecimal change = view.getManager().getPayWithCoin().getPaymentReceived().subtract(view.getManager().getGroceriesCost());
		if(change.compareTo(new BigDecimal(0.05)) == -1) {
			label3.setText("Change: None");
		} else {
			label3.setText("Change: $" + change);
			label5.setText("Please remove your change");
		}
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

	@Override
	public void notifyPayment() {
		setPaymentLabels();
		exitButton = new JButton("Exit");
		exitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.switchToPanel("Success12");
			}
		});
	    exitButton.setBounds(10, 550, 100, 50); // Set the position and size of the button
	    exitButton.setFocusable(false);
	    add(exitButton);
	    validate(); // Refresh the panel to show the new button
	    repaint();
	}

	@Override
	public void notifyPaidAmountChanged() {
		setPaymentLabels();
	}

	@Override
	public void notifyDanglingBanknote() {}
}
