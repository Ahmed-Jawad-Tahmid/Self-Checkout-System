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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.jjjwelectronics.card.AbstractCardReader;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.Card.CardData;
import com.thelocalmarketplace.hardware.external.CardIssuer;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import shopping.interfaces.IPaymentListener;
import shopping.manager.ShoppingManager;

import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.card.CardReaderListener;
import com.jjjwelectronics.card.ChipFailureException;
import com.jjjwelectronics.card.InvalidPINException;
import com.jjjwelectronics.card.MagneticStripeFailureException;

import powerutility.NoPowerException;

/**
 * Handles the process of making a payment using a debit card.
 * PayWithDebit collaborates with shopping manager to retrieve the total cost
 * and update payment status,
 * and with card issuer for authorization.
 * 
 */
public class PayWithDebit {
    private ShoppingManager shoppingManager;
    private List<IPaymentListener> paymentListeners = new ArrayList<>();
    protected BigDecimal groceriesCost;
    private BigDecimal amountPaid = BigDecimal.ZERO;
    protected BigDecimal amountDue;
    private boolean paidFull = false;
    private CardIssuer issuer;
    boolean signatureValid = false;

    /**
     * Constructs a PayWithDebit object with a specified shopping manager and card
     * issuer.
     * Initializes the groceries cost based on the shopping manager's total.
     *
     * @param sm The shopping manager handling the grocery transaction.
     * @param issuer The card issuer used for payment authorization and transactions.
     * @param  
     */
    public PayWithDebit(ShoppingManager sm, CardIssuer issuer, AbstractCardReader cardReader) {
        this.shoppingManager = sm;
        this.issuer = issuer;
        this.groceriesCost = shoppingManager.getGroceriesCost();
        this.amountDue = groceriesCost;
        paymentListeners.add(sm);
    }
    
    
    /**
     * Handles the payment process. 
     * Notifies all payment listeners upon successful payment.
     *
     * @param cardData The data of the swiped card.
     */
    public String pay(CardData cardData) throws IOException {
        groceriesCost = shoppingManager.getGroceriesCost();
    	if(cardData == null) {
            throw new NullPointerException("Card data cannot be null");
        }

    	long holdNumber = issuer.authorizeHold(cardData.getNumber(), groceriesCost.doubleValue());
        if (holdNumber != -1) {
            	amountPaid = amountPaid.add(amountDue);
            	amountDue = groceriesCost.subtract(amountPaid);
            	paidFull = amountDue.compareTo(BigDecimal.ZERO) == 0;
            	
            	notifyPaymentListeners();
            	
            	if (amountPaid.compareTo(BigDecimal.ZERO) == 0) {
                    System.out.println("Invalid amount. Please try again.");
                    return "Invalid amount. Please try again.";
                } else {
                    System.out.println("Amount paid: $" + amountPaid);
                    if (!paidFull) {
                        System.out.println("Amount due: $" + amountDue);
                        return "Amount due: $" + amountDue;
                    } else {
                        System.out.println("No amount due. Payment is complete.");
                        return "No amount due. Payment is complete.";
                    }
                }
            }
		return null;
	}


    /**
     * Initiates the card swiping process and tries to make a payment.
     *
     * @param card The customer's debit card.
     * @throws IOException If an I/O error occurs during the card swipe.
     * @throws NullPointerException If the card argument is null.
     */
    public String SwipeCard(Card card) throws IOException {
    	if (shoppingManager.isGroceriesPaid()) {
            System.out.println("Groceries have already been paid for.");
            return "Groceries have already been paid for.";
        }
    	if (card == null) {
            throw new NullPointerException("Error: Invalid card. Please provide a valid card.");
            
        }
        
        try {
            // Swipe the card to get card data
            CardData cardData = card.swipe();
            
            // Check for signature validity
           // boolean signatureValid = getSignature(cardData.getCardholder());
            
            if (getSignature(cardData.getCardholder())) {
            // Proceed with payment
                pay(cardData);
            } else {
                System.out.println("Payment unsuccessful.");
                return "Payment unsuccessful.";
            }
            
        } catch (MagneticStripeFailureException e) {
            System.err.println("Magnetic stripe failure occurred. Unable to Read Card: " + e.getMessage());
            return "Magnetic stripe failure occurred. Unable to Read Card: " + e.getMessage();
        }
		return null;
    }
    
    
    // Insert Card method
    public String InsertCard(Card card) throws IOException {
        if (shoppingManager.isGroceriesPaid()) {
            System.out.println("Groceries have already been paid for.");
            return "Groceries have already been paid for.";
        }
        if (card == null) {
            throw new NullPointerException("Error: Invalid card. Please provide a valid card.");
        	
        }
        
        
        try { 
            
            CardData cardData = card.insert(getPin()); 
            
            // Check for signature validity
            //boolean signatureValid = getSignature(cardData.getCardholder());
            
            if (getSignature(cardData.getCardholder())) {
                // Proceed with payment
                pay(cardData);
            } else {
                System.out.println("Payment unsuccessful.");
                return "Payment unsuccessful.";
            }
            
        } catch (ChipFailureException e) {
            System.err.println("Chip Failure: Please reinsert card and try again: " + e.getMessage());
            return "Chip Failure: Please reinsert card and try again: " + e.getMessage();
        }
		return null;
    }
    
    
    // Tap Card method
    public String TapCard(Card card) throws IOException {
        if (card == null) {
            throw new NullPointerSimulationException("Error: Invalid card. Please provide a valid card.");
        }
        if (shoppingManager.isGroceriesPaid()) {
            System.out.println("Groceries have already been paid for.");
            return "Groceries have already been paid for.";
        }
        try {
        	CardData cardData = card.tap();
        	
        	pay(cardData);
        	
        } catch (ChipFailureException e) {
        	System.err.println("Chip Failure: Please tap the card again or try a different payment method: " + e.getMessage());
        	return "Chip Failure: Please tap the card again or try a different payment method: " + e.getMessage();
        	
        }
		return null;
    }
    
    
    
    
    @SuppressWarnings("resource")
    protected boolean getSignature(String cardholder) {
        int i = 0;
        boolean valid = false;
        //you get 5 tries to enter a valid signature
        Scanner scanner = new Scanner(System.in); // Moved scanner creation outside the loop
        while (i < 5) { // Changed 6 to 5 to match the comment
            System.out.println("Enter signature: ");
            String signature = scanner.nextLine();
            
            if (signature.equals(cardholder)) {
                valid = true;
                System.out.println("Signature verified. Please proceed with your transaction.");
                break; // Exit the loop if signature is valid
            } else {
                System.out.println("Invalid signature. Please try again.");
            }
            i++; // Increment the loop counter
        }
        scanner.close(); // Moved scanner close outside the loop
        return valid;
    }
    
    

    
    
    
    protected String getPin() {
        Scanner scanner = new Scanner(System.in); 
        boolean valid = false;
        String pin = null;

        while (!valid) {
            System.out.println("Enter pin: ");
            pin = scanner.nextLine();

            if (pin.length() == 4) {
                valid = true;
            } else {
                System.out.println("Invalid pin. Please try again.");
            }
        }
        scanner.close();
        if (!valid) {
            throw new InvalidPINException();
        }
        return pin;
    }

     
    
    /**
     * Retrieves the total amount paid.
     * 
     * @return The total amount paid.
     */
    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    /**
     * Retrieves the remaining amount due.
     * 
     * @return The remaining amount due.
     */
    public BigDecimal getAmountDue() {
        return amountDue;
    }

    /**
     * Checks if the payment has been completed in full.
     *
     * @return True if the payment is complete, otherwise false.
     */
    public boolean isPaidFull() {
        return paidFull;
    }

    /**
     * Processes the data read from a card.
     *
     * @param data The card data to be processed.
     * @throws SecurityException if the card type is not "debit".
     */
    public void theDataFromACardHasBeenRead(CardData data) {
        if (!data.getType().toLowerCase().equals("debit")) {
            throw new SecurityException("Invalid card type!");
        }
    }

    /**
     * Notifies all registered payment listeners.
     * This method iterates through the list of payment listeners and invokes the
     * notifyPayment() method
     * on each listener to notify them of a payment event.
     * 
     */
    public void notifyPaymentListeners() {
        for (IPaymentListener listener : paymentListeners) {
            listener.notifyPayment();
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

    /**
     * Provides an abstract class that implements the CardReaderListener interface,
     * handling device and card reader events, including enabling, disabling,
     * turning on,
     * turning off devices, and swiping cards.
     */
    public abstract class IdeviceListner implements CardReaderListener {
    	 private PayWithDebit payWithDebit;
    	 private Card card;
    	 
    	 public IdeviceListner(PayWithDebit payWithDebit, Card card) {
    	        this.payWithDebit = payWithDebit;
    	        this.card = card;
    	}
        /**
         * Handles the event when a device has been enabled.
         *
         * @param device The device that has been enabled.
         */
        @Override
        public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
            System.out.println("Device has been enabled.");
        }

        /**
         * Handles the event when a device has been disabled, throwing a
         * NoPowerException
         * to indicate the device is not operational.
         *
         * @param device The device that has been disabled.
         * @throws NoPowerException Indicates that the device has lost power and is not
         *                          operational.
         */
        @Override
        public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
            throw new NoPowerException();
        }

        /**
         * Handles the event when a device has been turned on.
         *
         * @param device The device that has been turned on.
         */
        @Override
        public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
            System.out.println("Device has been turned on.");
        }

        /**
         * Handles the event when a device has been turned off, throwing a
         * NoPowerException
         * to indicate the device is not operational.
         *
         * @param device The device that has been turned off.
         * @throws NoPowerException Indicates that the device has lost power and is not
         *                          operational.
         */
        @Override
        public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
            throw new NoPowerException();
        }

        /**
         * Handles the event when a card has been swiped through the card reader
         */
        @Override
        public void aCardHasBeenSwiped() {
            System.out.println("Card has been swiped.");
            try {
                this.payWithDebit.SwipeCard(card);
            } catch (Exception e) {
                // Handle exceptions such as MagneticStripeFailureException, IOException
                System.err.println("An error occurred during card swipe: " + e.getMessage());
            }
        };
     }  
}