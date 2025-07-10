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

package product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.thelocalmarketplace.hardware.Product;

import shopping.interfaces.IProductListener;

public abstract class AbstractProductAdder {
	protected List<IProductListener> productListeners = new ArrayList<>();
	
	/**
     * Registers a new listener interested in product additions.
     *
     * @param i The listener to register.
     */
	public void addListener(IProductListener i) {
		this.productListeners.add(i);
	}
	
	/**
     * Unregisters a listener from being notified about product additions.
     *
     * @param i The listener to unregister.
     */
	public void removeListener(IProductListener i) {
		this.productListeners.remove(i);
	}
	
	protected void notifyListeners(Product product, String name) {
		for (IProductListener listener : productListeners) {
			System.out.println("notifying manager");
			listener.addProduct(product, name);
		}
	}
	
}
