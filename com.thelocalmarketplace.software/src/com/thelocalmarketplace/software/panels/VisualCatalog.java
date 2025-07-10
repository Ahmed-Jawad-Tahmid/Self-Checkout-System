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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import com.thelocalmarketplace.software.CustomerView;

import product.VisualCatalogDatabase;
import product.VisualProductInfo;

import java.net.URL;

public class VisualCatalog extends JPanelRefreshable {
	private CustomerView view;
	private VisualProductInfo[] productCatalog;
    private JLabel notificationLabel; // Notification label for product addition
	
	private void setCatalog() {
		productCatalog = VisualCatalogDatabase.getAllProducts();
		
	}

	public VisualCatalog(CustomerView view) {
		this.view=view;
		//set catalog
		setCatalog();
		
        setLayout(new BorderLayout());
        
		
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(0, 3, 10, 10)); // rows, cols, hgap, vgap

        // Scroll pane that will make the grid scrollable
        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        notificationLabel = new JLabel();
        notificationLabel.setHorizontalAlignment(JLabel.CENTER);
        notificationLabel.setForeground(Color.BLUE); 
        
        // Example of adding items to the grid
        for (int i = 0; i < productCatalog.length; i++) {
        	final JPanel itemPanel = createProductPanel(i);

            gridPanel.add(itemPanel);
        }


        add(scrollPane, BorderLayout.CENTER);
        
        JButton cancelButton = new JButton("Finish");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.switchToPanel("CartPanel");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelButton);

        add(notificationLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
    }

	private JPanel createProductPanel(int i) {
		VisualProductInfo product = productCatalog[i];
		
		final JPanel itemPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.NORTH;

		final JLabel titleLabel = new JLabel(product.getName(), JLabel.CENTER);
		itemPanel.add(titleLabel, gbc);

		final JLabel imageLabel = new JLabel("Loading image...");
		gbc.anchor = GridBagConstraints.CENTER;
		itemPanel.add(imageLabel, gbc);

		final JButton addButton = new JButton("Add Product");
		gbc.anchor = GridBagConstraints.SOUTH;
		itemPanel.add(addButton, gbc);
		
		SwingWorker<ImageIcon, Void> worker = new SwingWorker<ImageIcon, Void>() {
		    @Override
		    protected ImageIcon doInBackground() throws Exception {
		        URL imageUrl = new URL(product.getImageURL()); // Replace with actual image URL
		        BufferedImage originalImage = ImageIO.read(imageUrl);
		        Image scaledImage = originalImage.getScaledInstance(200, -1, Image.SCALE_SMOOTH); // Set width to 200 and preserve aspect ratio
		        return new ImageIcon(scaledImage);
		    }

		    @Override
		    protected void done() {
		        try {
		            ImageIcon imageIcon = get();
		            imageLabel.setIcon(imageIcon);
		            imageLabel.setText(""); // Remove the placeholder text
		        } catch (Exception e) {
		            System.out.println("Failed to load image URL <" + product.getImageURL() + ">");
		            imageLabel.setText("Failed to load image.");
		        }
		    }
		};
		worker.execute();

		addButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	try {
		    		view.getManager().addItemFromVisualCatalog(product);
		    		notificationLabel.setText(CartPanel.getNotificationString());	
		    	}
		    	catch (Exception error)
		    	{		    		
		    		notificationLabel.setText("Error: " + error.getMessage());
		    	}
		    }
		});
		return itemPanel;
	}
}