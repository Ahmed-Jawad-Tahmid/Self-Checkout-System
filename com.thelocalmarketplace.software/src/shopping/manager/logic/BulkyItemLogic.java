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

import com.jjjwelectronics.OverloadedDevice;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.panels.CartPanel;

import product.ProductInfo;
import shopping.manager.ShoppingManager;

public class BulkyItemLogic extends AbstractShoppingLogic {

	public BulkyItemLogic(ShoppingManager manager) {
		super(manager);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean hasDiscrepanciesProduct() {
		// check for weight discrepancy
		try {
			if (shoppingManager.hasWeightDiscrepancy()) {
				// signal to customer/attendant that there is a weight discrepancy
				System.out.println("SYSTEM: There is a weight discrepancy");
				CartPanel.getInstance().notifyError("There is a weight discrepancy");
				return true;
			} else {
				// if there is no discrepancy, re-enable devices if they were disabled
				System.out.println("SYSTEM: There is no weight discrepancy");
				enableStation();
				return false;
			}
		} catch (OverloadedDevice e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean canAddProduct() {
		if (!shoppingManager.isAttendantApproved()) {
			CartPanel.getInstance().notifyMessage("You must wait for attendant approval");
			return false;
		}
		return true;
	}
	
	@Override
	public void addedProduct(Product p) {		
		// set as a bulky item
		shoppingManager.setPendingProductBulky(true);
		
		// signal attendant
		System.out.println("SYSTEM: NO-BAGGING request accepted. Please do not put the item in the bagging area");
		CartPanel.getInstance().notifyMessage("Bulky item request accepted. Please do not put the item in the bagging area, and please continue shopping");
		// reset everything
		shoppingManager.setAttendantApproved(false);
		shoppingManager.setDefaultLogic(true);
	}
	
	@Override
	public boolean canRemoveProduct() {
		CartPanel.getInstance().notifyError("You cannot remove product when adding bulky item");
		return false;
	}

}
