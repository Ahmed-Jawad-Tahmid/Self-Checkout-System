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

import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.panels.CartPanel;

import product.ProductInfo;
import shopping.manager.ShoppingManager;

public class PLUCodedProductLogic extends AbstractShoppingLogic {

	public PLUCodedProductLogic(ShoppingManager manager) {
		super(manager);
		// TODO Auto-generated constructor stub
	}
	
	private boolean addedItem = false;
	private BigDecimal originalWeight;
	private long costPerKilo;
	
	@Override
	public boolean canAddProduct()
	{
		addedItem = shoppingManager.getCurrentPendingProduct() != null;
		if (addedItem == true)
		{
			CartPanel.getInstance().notifyMessage("Do not add another product. Please place the PLU product on the scale for pricing");
		}
		return addedItem == false;
	} 
	
	@Override
	public boolean canRemoveProduct()
	{
		addedItem = shoppingManager.getCurrentPendingProduct() != null;
		if (addedItem == true)
		{
			CartPanel.getInstance().notifyMessage("You cannot remove item as of now. Please place the PLU product on the scale for pricing");
		}
		return addedItem == false;
	}
	
	@Override
	public void addedProduct(Product p) {		
		// get original weight
		originalWeight = currentExpectedWeight();
		costPerKilo = p.getPrice();
		
		// signal attendant
		CartPanel.getInstance().notifyMessage("Please add the product to the scale to determine weight");
	}
	
	@Override 
	public void scaleChanged(BigDecimal totalWeight)
	{
		addedItem = shoppingManager.getCurrentPendingProduct() != null;
		if (addedItem == false)
		{
			return;
		}
		int itemMassInGrams = totalWeight.subtract(originalWeight).intValue();
		System.out.println("this is the new mass: " + itemMassInGrams);
		if (itemMassInGrams > 0)
		{
			//calculate the cost of a PLU product
			BigDecimal costPer = new BigDecimal(costPerKilo);
			BigDecimal cost = totalWeight.multiply(costPer).divide(new BigDecimal(1000));
			
			shoppingManager.setPendingProductPrice(cost);
			shoppingManager.setPendingProductWeight(new BigDecimal(itemMassInGrams));
			
			//finished
			CartPanel.getInstance().notifyMessage("Product has been weighted and added to the total");
			shoppingManager.setDefaultLogic(true);
			shoppingManager.refreshGroceryInfo();
		}
	}
	
	@Override 
	public boolean hasDiscrepanciesProduct() {
		return false;
	}
}
