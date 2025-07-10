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

import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.card.InvalidPINException;
import com.jjjwelectronics.card.MagneticStripeFailureException;
import com.thelocalmarketplace.hardware.external.CardIssuer;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import shopping.interfaces.IPaymentListener;
import shopping.manager.ShoppingManager;

public class PayWithCredit {
	private ShoppingManager shoppingManager;
	private List<IPaymentListener> paymentListeners = new ArrayList<>();
	protected BigDecimal groceriesCost;
	private boolean paymentMade = false;
	private CardIssuer issuer;
	private boolean signatureValid = false;
	private String signature = null;

	public PayWithCredit(ShoppingManager sm, CardIssuer issuer) {
		this.shoppingManager = sm;
		this.issuer = issuer;
		this.groceriesCost = shoppingManager.getGroceriesCost();
		paymentListeners.add(sm);
	}

	public String SwipeCard(Card card) throws IOException {
		if (shoppingManager.isGroceriesPaid()) {
			System.out.println("Payment has already been made.");
			return "Payment has already been made.";
		}

		if (card == null) {
			throw new NullPointerSimulationException("Card cannot be null");
		}
		String result;
		signatureValid = getSignature(card.cardholder);
		
		if (signatureValid) {
			result = pay(card.swipe());
		} else {
			result = "Payment failed Invalid signature";
		}

		return result;
		
	}

	public String InsertCard(Card card) throws IOException, InvalidPINException {
		if (shoppingManager.isGroceriesPaid()) {
			System.out.println("Payment has already been made.");
			return "Payment has already been made.";
		}

		if (card == null) {
			throw new NullPointerSimulationException("Card cannot be null");
		}
		
		String result;
		signatureValid = getSignature(card.cardholder);
		
		if (signatureValid) {
			result = pay(card.insert(getPin()));
		} else {
			result = "Payment failed Invalid signature";
		}

		return result;

	}

	public String TapCard(Card card) throws IOException {
		if (shoppingManager.isGroceriesPaid()) {
			System.out.println("Payment has already been made.");
			return "Payment has already been made.";
		}

		if (card == null) {
			throw new NullPointerSimulationException("Card cannot be null");
		}

		if (card.isTapEnabled == false) {
			System.out.println("Tap is not enabled on this card");
			return "Tap is not enabled on this card";
		}

		if (card.hasChip == false) {
			System.out.println("No chip detected. Please insert card or swipe card.");
			return "No chip detected. Please insert card or swipe card.";
		}

		return pay(card.tap());

	}

	private String pay(CardData cardData) throws MagneticStripeFailureException {
		if (cardData == null) {
			throw new NullPointerSimulationException("Card data cannot be null");
		}
		String result;
		
		long holdNumber = issuer.authorizeHold(cardData.getNumber(), groceriesCost.doubleValue());
		if (holdNumber != -1) {
			if (issuer.postTransaction(cardData.getNumber(), holdNumber, groceriesCost.doubleValue())) {
				notifyPaymentListeners();
				paymentMade = true;
				groceriesCost = BigDecimal.ZERO;
				result = "Payment successful  Current amount due: " + groceriesCost;
			} else {
				result = "Payment failed Current amount due: " + groceriesCost;
			}
		} else {
			result = "Payment failed Credit has insufficient holds";
		}


		System.out.println(result);
		return result;
	}

	public boolean isPaymentMade() {
		return paymentMade;

	}

	protected boolean getSignature(String cardholder) {
		int i = 0;
		boolean valid = false;
		// you get 5 tries to enter a valid signature
		while (i < 6) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Enter signature: ");
			signature = scanner.nextLine();
			scanner.close();

			if (signature.equals(cardholder)) {
				valid = true;
				return valid;
			} else {
				System.out.println("Invalid signature. Please try again.");
			}
		}
		return valid;
	}

	protected String getPin() {
		boolean valid = false;
		String pin = null;

		// this is used the keep in check if the pin is 4 digits
		// checking if the pin is correct is in the card class
		while (!valid) {
			Scanner scanner = new Scanner(System.in);
			System.out.println("Enter pin: ");
			pin = scanner.nextLine();
			scanner.close();
			if (pin.length() == 4) {
				valid = true;
			} else {
				System.out.println("Invalid pin. Please try again.");
			}
		}

		return pin;
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

}
