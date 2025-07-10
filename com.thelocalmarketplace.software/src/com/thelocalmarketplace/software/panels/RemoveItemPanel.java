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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.thelocalmarketplace.software.CustomerView;

public class RemoveItemPanel extends JPanelRefreshable {
    private CustomerView view;

    public RemoveItemPanel(CustomerView view) {
        this.view = view;

        setLayout(null);

        JLabel label1 = new JLabel();
        JLabel label3 = new JLabel();
        JLabel label4 = new JLabel();

        JButton removeButton = new JButton();
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle remove button click
                // This should remove the item from the cart
                view.getManager().getStation().getBaggingArea().removeAnItem(view.getCurrentItem());
                view.getItemsOnScale().remove("apple");
                view.switchToPanel("CartPanel");
            }
        });
        
        JButton itemBulky = new JButton();
        itemBulky.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	//code to to to blocked panel
            	view.switchToPanel("BlockedPanel");
            }
        });


        removeButton.setBounds(10, 100, 100, 30);
        itemBulky.setBounds(10, 200, 100, 30);

        label4.setBounds(10,0, 120,30);
        label4.setText("Product Name: ");
        label3.setBounds(10, 10, 200, 100);
        label3.setText("Remove from Scale:");

        removeButton.setFocusable(false);

        itemBulky.setFocusable(false);

        removeButton.setText("Remove");

        itemBulky.setText("Item Bulky");

        add(label4);
        add(removeButton);
        add(itemBulky);
        add(label3);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                label4.setText("Product Name: " + view.getRecentProductName());
            }
        });
    }
}