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

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteDispensationSlotObserver;
import com.thelocalmarketplace.software.CustomerView;

import shopping.interfaces.IPaymentListener;

public class InsertBanknotes extends JPanelRefreshable implements ActionListener, IPaymentListener, BanknoteDispensationSlotObserver {

	private CustomerView view;
	private String name;
	Currency currency = Currency.getInstance("CAD");
	
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JLabel label5;
	private JLabel errorLabel;
	private JButton exitButton;

	public InsertBanknotes(CustomerView view) {
		this.view = view;
		this.name = name;
		
		view.getManager().getPayWithBanknote().addListener(this);
		view.getManager().getStation().getBanknoteOutput().attach(this);

		setLayout(null);

		label1 = new JLabel();
		label2 = new JLabel();
		label3 = new JLabel();
		JLabel label4 = new JLabel();
		label5 = new JLabel();
		errorLabel = new JLabel();

		JButton b5d = new JButton();
		JButton b10d = new JButton();
		JButton b20d = new JButton();
		JButton b50d = new JButton();
		JButton b100d = new JButton();

		addActionInsertBanknoteUpdateLabels(b5d, new BigDecimal("5"), view);
		addActionInsertBanknoteUpdateLabels(b10d, new BigDecimal("10"), view);
		addActionInsertBanknoteUpdateLabels(b20d, new BigDecimal("20"), view);
		addActionInsertBanknoteUpdateLabels(b50d, new BigDecimal("50"), view);
		addActionInsertBanknoteUpdateLabels(b100d, new BigDecimal("100"), view);

		label1.setBounds(210, 0, 100, 100);
		label2.setBounds(210, 400, 100, 100);
		label3.setBounds(210, 500, 100, 100);
		label5.setBounds(210, 550, 300, 100);
		label5.setFont(new Font("Arial", Font.PLAIN, 20));
		errorLabel.setBounds(210, 600, 800, 100);

		label2.setText("Total: $" + view.getManager().getGroceriesCost());
		setPaymentLabels();

		b5d.setBounds(10, 50, 100, 30);
		b10d.setBounds(10, 150, 100, 30);
		b20d.setBounds(10, 250, 100, 30);
		b50d.setBounds(10, 350, 100, 30);
		b100d.setBounds(10, 450, 100, 30);

		label4.setBounds(10, 0, 120, 30);
		label4.setText("Insert Banknotes:- ");

		b5d.setFocusable(false);
		b10d.setFocusable(false);
		b20d.setFocusable(false);
		b50d.setFocusable(false);
		b100d.setFocusable(false);

		b5d.setText("$5");
		b10d.setText("$10");
		b20d.setText("$20");
		b50d.setText("$50");
		b100d.setText("$100");

		// setBounds(0, 0, 200, 500);
		add(label4);
		add(b5d);
		add(b10d);
		add(b20d);
		add(b50d);
		add(b100d);

//		Border border=BorderFactory.createMatteBorder(0,0,0,002, Color.BLACK);
//		setBorder(border);

		add(label1);
		add(label2);
		add(label3);
		add(label5);
		add(errorLabel);
	}

	private void addActionInsertBanknoteUpdateLabels(JButton button, BigDecimal amount, CustomerView view) {
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Banknote banknote = new Banknote(currency, amount);
				try {
					view.getManager().getStation().getBanknoteInput().receive(banknote);
				} catch (DisabledException e1) {
					// TODO Auto-generated catch block
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
		label1.setText("Amount Paid: " + view.getManager().getPayWithBanknote().getPaymentRecieved());
		BigDecimal change = view.getManager().getPayWithBanknote().getPaymentRecieved().subtract(view.getManager().getGroceriesCost());
		// We won't give change less than 5 cents
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

	}

	@Override
	public void notifyPaidAmountChanged() {
		setPaymentLabels();
	}

	@Override
	public void notifyDanglingBanknote() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnedOn(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnedOff(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void banknoteDispensed(BanknoteDispensationSlot slot, List<Banknote> banknotes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void banknotesRemoved(BanknoteDispensationSlot slot) {
		// TODO Auto-generated method stub
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

}
