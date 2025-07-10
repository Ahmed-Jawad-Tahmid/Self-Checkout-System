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

import com.thelocalmarketplace.software.CustomerView;

public class Failure13 extends JPanelRefreshable implements ActionListener{
	
	private CustomerView view;
	private String name;
	
	public Failure13 () {
		this.view=view;
		this.name=name;
		
		this.setLayout(null);

		JButton tryagain = new JButton();
		JLabel denied= new JLabel();
				
		denied.setText("Payment Denied");
		denied.setBounds(180,150, 100,50);
		
		tryagain.setText("TRY AGAIN");
		tryagain.setBounds(180, 200, 100, 50);		
		tryagain.setFocusable(false);

		add(denied);
		add(tryagain);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	

}
