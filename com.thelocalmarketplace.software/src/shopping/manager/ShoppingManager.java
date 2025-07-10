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

package shopping.manager;

import shopping.interfaces.IPaymentListener;
import shopping.interfaces.IProductListener;
import shopping.manager.logic.AbstractShoppingLogic;
import shopping.manager.logic.BulkyItemLogic;
import shopping.manager.logic.BarcodedProductShoppingLogic;
import shopping.manager.logic.PLUCodedProductLogic;
import shopping.manager.logic.RefillLogic;

import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;
import com.tdc.CashOverloadException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispenserBronze;
import com.tdc.banknote.IBanknoteDispenser;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.panels.CartPanel;

import payment.PayGroceriesWithCoin;
import payment.PayWithBanknote;
import powerutility.PowerGrid;
import product.AddProductWithBarcode;
import product.AddProductWithPLUCode;
import product.AddProductWithVisualCatalog;
import product.ProductInfo;
import product.VisualProductInfo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.jjjwelectronics.*;


/**
 * The ShoppingManager class manages the shopping process in a self-checkout station,
 * including keeping track of groceries, their cost, expected weight, and handling
 * discrepancies between expected and actual weights.
 */
public class ShoppingManager implements IProductListener, IPaymentListener, ElectronicScaleListener{
	// list of groceries
	private Map<Product, ProductInfo> groceries = new HashMap<Product, ProductInfo>();
		
	protected BigDecimal groceriesCost = new BigDecimal(0);
	protected BigDecimal expectedWeight = new BigDecimal(0);	
	private BigDecimal currentGroceriesWeight = new BigDecimal(0);
	private boolean hasBeenPaid = false;	
	private AbstractSelfCheckoutStation station;
	
	private AddProductWithBarcode addProductWithBarcode;
	private AddProductWithVisualCatalog addProductWithVisualCatalog;
	private AddProductWithPLUCode addProductWithPLUCode;
	
	private PayGroceriesWithCoin payGroceriesWithCoin;
	private PayWithBanknote payWithBanknote;

	private AbstractShoppingLogic shoppingLogic;
	private AbstractShoppingLogic defaultShoppingLogic;
	private AbstractShoppingLogic bulkyItemLogic;
	private AbstractShoppingLogic pluCodeItemLogic;
	private AbstractShoppingLogic bagAddingLogic;
	private AbstractShoppingLogic barcodedProductLogic;
	private RefillLogic refillLogic = new RefillLogic(this);
	private boolean hasDiscrepancy;
	private boolean attendantApproved = false;
	
	private RecieptPrinter receiptPrinter;
	private String stationType;

	private ConfigCheckoutStation config;

	private int bagCount = 0;
	private Product recentProduct;
	private ProductInfo currentPendingProduct;
	
	/**
     * Constructs a new ShoppingManager instance associated with a given self-checkout station.
     * 
     * @param station the self-checkout station to associate with this manager
     */
	public ShoppingManager(String stationType, ConfigCheckoutStation config) {
		// assign station and plug in
		this.station = SelfCheckoutStationFactory.createSelfCheckoutStation(stationType, config);
		this.config = config;
		this.stationType = stationType;
		managerSetup();
	}
	
	public ShoppingManager(String stationType) {
		// assign station and plug in
		this.station = SelfCheckoutStationFactory.createSelfCheckoutStation(stationType, config = new ConfigCheckoutStation());
		managerSetup();
	}


	public void addBag(){
		this.bagCount++;
	}

	public int getBagCount() {
		return this.bagCount;
	}
	
	private void managerSetup() {
		PowerGrid.engageUninterruptiblePowerSource();
		getStation().plugIn(PowerGrid.instance());
		getStation().turnOn();
		
		// create and assign software
		addProductWithBarcode = new AddProductWithBarcode();
		getStation().getMainScanner().register(addProductWithBarcode);
		getStation().getHandheldScanner().register(addProductWithBarcode);
		addProductWithBarcode.addListener(this);
		
		addProductWithVisualCatalog = new AddProductWithVisualCatalog();
		addProductWithVisualCatalog.addListener(this);
		
		addProductWithPLUCode = new AddProductWithPLUCode();
		addProductWithPLUCode.addListener(this);

		
		getStation().getBaggingArea().register(this);
		
		//payment systems
		payGroceriesWithCoin = new PayGroceriesWithCoin(this);
		getStation().getCoinValidator().attach(payGroceriesWithCoin);
		payGroceriesWithCoin.addListener(this);
		payWithBanknote = new PayWithBanknote(this);
		getStation().getBanknoteValidator().attach(payWithBanknote);
		payWithBanknote.addListener(this);
		
		receiptPrinter = new RecieptPrinter(station.getPrinter());
		
		// create the shopping manager logic: 
		defaultShoppingLogic = new BarcodedProductShoppingLogic(this);
		pluCodeItemLogic = new PLUCodedProductLogic(this);
		bulkyItemLogic = new BulkyItemLogic(this);
		
		shoppingLogic = defaultShoppingLogic;
	}
	
	public void confirmAction() {
		shoppingLogic.userConfirmation();
	}
	
	public boolean isDefaultLogic()
	{
		return (shoppingLogic instanceof BarcodedProductShoppingLogic);
	}
	
	public AbstractShoppingLogic getLogic()
	{
		return (shoppingLogic);
	}
	
	public void setDefaultLogic(boolean force) {
		if (shoppingDiscrepancyHeader() == true) return;	
		if (!isDefaultLogic() && force == false) return;
		shoppingLogic = defaultShoppingLogic;
		currentPendingProduct = null;
		
		System.out.println("LOGIC: switched now to default logic");
	}
	
	public void setAddBulkyItemLogic() {
		if (shoppingDiscrepancyHeader() == true) return;
		if (!(isDefaultLogic())) return;
		shoppingLogic = bulkyItemLogic;
		currentPendingProduct = null;
		
		System.out.println("LOGIC: switched now to bulky item logic");
	}
	
	public void setPLUCodeLogic() {
		if (shoppingDiscrepancyHeader() == true) return;
		if (!(isDefaultLogic())) return;
		shoppingLogic = pluCodeItemLogic;
		currentPendingProduct = null;

		System.out.println("LOGIC: switched now to PLU code logic");
	}
	

	public void addItemFromVisualCatalog(VisualProductInfo p) {
		if (shoppingDiscrepancyHeader() == true) return;

		if (p.getProduct() instanceof BarcodedProduct)
		{
			setDefaultLogic(false);
		}
		if (p.getProduct() instanceof PLUCodedProduct)
		{
			setPLUCodeLogic();
		}
		
		addProductWithVisualCatalog.addProduct(p);
	}

	public void addItemFromPLUCOde(PLUCodedProduct p) {
		if (shoppingDiscrepancyHeader() == true) return;
		
		addProductWithPLUCode.addProduct(p);
	}

	public void attendantOverride() {
		//attendant overrides any weight expectations
		expectedWeight = getWeight();
		setDefaultLogic(true);
		System.out.println("Attendant Override Approved");
	}

	public void requestForHelp(String message) {
		System.out.println("HELP REQUESTED: " + message);
	}
	
	public boolean isAttendantApproved() {
		return attendantApproved;
	}

	public void setAttendantApproved(boolean attendantApproved) {
		this.attendantApproved = attendantApproved;
	}
	
	public Map<Product, ProductInfo> getGroceries() {
		return groceries;
	}
	
	/**
     * Marks the groceries as paid.
     */
	@Override
	public void notifyPayment() {
		if (shoppingDiscrepancyHeader() == true) return;
		hasBeenPaid = true;

		
		String receipt = RecieptPrinter.getReciept(groceries, groceriesCost);
		for (char c : receipt.toCharArray())	
			try {
				receiptPrinter.print(c);
			} catch (EmptyDevice | OverloadedDevice e) {
				e.printStackTrace();
			}
		receiptPrinter.cutPaper();
		System.out.println(receiptPrinter.removeReceipt());
	}

	private boolean shoppingDiscrepancyHeader() {
		updateShoppingDevices();
		return hasShoppingDiscrepancy();
	}
	
	
	public void reset() {
		groceries.clear();
		groceriesCost = new BigDecimal(0);
		expectedWeight = new BigDecimal(0);	
		currentGroceriesWeight = new BigDecimal(0);
		hasBeenPaid = false;
		bagCount = 0;
		setDefaultLogic(true);
	}
		
	/**
     * Checks if the groceries have been paid for.
     * 
     * @return true if groceries have been paid for, false otherwise
     */
	public boolean isGroceriesPaid() {
		return hasBeenPaid;
	}
	
	/**
     * Retrieves the total cost of the groceries.
     * 
     * @return the total cost of the groceries
     */
	public BigDecimal getGroceriesCost() {
		return groceriesCost;
	}
	
	public ProductInfo getCurrentPendingProduct() {
		return currentPendingProduct;
	}
	public void setPendingProductWeight(BigDecimal weight) {
		currentPendingProduct.setWeight(weight);
	}
	public void setPendingProductPrice(BigDecimal price) {
		currentPendingProduct.setPrice(price);
	}
	public void setPendingProductBulky(boolean isBulky) {
		currentPendingProduct.setBulky(isBulky);;
	}
	
	/**
     * Adds a product to the grocery list and updates related information.
     * 
     * @param p the product to add
     * @param weight the weight of the product
     */
	public void addProduct(Product p, String name) {
		System.out.println("LOGIC: Current logic is " + shoppingLogic);
		if (shoppingDiscrepancyHeader() == true) return;

		if (shoppingLogic.canAddProduct() == false)
		{
			System.out.println("SYSTEM: Cannot add product in this state, possibly a deliberate design choice");
			return;
		}
		
		disableDevices();

		currentPendingProduct = getGroceries().get(p);
		
		if (currentPendingProduct == null) {
			currentPendingProduct = new ProductInfo(1, new BigDecimal(0), name);
		} else {
			currentPendingProduct.setCount(currentPendingProduct.getCount() + 1);
		}
		
		getGroceries().put(p, currentPendingProduct);
		setRecentProduct(p);
				
		shoppingLogic.addedProduct(p);
	}
	
	public void removeProduct(Product p, int amount) {
		if (shoppingLogic.canRemoveProduct() == false)
		{
			throw new UnsupportedOperationException("Cannot remove product in this state");
		}
		
		if (!getGroceries().containsKey(p)) {
			System.out.println("Groceries does not contain the product you are trying to remove");
		} else if (getGroceries().get(p).getCount() >= 1) {
			int newCount = getGroceries().get(p).getCount() - 1;
			getGroceries().get(p).setCount(newCount);
			
			currentPendingProduct = getGroceries().get(p);

			expectedWeight = shoppingLogic.removeProductNewExpectedWeight(currentPendingProduct, amount);

			ProductInfo productInfo = getGroceries().get(p);
			groceriesCost = shoppingLogic.removeProductNewCosts(productInfo, amount);

			if (getGroceries().get(p).getCount() == 0) {
				getGroceries().remove(p);
			}
		} 
		
		shoppingLogic.removedProduct(p);
		disableDevices();
		System.out.println("Shopping manager: product removed from cart");
	}

	public void disableDevices() {
		getStation().getMainScanner().disable();
		getStation().getHandheldScanner().disable();
		getStation().getCoinSlot().disable();
	}

	public void enableDevices() {
		getStation().getMainScanner().enable();
		getStation().getHandheldScanner().enable();
		getStation().getCoinSlot().enable();
	}

	/**
     * Retrieves the current weight of the groceries from the electronic scale.
     * 
     * @return the current weight of the groceries in grams
     * @throws OverloadedDevice if the mass exceeds the scale's limit
     */
    public BigDecimal getWeight() {
    	// Get current weight from the bagging area
    	return currentGroceriesWeight;
    }
    
	

    /**
     * Retrieves the expected total weight of the groceries.
     * 
     * @return the expected total weight of the groceries
     */
	public BigDecimal getExpectedWeight() { 
		return expectedWeight;	    
	}
	
	public void refreshGroceryInfo() {
		expectedWeight = new BigDecimal(0);
		groceriesCost = new BigDecimal(0);
		for (Product p : getGroceries().keySet()) {
			ProductInfo info = getGroceries().get(p);
			BigDecimal weight = info.getWeight().multiply(new BigDecimal(info.getCount()));
			BigDecimal cost = info.getPrice().multiply(new BigDecimal(info.getCount()));;
			
			expectedWeight = expectedWeight.add(weight);
			groceriesCost = groceriesCost.add(cost);
		}
	}
     
	
	/**
     * Handles changes in the mass on the scale.
     * 
     * @param scale the electronic scale
     * @param mass the new mass measurement
     */
	@Override
	public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
		// set new weight
		System.out.println("the mass " + mass.inGrams());
		setWeight(mass.inGrams());
		shoppingLogic.scaleChanged(mass.inGrams());
		updateShoppingDevices();
	}
	
	
	/**
	 * checks for a discrepancy between the expected and the actual weight of the groceries
	 *
	 * @return true if there is a discrepancy, false otherwise
	 * @throws OverloadedDevice if the mass exceeds the scale's limit
	 */
	
	public boolean hasWeightDiscrepancy() throws OverloadedDevice {
		double currentWeight = getWeight().doubleValue();
		double expectedWeight = getExpectedWeight().doubleValue();
		if (Math.abs(currentWeight - expectedWeight) <= Double.MIN_VALUE) {
			return false;
		} else {
			return true;
		}
	}
	
	public boolean hasShoppingDiscrepancy() {
		if (shoppingLogic.hasDiscrepanciesProduct()) {
			return true;
		}
		return false;
	}
	
	private void updateShoppingDevices() {
		if (hasShoppingDiscrepancy()) disableDevices();
		else enableDevices();
	}
	
	private void setWeight(BigDecimal newWeight) {
		this.currentGroceriesWeight = newWeight;
	}
	
	public Product getProductByName(String name) {
	    for (Map.Entry<Product, ProductInfo> entry : getGroceries().entrySet()) {
	        if (entry.getValue().getName().equals(name)) {
	            return entry.getKey();
	        }
	    }
	    return null; // Return null if no product with the given name is found
	}

	/**
     * Gets the SelfCheckoutStation associated with this ShoppingManager.
     * 
     * @return the SelfCheckoutStation instance
     */
	public AbstractSelfCheckoutStation getStation() {
		return station;
	}
	
	public void setRecentProduct(Product p) {
		this.recentProduct = p;
	}
	
	public Product getRecentProduct() {
		return recentProduct;
	}
	
	public RefillLogic getRefillLogic() {
		return refillLogic;
	}

	
	// Unused Methods 
	/**
     * Called when a device has been enabled.
     * 
     * @param device the device that has been enabled
     */
	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub
		
	}
	
	/**
     * Called when a device has been disabled.
     * 
     * @param device the device that has been disabled
     */
	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
		
	}
	
	/**
     * Called when a device has been turned on.
     * 
     * @param device the device that has been turned on
     */
	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
		
	}
	
	/**
     * Called when a device has been turned off.
     * 
     * @param device the device that has been turned off
     */
	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		
	}
	
	
	/**
     * Called when the mass on the scale exceeds its limit.
     * 
     * @param scale the electronic scale whose mass limit has been exceeded
     */
	@Override
	public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
		
	}

	/**
     * Called when the mass on the scale no longer exceeds its limit.
     * 
     * @param scale the electronic scale whose mass no longer exceeds the limit
     */
	@Override
	public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
		
	}
	
	public PayWithBanknote getPayWithBanknote() { return payWithBanknote; }
	public PayGroceriesWithCoin getPayWithCoin() { return payGroceriesWithCoin; }

	@Override
	public void notifyPaidAmountChanged() {}

	@Override
	public void notifyDanglingBanknote() {}
}