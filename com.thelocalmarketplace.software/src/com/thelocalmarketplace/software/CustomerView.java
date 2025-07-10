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

package com.thelocalmarketplace.software;
import java.awt.CardLayout;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import com.jjjwelectronics.Item;
import com.thelocalmarketplace.software.panels.*;
import com.thelocalmarketplace.software.panels.CreditPanel;

import payment.AddOwnBags;
import shopping.manager.ShoppingManager;

public class CustomerView {
	private ShoppingManager manager;
	private JFrame customerFrame;
	private HashMap<String, JPanelRefreshable> panels;
	private CardLayout cardLayout;
	private String previousPanel;
	private AddOwnBags bags;
	private Item currentItem;
	private Map<String, Item> itemsOnScale;
	private String recentProductName;

	public String getCurrentPanelId() {
        for (Map.Entry<String, JPanelRefreshable> entry : panels.entrySet()) {
            if (entry.getValue().isVisible()) {
                return entry.getKey();
            }
        }
        return null; // return null if no panel is visible 
    }
	private JFrame additionalFrame;
	private IRLPanel IRL;
		
	public CustomerView(ShoppingManager sm) {
		this.manager = sm;
		this.itemsOnScale = new HashMap<>();
		bags = new AddOwnBags(sm);
		
		// initialize IRL window
		// Initialize the additional frame
        additionalFrame = new JFrame("IRL");
        additionalFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        additionalFrame.setSize(500, 500); // Adjust size as needed

        // Initialize the additional panel and pass this CustomerView instance to it
        IRL = new IRLPanel(this);
        additionalFrame.add(IRL);

        // Show the additional frame
        additionalFrame.setVisible(false);
		
		customerFrame = new JFrame("Customer View");
		customerFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		customerFrame.setSize(1000, 700);
		
		panels = new HashMap<>();
		cardLayout = new CardLayout();
		customerFrame.setLayout(cardLayout);
		
		addAllPanels();
		
		// add all panels to frame
		for (Map.Entry<String, JPanelRefreshable> entry : panels.entrySet()) {
			customerFrame.add(entry.getValue(), entry.getKey());
		}
		
		// initial panel
		cardLayout.show(customerFrame.getContentPane(), "StartSessionPanel");
		previousPanel = "StartSessionPanel";
	}

	private void addAllPanels() {
		// initialize panels and add them to the map and frame
		BlockedPanel blocked = new BlockedPanel(this);
		panels.put("BlockedPanel", blocked);
		
		StartSessionPanel start = new StartSessionPanel(this);
		panels.put("StartSessionPanel", start);
		
		CartPanel.setView(this);
		CartPanel cart = CartPanel.newInstance();
		panels.put("CartPanel", cart);
		
		AddItems add = new AddItems(this);
		panels.put("AddItems", add);
		
		VisualCatalog visualCatalog = new VisualCatalog(this);
		panels.put("VisualCatalog", visualCatalog);
		
		EnterPLUCode enterPLUCode = new EnterPLUCode(this);
		panels.put("EnterPLUCode", enterPLUCode);

		PaymentOptions payOpt = new PaymentOptions(this);
		panels.put("PaymentOptions", payOpt);
		
		InsertBanknotes inBanknotes = new InsertBanknotes(this);
		panels.put("InsertBanknotes", inBanknotes);
		
		InsertCoins inCoins = new InsertCoins(this);
		panels.put("InsertCoins", inCoins);
		
		RemoveItemPanel remove = new RemoveItemPanel(this);
		panels.put("RemoveItem", remove);
		
		CreditPanel credit = new CreditPanel(this);
		panels.put("CreditPanel", credit);
		
		DebitPanel debit = new DebitPanel(this);
		panels.put("DebitPanel", debit);
		
		Success12 success = new Success12(this);
		panels.put("Success12", success);
	}
	
	public void switchToPanel(String panelId) {
		if (!panelId.equals("BlockedPanel")) {
			previousPanel = panelId;
		}
		cardLayout.show(customerFrame.getContentPane(), panelId);
		panels.get(panelId).refreshUI();
        System.out.println("Now visible: " + panelId);
	}
	
	public void setVisible(boolean visible) {
		customerFrame.setVisible(visible);
        additionalFrame.setVisible(visible);
	}
	
	public JPanelRefreshable getCurrentPanel() {
        for (Map.Entry<String, JPanelRefreshable> entry : panels.entrySet()) {
            if (entry.getValue().isVisible()) {
                return entry.getValue();
            }
        }
        return null;
	}
	
	public ShoppingManager getManager() {
		return manager;
	}
	
	public String getPreviousPanel() {
		return previousPanel;
	}
	
	public void setCurrentItem(Item item) {
		this.currentItem = item;
	}
	
	public Item getCurrentItem() {
		return currentItem;
	}
	
	public Map<String, Item> getItemsOnScale() {
		return itemsOnScale;
	}

	public String getRecentProductName() {
		return recentProductName;
	}
	
	public void setRecentProductName(String name) {
		this.recentProductName = name;
	}
	
	public Map<String, JPanelRefreshable> getPanels() {
		return panels;
	}

}
