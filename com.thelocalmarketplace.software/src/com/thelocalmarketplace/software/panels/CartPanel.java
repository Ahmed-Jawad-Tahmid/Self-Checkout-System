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
import java.util.Map;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Shape;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position.Bias;
import javax.swing.text.View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;

import com.jjjwelectronics.Item;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.CustomerView;

import product.ProductInfo;
import shopping.manager.ShoppingManager;

public class CartPanel extends JPanelRefreshable{
	private static CustomerView view;
	private ShoppingManager manager;
	private DefaultTableModel model;
	JLabel totalValueLabel;
	JLabel expectedWeightLabel;
	JLabel weightOnScaleLabel;
	private JLabel notificationLabel;
	
    private static CartPanel instance = null;
    private static String notificationString = "";
    private static String expectedWeight = "";
    
    public static String getNotificationString() {
    	return notificationString;
    }

    public static synchronized CartPanel getInstance()
    {
        if (instance == null)
        	instance = new CartPanel();
 
        return instance;
    }
    
    public static synchronized CartPanel newInstance()
    {
    	instance = new CartPanel();
    	return instance;
    }
    
    
    public static void setView(CustomerView newView) {
		view = newView;
    }
    
	public CartPanel() {
		this.manager = view.getManager();
		setLayout(new GridLayout(1, 2));
		
		// Create the popup dialog
        final JDialog dialog = new JDialog();
        dialog.setModal(true);
        JOptionPane optionPane = new JOptionPane("Please remove item from cart", JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        dialog.setContentPane(optionPane);
        dialog.pack();
        dialog.setLocationRelativeTo(null); // Center the dialog on the screen
		
		// Right side
        JPanel rightPanel = new JPanel(new BorderLayout()); // BorderLayout for right side
        String[] columnNames = {"Product", "Price($)"};
        model = new DefaultTableModel(columnNames, 0); // Table model
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        rightPanel.add(scrollPane, BorderLayout.CENTER); // Add table to center of right side

        // Total row
        JPanel totalPanel = new JPanel(new GridLayout(3, 2)); // GridLayout with 1 row and 2 columns
        JLabel totalLabel = new JLabel("Total");
        totalValueLabel = new JLabel(view.getManager().getGroceriesCost().toString()); // Initially set to 0
        JLabel expectedWeightLabelTitle = new JLabel("Expected Weight");
        expectedWeightLabel = new JLabel("0"); // Initially set to 0
        JLabel weightOnScaleLabelTitle = new JLabel("Weight on Scale");
        weightOnScaleLabel = new JLabel("0"); // Initially set to 0
        totalPanel.add(expectedWeightLabelTitle);
        totalPanel.add(expectedWeightLabel);
        totalPanel.add(weightOnScaleLabelTitle);
        totalPanel.add(weightOnScaleLabel);
        totalPanel.add(totalLabel);
        totalPanel.add(totalValueLabel);

        // Pay button
        JButton payButton = new JButton("Pay");
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle pay button click
            	view.switchToPanel("PaymentOptions");
            }
        });

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalPanel, BorderLayout.NORTH); // Add total row to north of bottom panel
        bottomPanel.add(payButton, BorderLayout.SOUTH); // Add pay button to south of bottom panel

        rightPanel.add(bottomPanel, BorderLayout.SOUTH); // Add bottom panel to south of right side
		
		// Left side
        JPanel leftPanel = new JPanel(new GridLayout(4, 1)); // GridLayout with 4 rows and 1 column
        JButton catalogButton = new JButton("Visual Catalog");
        catalogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle catalog button click
            	view.switchToPanel("VisualCatalog");

            }
        });
        
        JButton plusButton = new JButton("Enter PLU Code");
        plusButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	view.getManager().setPLUCodeLogic();
            	view.switchToPanel("EnterPLUCode");
            }
        });
        
        JButton bulkyItemButton = new JButton("Handle Bulky Item");
        bulkyItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
            		view.getManager().setAddBulkyItemLogic();        
            		notifyMessage("You are now handling a bulky item, please wait for an attendant to approve and then add the desired product.");
            	}
            	catch (Exception error)
            	{
            		notifyError(error.getMessage());
            	}
            }
        });
        
        JButton cancelAssistance = new JButton("Cancel Bulky Item");
        cancelAssistance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	view.getManager().setDefaultLogic(true);
        		notifyMessage("cancelled, continue shopping normally");

            }
        });
        
        JButton removeButton = new JButton("Remove Item");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle remove button click
            	int selectedRow = table.getSelectedRow();
            	if (selectedRow != -1) {
            		// take note of the name we just removed from the table
            		String recentProductName = (String) model.getValueAt(selectedRow, 0);
            		view.setRecentProductName(recentProductName);
            		
            		// get the product from the manager associated by the name
            		Product product = view.getManager().getProductByName(recentProductName);
            		
            		try {
                		view.getManager().removeProduct(product, 1);
                		view.getManager().refreshGroceryInfo();
                		
                		// from the list of items that are on the scale, get the item by the recent product removed
                		Item item = view.getItemsOnScale().get(recentProductName);
                		
                		// set the current item to be the item we want to remove
                		view.setCurrentItem(item);
                		
                		// set the recent product to be the product we removed from the table
                		view.getManager().setRecentProduct(product);
                		
                		// remove the table entry
                		model.removeRow(selectedRow);

                		// remove the product
                		dialog.setVisible(true);	
                		refreshUI();
            		}
            		catch (Exception error)
            		{
            			//bruh
            		}
            	}
            }
        });
        leftPanel.add(catalogButton);
        leftPanel.add(plusButton);        
        leftPanel.add(bulkyItemButton);
        leftPanel.add(removeButton);
        leftPanel.add(cancelAssistance);
        
        // Add left and right panels to main panel
        add(leftPanel);
        add(rightPanel);
        
        
        // Initialize notification label
        notificationLabel = new JLabel("Notifications will be present here...");
        notificationLabel.setHorizontalAlignment(SwingConstants.CENTER);
        notificationLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Set font properties

        // Add notification label to the top of the frame
        add(notificationLabel, BorderLayout.NORTH);
        

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				model.setRowCount(0);
				updateTable();
        		view.getManager().refreshGroceryInfo();

				// update the expected weight, weight on scale, and total cost of groceries
				expectedWeightLabel.setText(view.getManager().getExpectedWeight().toString());
				weightOnScaleLabel.setText(view.getManager().getWeight().toString());
				totalValueLabel.setText(view.getManager().getGroceriesCost().toString());
			}
		});
	}
	
	public void updateTable() {
        // table displays every item in groceries
		view.getManager().refreshGroceryInfo();

        for (Map.Entry<Product, ProductInfo> entry: view.getManager().getGroceries().entrySet()) {
        	ProductInfo info = entry.getValue();
        	for (int i = 0; i < info.getCount(); i++) {
            	model.addRow(new Object[] {info.getName(), info.getPrice()});
        	}
        }
	}
	
	@Override
	public void refreshUI() {
		view.getManager().refreshGroceryInfo();
		model.setRowCount(0);
		updateTable();
		
		expectedWeight = view.getManager().getExpectedWeight().toString();
		// update the expected weight, weight on scale, and total cost of groceries
		expectedWeightLabel.setText(expectedWeight);
		weightOnScaleLabel.setText(view.getManager().getWeight().toString());
		totalValueLabel.setText(view.getManager().getGroceriesCost().toString());
		notificationLabel.setText("<html>"+ CartPanel.notificationString +"</html>");
		repaint();
		revalidate();
	}
	
	public void notifyError(String message) {
		System.out.println("message recieved: " + message);

		CartPanel.notificationString = message;
        notificationLabel.setForeground(Color.RED); // Set text color to red
		notificationLabel.setText("<html>"+ CartPanel.notificationString +"</html>");
		   
		refreshUI();

	}
	
	public void notifyMessage(String message) {
		System.out.println("message recieved: " + message);
		
		CartPanel.notificationString = message;
        notificationLabel.setForeground(Color.GRAY); // Set text color to red
		notificationLabel.setText("<html>"+ CartPanel.notificationString +"</html>");
		
		refreshUI();
	}
	
}
