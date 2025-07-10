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

package shopping.manager.logic;

import java.math.BigDecimal;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.panels.CartPanel;

import product.ProductInfo;
import shopping.manager.ShoppingManager;

/*
 * Holds the default logic for how a shopping manager will handle it's core functions
 * Think of this class as a template for the basic shopping functions, and you can 
 * override the functions to create unique logic depending on the shopping status
 * 
 * By default, it holds the information for a barcoded product scanning.
 */
public abstract class AbstractShoppingLogic {
	protected ShoppingManager shoppingManager;
	private BigDecimal currentWeight = new BigDecimal(0);
	
	public AbstractShoppingLogic(ShoppingManager manager) {
		this.shoppingManager = manager;
	}

	protected ShoppingManager getShoppingManager() {
		return this.shoppingManager;
	}
	
	//use for verification from the customer end, such as "Customer: Signals that the bags have been added."
	public void userConfirmation() {
		
	}
	
	public void scaleChanged(BigDecimal m) {
		currentWeight = m;
	}
	
	
	public boolean hasDiscrepanciesProduct() {
		// check for weight discrepancy
		try {
			if (shoppingManager.hasWeightDiscrepancy()) {
				// signal to customer/attendant that there is a weight discrepancy
				System.out.println("SYSTEM: There is a weight discrepancy");
				CartPanel.getInstance().notifyError("There is a weight discrepancy. Likely because a product was scanned, but not placed on the scale.");
				return true;
			} else {
				// if there is no discrepancy, re-enable devices if they were disabled
				System.out.println("SYSTEM: There is no weight discrepancy");
				CartPanel.getInstance().notifyMessage("Continue shopping!");
				enableStation();
				return false;
			}
		} catch (OverloadedDevice e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean hasDiscrepanciesRemoveProduct() {
		return false;
	}
	
	//Add Product
	public boolean canAddProduct()
	{
		return true;
	}
	
	public void addedProduct(Product p) {
		// Signal customer to place item in bagging area
		shoppingManager.setPendingProductPrice(new BigDecimal(p.getPrice()));
		
		BarcodedProduct product = (BarcodedProduct) p; //can do this because default logic only handles barcoded items
		shoppingManager.setPendingProductWeight(new BigDecimal(product.getExpectedWeight()));
		
		shoppingManager.refreshGroceryInfo();
		System.out.println("SYSTEM: Please put the scanned item into the bagging area");
		CartPanel.getInstance().notifyMessage("Please put the scanned item into the bagging area");
		shoppingManager.setDefaultLogic(true);
	}
		
	
	//Remove Product 
	public boolean canRemoveProduct()
	{
		return true;
	}
	
	public BigDecimal removeProductNewExpectedWeight(ProductInfo info, int amount) {		
		BigDecimal productCount = BigDecimal.valueOf(amount);
		BigDecimal weightToRemove = info.getWeight().multiply(productCount);
		
		BigDecimal expectedWeight = currentExpectedWeight();
		if (!info.isBulky())
		{
			expectedWeight = expectedWeight.subtract(weightToRemove);
		}
		return expectedWeight;	
	}
	
	public BigDecimal removeProductNewCosts(ProductInfo p, int amount) {
		BigDecimal price = new BigDecimal(amount).multiply(p.getPrice());
		BigDecimal newCosts = currentGroceriesCost().subtract(price);
		return newCosts;
	}
	
	public void removedProduct(Product p) {
		System.out.println("SYSTEM: Please remove the item from the bagging area");
		CartPanel.getInstance().notifyMessage("Product has been removed, if it was on the bagging area, please remove it.");
	}
	
	
	protected BigDecimal currentExpectedWeight() {
		return shoppingManager.getExpectedWeight();
	}

	protected BigDecimal currentActualWeight() {
		return shoppingManager.getWeight();
	}
	
	private BigDecimal currentGroceriesCost() {
		return shoppingManager.getGroceriesCost();
	}
	
	protected void enableStation() {
		shoppingManager.enableDevices();
	}
	
	private void disableStation() {
		shoppingManager.disableDevices();
	}
	
}
