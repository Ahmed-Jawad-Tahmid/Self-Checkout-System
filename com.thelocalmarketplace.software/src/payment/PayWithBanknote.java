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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.NoCashAvailableException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteDispensationSlotObserver;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;
import com.tdc.banknote.IBanknoteDispenser;

import shopping.interfaces.IPaymentListener;
import shopping.manager.ShoppingManager;

public class PayWithBanknote implements BanknoteValidatorObserver {
	// Fields for basic functionality
	private ShoppingManager shoppingManager;
	private List<IPaymentListener> paymentListeners = new ArrayList<>();
	private BigDecimal paymentRecieved;

	// Fields for getting change
	Map<BigDecimal, IBanknoteDispenser> dispensers;
	private BigDecimal closestToTotal = BigDecimal.valueOf(1000);
	

	/**
	 * A new instance that can handle payments with bank notes
	 * 
	 * @param sm: The shopping manager
	 */
	public PayWithBanknote(ShoppingManager sm) {
		this.shoppingManager = sm;
		this.paymentRecieved = BigDecimal.ZERO;
	}

	public BigDecimal getPaymentRecieved() { return paymentRecieved; }

	/**
	 * Uses the observer method to detect when a bank note is inserted, reduces
	 * total cost by the denomination, and checks if payment is finished
	 * 
	 * @param validator    The Banknote Validator
	 * @param currency     The currency of the inserted note
	 * @param denomination The denomination of the inserted note
	 */
	@Override
	public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal denomination) {
		// Check that groceries are not paid yet
		if (this.shoppingManager.isGroceriesPaid()) {
			System.out.println("Transaction is already complete, no need to insert more cash");
			produceChange(denomination);
		} else {
			// Increase amount paid by denomination
			this.paymentRecieved = this.paymentRecieved.add(denomination);
			// If done paying, check if change is needed and dispense it, else tell system
			// it is paid
			if (this.paymentRecieved.compareTo(this.shoppingManager.getGroceriesCost()) > 0){
				System.out.println("Change dispensing");
				produceChange(this.paymentRecieved.subtract(shoppingManager.getGroceriesCost()));
				notifyPaymentListeners();
				notifyPaidAmountChangedListeners();
			} else if (this.paymentRecieved.compareTo(this.shoppingManager.getGroceriesCost()) == 0) {
				System.out.println("Perfect Payment");
				notifyPaymentListeners();
				notifyPaidAmountChangedListeners();
			} else {
				System.out.println("Amount remaining: " + this.shoppingManager.getGroceriesCost().subtract(this.paymentRecieved));
				notifyPaidAmountChangedListeners();
			}
		}
	}

	private void notifyPaymentListeners() {
		// Notify the payment listeners
		for (IPaymentListener listener : paymentListeners) {
			listener.notifyPayment();
		}
	}
	private void notifyPaidAmountChangedListeners() {
		// Notify the payment listeners
		for (IPaymentListener listener : paymentListeners) {
			listener.notifyPaidAmountChanged();
		}
	} 

	/**
	 * This method looks through all dispenser's denominations and emits notes one
	 * by one depending on which bills were the closest to the change needed
	 * 
	 * @param changeToProduce The amount of change that must be given to the
	 *                        customer
	 */
	private void produceChange(BigDecimal changeToProduce) {
		dispensers = this.shoppingManager.getStation().getBanknoteDispensers();
		
		ArrayList<BigDecimal> denominations = new ArrayList<BigDecimal>();
		for (BigDecimal denomination : dispensers.keySet()) {
			denominations.add(denomination);
		}
		Collections.sort(denominations, Collections.reverseOrder());
		
		long totalChange = changeToProduce.longValue();
		for (BigDecimal denomination : denominations) {
			long denom = denomination.longValue();
			
			long remainderIfDivided = totalChange % denom;
			long changeWithoutRemainder = totalChange - remainderIfDivided;
			long timesDenominationCanFit = changeWithoutRemainder / denom;
			
			for (int i = 0; i < timesDenominationCanFit; i++)
			{	 
				emitBanknoteDispenser(denomination);
			}
			totalChange -= timesDenominationCanFit*denom;
		}
			shoppingManager.getStation().getBanknoteOutput().dispense();
			
		if (totalChange != 0)
		{
			System.out.println("Could not give exact bank note change (this is not a bug)");
		}
	}

	private void emitBanknoteDispenser(BigDecimal denomination) {
		try {
			dispensers.get(denomination).emit();
		} catch (NoCashAvailableException e) {
			throw new RuntimeException("No cash in dispenser");
		} catch (DisabledException e) {
			throw new RuntimeException("Dispenser disabled");
		} catch (CashOverloadException e) {
			throw new RuntimeException("Too many notes in sink");
		}
	}

	/**
	 * If an invalid bank note is inserted, this method will tell the user to try
	 * again or insert a different bank note
	 * 
	 * @param validator The bank note validator where this behaviour was found
	 */
	@Override
	public void badBanknote(BanknoteValidator validator) {
		System.out.println("Invalid note detected, please try again or insert a different bill");
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


	// Unused methods implemented from BanknoteValidatorObserver

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



}
