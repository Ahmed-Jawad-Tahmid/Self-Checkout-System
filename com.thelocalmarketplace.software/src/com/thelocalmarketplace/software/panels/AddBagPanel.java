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

import javax.swing.*;

import com.thelocalmarketplace.software.CustomerView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddBagPanel extends JPanelRefreshable {
    private CustomerView view;
    private JButton addBagButton;

    public AddBagPanel() {
        
        // Set layout for the panel
        setLayout(new GridBagLayout());
        addBagButton = new JButton("Add New Bag");
        
        addBagButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	view.getManager().addBag();
            }
        });

        // Set button size
        addBagButton.setPreferredSize(new Dimension(120, 30));

        // Add button to the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding
        add(addBagButton, gbc);
    }

    public JButton getAddBagButton() {
        return this.addBagButton;
    }

    public void setView(CustomerView view) {
        this.view = view;
    }


}
