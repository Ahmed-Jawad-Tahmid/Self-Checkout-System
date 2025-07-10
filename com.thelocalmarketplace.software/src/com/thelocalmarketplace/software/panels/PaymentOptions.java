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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.thelocalmarketplace.software.CustomerView;

public class PaymentOptions extends JPanelRefreshable implements ActionListener{
	
	private CustomerView view;
	private String name;
	JLabel label1;

	public PaymentOptions(CustomerView view) {
		this.view=view;
		this.name=name;
		
		setLayout(null);

		label1=new JLabel();
		JLabel label4=new JLabel();

		JButton coin=new JButton();
		JButton banknote=new JButton();
		JButton credit=new JButton();
		JButton debit=new JButton();
		JButton checkout=new JButton();
		
		banknote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	view.switchToPanel("InsertBanknotes");
            }
		});
		
		coin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	view.switchToPanel("InsertCoins");
            }
		});
		
		credit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	view.switchToPanel("CreditPanel");
            }
		});
		
		debit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	view.switchToPanel("DebitPanel");
            }
		});
		
		checkout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	view.switchToPanel("CartPanel");
            }
		});
				
		label1.setBounds(210, 0, 100, 100);
		checkout.setBounds(210,450,200,30);

		checkout.setText("Return to Cart");
		checkout.setFocusable(false);
		
		
		
		coin.setBounds(10, 50, 120, 30);
        banknote.setBounds(10, 150, 120, 30);
        credit.setBounds(10, 250, 120, 30);
        debit.setBounds(10, 350, 120, 30);
        
        label4.setBounds(10,0, 120,30);
        label4.setText("Payment Options:-");
        
        coin.setFocusable(false);
        banknote.setFocusable(false);
        debit.setFocusable(false);
        credit.setFocusable(false);
        
        coin.setText("COIN");
        banknote.setText("BANKNOTE");
        credit.setText("CREDIT");
        debit.setText("DEBIT");
        
        
		//setBounds(0, 0, 200, 500);
		add(label4);
		add(coin);
		add(banknote);
		add(credit);
		add(debit);
		
//		Border border=BorderFactory.createMatteBorder(00,00, 00,002, Color.BLACK);
//		setBorder(border);
		
		add(label1);
		add(checkout);
		
		
		
		
		
		

	}
	
	@Override
	public void refreshUI() {
		label1.setText("Bill Total: $" + view.getManager().getGroceriesCost());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
