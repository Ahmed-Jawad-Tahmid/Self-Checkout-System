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
import java.util.NoSuchElementException;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import shopping.interfaces.IProductListener;

/**
 * Handles adding products with barcodes by listening to barcode scans
 * and notifying registered product listeners upon successful scans.
 */
public class AddProductWithBarcode extends AbstractProductAdder implements BarcodeScannerListener {
	
	// create list of IProductListeners
	public List<IProductListener> productListeners = new ArrayList<>();
	private boolean turnedOn = true;		
	private boolean enabled = true;


	 /**
     * Processes a barcode scan. If the barcode corresponds to a known product,
     * notifies all registered listeners with the product and its expected weight.
     *
     * @param barcodeScanner The barcode scanner that detected the barcode.
     * @param barcode The barcode that was scanned.
     */
	@Override
	public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {
		if (turnedOn == false) return;
		if (enabled == false) return;
		// System: Detects a barcode.
		// get the product associated with the barcode from product database.
		BarcodedProduct product = ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode);
		
		if (product == null)
		{
			throw new NoSuchElementException("No product for the given barcode");
		}
		
		String name = product.getDescription();
		// notify the listeners to add the product
		notifyListeners(product, name);
	}
	
	
	/**
     * Enables the device, allowing it to start scanning operations if also turned on.
     * @param device The device that has been enabled.
     */
	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
		enabled = true;
		
	}

	 /**
     * Disables the device, preventing it from scanning operations even if turned on.
     * @param device The device that has been disabled.
     */
	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
		enabled = false;
		
	}

	/**
     * Turns on the device, allowing it to perform operations if also enabled.
     * @param device The device that has been turned on.
     */
	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
		turnedOn = true;
		
	}

	/**
     * Turns off the device, stopping all operations regardless of its enabled state.
     * @param device The device that has been turned off.
     */
	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		turnedOn = false;
		
	}

}
