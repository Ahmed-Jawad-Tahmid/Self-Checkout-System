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

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.CoinTray;
import com.thelocalmarketplace.software.CustomerView;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class IRLPanel extends JPanelRefreshable {
    private CustomerView view;
    private ArrayList<BarcodedItem> addedItems = new ArrayList<>();
    private int scannedItemCount = 0;
    private int addedItemCount = 0;
    JLabel coinLabel = new JLabel();

    public IRLPanel(CustomerView customerView) {
        this.view = customerView;
        
        // Create a list to store the items added to the bagging area
        ArrayList addedItems = new ArrayList();

        setLayout(new GridLayout(2, 3)); // 2 rows, 3 columns
        
        String[] ITEM_DESCRIPTIONS = {"Milk", "Bread", "Meat", "Cheese", "Juice", "Soda"};
        BarcodedItem[] items = new BarcodedItem[ITEM_DESCRIPTIONS.length];
        int[] ITEM_MASSES = {50000000, 100000000, 150000000, 200000000, 250000000, 300000000};
        JPanel buttonsPanel = new JPanel();

        for (int i = 0; i < 6; i++) {
        	final int index = i;
        	
            JPanel section = new JPanel();
            section.setLayout(new BoxLayout(section, BoxLayout.PAGE_AXIS));

            JLabel itemLabel = new JLabel(ITEM_DESCRIPTIONS[i]);
            JButton scanButton = new JButton("Scan");
            JButton addButton = new JButton("Add");
            JButton removeButton = new JButton("Remove");
            
            // Add action listeners to the buttons
            scanButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	// Create a new item each time the button is clicked
                    Numeral[] numerals = new Numeral[]{
                        Numeral.valueOf((byte) ((index + 1) % 10)),
                        Numeral.valueOf((byte) ((index + 2) % 10)),
                        Numeral.valueOf((byte) ((index + 3) % 10)),
                        Numeral.valueOf((byte) ((index + 4) % 10)),
                        Numeral.valueOf((byte) ((index + 5) % 10))
                    };
                    Barcode validBarcode = new Barcode(numerals);
                    BarcodedItem newItem = new BarcodedItem(validBarcode, new Mass(ITEM_MASSES[index]));

                    System.out.println("Scan button clicked for " + itemLabel.getText());

                    // if the expected weight is less than the weight on the scale, it means that an item had not yet been removed
                    if (view.getManager().getExpectedWeight().compareTo(view.getManager().getWeight()) < 0) {
                        JOptionPane.showMessageDialog(null, "Please remove previous item from scale", "Remove Item", JOptionPane.WARNING_MESSAGE);
                    } else
						try {
							if (!view.getManager().hasWeightDiscrepancy()){
							    // Scan the new item
							    view.getManager().getStation().getMainScanner().scan(newItem);
							    view.setCurrentItem(newItem);
							    // Refresh the UI and show the dialog
							    view.getPanels().get("CartPanel").refreshUI();
	                            JOptionPane.showMessageDialog(null, "Please place item on scale", "Place Item", JOptionPane.WARNING_MESSAGE);
							    scannedItemCount++;
							}
						} catch (OverloadedDevice e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                }
            });
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                	// Create a new item each time the button is clicked
                    Numeral[] numerals = new Numeral[]{
                        Numeral.valueOf((byte) ((index + 1) % 10)),
                        Numeral.valueOf((byte) ((index + 2) % 10)),
                        Numeral.valueOf((byte) ((index + 3) % 10)),
                        Numeral.valueOf((byte) ((index + 4) % 10)),
                        Numeral.valueOf((byte) ((index + 5) % 10))
                    };
                    Barcode validBarcode = new Barcode(numerals);
                    BarcodedItem newItem = new BarcodedItem(validBarcode, new Mass(ITEM_MASSES[index]));
                    
                	view.getManager().getStation().getBaggingArea().addAnItem(newItem);
                	addedItems.add(newItem);
                    addedItemCount++;
                    
                    try {
						if (view.getManager().hasWeightDiscrepancy()) {
						    JOptionPane.showMessageDialog(null, "There is a weight discrepancy. Please stop placing items ", "Stop placing items", JOptionPane.WARNING_MESSAGE);
						}
					} catch (HeadlessException | OverloadedDevice e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    
                    // Refresh the UI and hide the dialog
                    view.getPanels().get("CartPanel").refreshUI();
                    
                    System.out.println(addedItems.toString());
                }
            });
            removeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Remove the last added item from the bagging area and the addedItems list
                    if (!addedItems.isEmpty()) {
                    	System.out.println("items exist in the list");
                        BarcodedItem lastAddedItem = (BarcodedItem) addedItems.get(addedItems.size() - 1);
                        
                        if (addedItems.contains(lastAddedItem)) {
                        	System.out.println("specific item exists in the list");
                        	addedItems.remove(lastAddedItem);
                            view.getManager().getStation().getBaggingArea().removeAnItem(lastAddedItem);
                            

                            addedItemCount--;
                        } else {
                        	// Show a dialog if the user tries to remove an item that does not exist in the addedItems list
                            JOptionPane.showMessageDialog(null, "This item does not exist in the bagging area", "Item Not Found", JOptionPane.WARNING_MESSAGE);
                        }
                    } else {
                    	System.out.println("no items exist in the list");
                        JOptionPane.showMessageDialog(null, "No items to remove from scale", "No items", JOptionPane.WARNING_MESSAGE);
                    }
                    // Refresh the UI
                    view.getPanels().get("CartPanel").refreshUI();
                    
                    System.out.println(addedItems.toString());
                }
            });

            section.add(itemLabel);
            section.add(scanButton);
            section.add(addButton);
            section.add(removeButton);

            add(section);
        }
        
        JButton getCoinButton = new JButton("Remove Change");
        JButton getBanknoteButton = new JButton("Remove Banknote");
        JButton getReceiptButton = new JButton("Remove Receipt");
        
        getCoinButton.setBounds(50, 50, 30, 20);
        getBanknoteButton.setBounds(200, 50, 30, 20);
        getReceiptButton.setBounds(350, 50, 30, 20);
        
        getCoinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CoinTray coinTray = view.getManager().getStation().getCoinTray();
                List<Coin> change = coinTray.collectCoins();
                
                if (change.size() == 0) {
                    coinLabel.setText("No Coins Collected");
                } else {
                    coinLabel.setText("Coins Collected");
                }
                
                buttonsPanel.add(coinLabel);
                validate();
                repaint();
            }
        });

        getBanknoteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BanknoteDispensationSlot banknoteSlot = view.getManager().getStation().getBanknoteOutput();
                try {
                    banknoteSlot.removeDanglingBanknotes();
                } catch (NullPointerException ex) {
                    // Handle the exception
                	coinLabel.setText("No dangling banknote to remove.");
                }
                // A bank note was removed
                coinLabel.setText("Banknotes Collected");

                buttonsPanel.add(coinLabel);
                validate();
                repaint();
            }
        });
        
        getReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                	view.getManager().getStation().getPrinter().removeReceipt();
                } catch (NullPointerException ex) {
                    // Handle the exception
                	coinLabel.setText("No receipt to remove");
                }
                // A receipt was removed
                coinLabel.setText("Receipt removed");

                buttonsPanel.add(coinLabel);
                validate();
                repaint();
            }
        });

        getReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle the get receipt action
            }
        });

        // Add the buttons to the panel
        buttonsPanel.add(getCoinButton);
        buttonsPanel.add(getBanknoteButton);
        buttonsPanel.add(getReceiptButton);
        
        
        add(buttonsPanel);
    }

    public int getAddedItemCount() {
        return addedItemCount;
    }

    public int getScannedItemCount() {
        return scannedItemCount;
    }

}