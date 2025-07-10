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


 package payment;
 import java.math.BigDecimal;
 import java.util.Map;
import java.util.Random;
import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Currency;
 import java.util.List;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.tdc.CashOverloadException;
import com.tdc.NoCashAvailableException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
 import com.tdc.IComponentObserver;
 import com.tdc.coin.Coin;
 import com.tdc.coin.CoinSlot;
 import com.tdc.coin.CoinStorageUnit;
 import com.tdc.coin.CoinValidator;
 import com.tdc.coin.CoinValidatorObserver;
 import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.Product;

import product.ProductInfo;
import shopping.interfaces.IPaymentListener;
import shopping.manager.ConfigCheckoutStation;
import shopping.manager.RecieptPrinter;
import shopping.manager.ShoppingManager;
 
 public class PayGroceriesWithCoin implements CoinValidatorObserver{
	private ShoppingManager shoppingManager;
	private List<IPaymentListener> paymentListeners = new ArrayList<>();
	private BigDecimal groceriesCost;// The cost of full groceries list. It will continue to decrease the more coins are inserted
	Map <BigDecimal, ICoinDispenser> dispenser;
	public CoinSlot coinSlot;
	private final Random pseudoRandomNumberGenerator = new Random();
	private static final int PROBABILITY_OF_FALSE_REJECTION = 1; /* out of 100 */
	public CoinStorageUnit storage;
	public Map<BigDecimal, ICoinDispenser> coinDispensers;	 
	boolean flag=false;// If cost of groceries have not been paid in full, this will stay false.
	RecieptPrinter printer;
	Map<Product, ProductInfo> groceries;// Map of all groceries
	BigDecimal fullGroceriesCost;// The cost of full groceries. We need this so that we can get the receipt for it. 
	Currency DEFAULT_CURRENCY=Coin.DEFAULT_CURRENCY;
	AbstractSelfCheckoutStation station;
	List <BigDecimal>coinDenom;
	CoinValidator validator;
	private BigDecimal paymentReceived;

	/**
		  * A virtual payment system for the self checkout station
		  * @param sm the ShoppingManager 
		  */
 
	 public PayGroceriesWithCoin(ShoppingManager sm) {
		 this.shoppingManager = sm;
		 this.groceriesCost = shoppingManager.getGroceriesCost();
		 fullGroceriesCost = shoppingManager.getGroceriesCost();
		 coinDenom = sm.getStation().getCoinDenominations();
		 this.groceries = shoppingManager.getGroceries();
		 this.coinDispensers = shoppingManager.getStation().getCoinDispensers();
		 this.paymentReceived = BigDecimal.ZERO;
		 // Add this to the list of observers of every coinValidator
	 }
	 
//	 Currency currency = Currency.getInstance("CAD"); // Example currency
//	 List<BigDecimal> denominations = Arrays.asList(new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1.00"), new BigDecimal("2.00")); // Example denominations

	 /*
	  * This method does the calculations depending on the coin inserted and decreases the groceries' cost
	  */
	 @Override
	 public void validCoinDetected(CoinValidator validator, BigDecimal value) {
		 if (this.shoppingManager.isGroceriesPaid()) {
			 System.out.println("Transaction already complete, no need to insert more coins");
			 try {
				giveChange(value);
			} catch (CashOverloadException | NoCashAvailableException | DisabledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException("No cash available");
			}
		 } else {
			 this.paymentReceived = this.paymentReceived.add(value);
			 if (this.paymentReceived.compareTo(this.shoppingManager.getGroceriesCost()) > 0) {
				 System.out.println("Dispensing change");
				 try {
					giveChange(this.paymentReceived.subtract(this.shoppingManager.getGroceriesCost()));
				} catch (CashOverloadException | NoCashAvailableException | DisabledException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException("No cash available");
				}
				 completePayment();
			 } else if (this.paymentReceived.compareTo(this.shoppingManager.getGroceriesCost()) == 0) {
				 System.out.println("Perfect Payment");
				 completePayment();
			 } else {
				 System.out.println("Amount remaining: " + this.shoppingManager.getGroceriesCost().subtract(this.paymentReceived));
			 	}
		 	}
	 	}
	 //Complete Payment 
	 private void completePayment() {
		 for (IPaymentListener listener : paymentListeners) {
			 listener.notifyPayment();
		 }
//		 boolean paymentComplete = true; // Set payment as complete
		 System.out.println("Payment succesfull");
	 }
	 
	 public void giveChange(BigDecimal change) throws CashOverloadException, NoCashAvailableException, DisabledException {
		    // sort the coin dispensers (biggest first)
		    List<Map.Entry<BigDecimal, ICoinDispenser>> sortedDispensers = new ArrayList<>(((Map<BigDecimal, ICoinDispenser>) coinDispensers).entrySet());
		    sortedDispensers.sort((a, b) -> b.getKey().compareTo(a.getKey()));

		    // from biggest to smallest give coin
		    for (Map.Entry<BigDecimal, ICoinDispenser> entry : sortedDispensers) {
		        BigDecimal coinValue = entry.getKey();
		        ICoinDispenser dispenser = entry.getValue();

		        while (change.compareTo(coinValue) >= 0 && !(dispenser.getCapacity()==0)) {
		        	try {
		                dispenser.emit();
		            } catch (NoCashAvailableException e) {
		                throw new NoCashAvailableException();
		            }
		            change = change.subtract(coinValue);
		        }
		    }
		    if (change.compareTo(BigDecimal.ZERO) > 0) {
		        throw new NoCashAvailableException();
		    }
		}
	 
	 /**
	  * Registers a new listener interested in payment notifications.
	  *
	  * @param i The listener to register.
	  */
	 public void addListener(IPaymentListener i) {
		 this.paymentListeners.add(i);
	 }
	 
	 /**
	  * Unregisters a listener from being notified about payment notifications.
	  *
	  * @param i The listener to unregister.
	  */
	 public void removeListener(IPaymentListener i) {
		 this.paymentListeners.remove(i);
	 }
 
	 @Override
	 public void invalidCoinDetected(CoinValidator validator) {
		 // This is handled by the hardware
		 System.out.println("Invalid coin, please try again or insert a different coin");
	 }
 
 
	 @Override
	 public void enabled(IComponent<? extends IComponentObserver> component) {
		 // TODO Auto-generated method stub
		 
	 }
 
 
	 @Override
	 public void disabled(IComponent<? extends IComponentObserver> component) {
		 // TODO Auto-generated method stub
		 
	 }
 
 
	 @Override
	 public void turnedOn(IComponent<? extends IComponentObserver> component) {
		 // TODO Auto-generated method stub
		 
	 }
 
 
	 @Override
	 public void turnedOff(IComponent<? extends IComponentObserver> component) {
		 // TODO Auto-generated method stub
		 
	 }
	 

	 public BigDecimal getPaymentReceived() {
		return paymentReceived;
	 }
 
 }
 