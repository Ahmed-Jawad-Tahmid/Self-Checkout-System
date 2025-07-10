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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.software.CustomerView;

public class AddItems extends JPanelRefreshable{
	private CustomerView view;

	public AddItems(CustomerView view) {
		this.view=view;
		
		setLayout(null);

		JLabel label1 = new JLabel();
		JLabel label3 = new JLabel();
		JLabel label4 = new JLabel();

		JButton placeButton = new JButton();
		placeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {     
            	view.getManager().getStation().getBaggingArea().addAnItem(view.getCurrentItem());
            	view.getItemsOnScale().put("apple", view.getCurrentItem());
            	view.switchToPanel("CartPanel");
            }
        });
		
		JButton noBagButton = new JButton();
		noBagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	view.switchToPanel("BlockedPanel");
            }
        });
		
		JButton cancelButton = new JButton();
		cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {     
            	view.getManager().removeProduct(view.getManager().getRecentProduct(), 1);
            	view.getManager().attendantOverride();
            	view.switchToPanel("CartPanel");
            }
        });
				
		placeButton.setBounds(10, 100, 100, 30);
        noBagButton.setBounds(10, 200, 100, 30);
        cancelButton.setBounds(10, 400, 100, 30);
        
        label4.setBounds(10,0, 120,30);
        label4.setText("Product Name: ");
		label3.setBounds(10, 10, 200, 100);
		label3.setText("Place in Bagging Area:");

        
        placeButton.setFocusable(false);
        noBagButton.setFocusable(false);
        cancelButton.setFocusable(false);
        
        placeButton.setText("Place");
        noBagButton.setText("Do Not Bag");
        cancelButton.setText("Cancel");
        
        
		add(label4);
		add(placeButton);
		add(noBagButton);
		add(cancelButton);
	
//		add(label1);
		add(label3);
		

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				label4.setText("Product Name: " + view.getManager().getGroceries().get(view.getManager().getRecentProduct()).getName());
			}
		});
	}

	
}
