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

package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.ChipFailureException;
import com.jjjwelectronics.card.InvalidPINException;
import com.thelocalmarketplace.hardware.external.CardIssuer;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import payment.PayWithCredit;
import shopping.manager.ShoppingManager;

class PayWithCreditStub extends PayWithCredit {

	public PayWithCreditStub(ShoppingManager sm, CardIssuer issuer) {
		super(sm, issuer);
	}

	@Override
	protected boolean getSignature(String cardholder) {
		return true;
	}

	@Override
	protected String getPin() {
		return "1234";
	}

}

class PayWithCreditBadSignatureStub extends PayWithCredit {

	public PayWithCreditBadSignatureStub(ShoppingManager sm, CardIssuer issuer) {
		super(sm, issuer);
	}

	@Override
	protected boolean getSignature(String cardholder) {
		return false;
	}

	@Override
	protected String getPin() {
		return "1234";
	}

}

class PayWithCreditBadPinStub extends PayWithCredit {

	public PayWithCreditBadPinStub(ShoppingManager sm, CardIssuer issuer) {
		super(sm, issuer);
	}

	@Override
	protected boolean getSignature(String cardholder) {
		return true;
	}

	@Override
	protected String getPin() {
		return "4321";
	}

}

public class PayWithCreditTest {
	private PayWithCreditStub creditSwipe;
	private PayWithCreditBadSignatureStub creditBadSignatureSwipe;
	private PayWithCreditBadPinStub creditBadPinSwipe;
	private ShoppingManagerStub shoppingManager;
	private CardIssuer cardIssuer;
	private Card JoeCard;
	private Card JaneCard;
	private Card expiredCard;
	private Card invalidCard;
	private Card insufficientCreditCard;

	@Before
	public void setUp() {
		cardIssuer = new CardIssuer("Visa", 100);
		Calendar current = Calendar.getInstance();

		// Valid card expiry date
		Calendar expiryDate = (Calendar) current.clone();
		expiryDate.set(Calendar.YEAR, 2025);
		expiryDate.set(Calendar.MONTH, Calendar.AUGUST);
		expiryDate.set(Calendar.DAY_OF_MONTH, 22);

		// Expired card date
		Calendar expiredDate = (Calendar) current.clone();
		expiredDate.set(Calendar.YEAR, 2020); // Intentionally set to expired

		// Adding card data
		try {
			cardIssuer.addCardData("123456789", "Joe", expiryDate, "375", 1000);
			cardIssuer.addCardData("987654321", "Jane", expiryDate, "385", 1);
			cardIssuer.addCardData("111122223333", "Expired", expiredDate, "400", 500);
			cardIssuer.addCardData("000000000", "Invalid", expiryDate, "999", 100); // Invalid card, not used in valid
																					// transaction tests
		} catch (InvalidArgumentSimulationException e) {
			System.err.println("Setup failed due to invalid card data: " + e.getMessage());
			// Optionally handle the exception if you want the test setup to continue
		}

		// Create card instances
		JoeCard = new Card("Visa", "123456789", "Joe", "375", "1234", true, true);
		JaneCard = new Card("Visa", "987654321", "Jane", "385", "1234", true, true);
		expiredCard = new Card("Visa", "111122223333", "Expired", "400", "1234", true, true);
		invalidCard = new Card("Visa", "000000000", "Invalid", "999", "1234", true, true);
		insufficientCreditCard = new Card("Visa", "987654321", "Jane", "385", "1234", true, true);
		// Added invalid card for
		// Initialize Shopping Manager and PayWithCredit stub
		shoppingManager = new ShoppingManagerStub("Gold");
		creditSwipe = new PayWithCreditStub(shoppingManager, cardIssuer);
		creditBadSignatureSwipe = new PayWithCreditBadSignatureStub(shoppingManager, cardIssuer);
		creditBadPinSwipe = new PayWithCreditBadPinStub(shoppingManager, cardIssuer);
	}

	@Test
	public void testHasEnoughCredit() throws IOException {
		creditSwipe.SwipeCard(JoeCard);

		assertTrue(shoppingManager.isPaid());

	}

	@Test
	public void testHasNotEnoughCredit() throws IOException {
		creditSwipe.SwipeCard(JaneCard);

		assertFalse(shoppingManager.isPaid());
	}

	@Test
	public void testSwipeCardWithValidCard() throws IOException {
		creditSwipe.SwipeCard(JoeCard);
		assertTrue(shoppingManager.isPaid());
	}
	
	@Test
	public void testSwipeCardWithBadSignature() throws IOException {
		String result = creditBadSignatureSwipe.SwipeCard(JoeCard);
		assertEquals(result, "Payment failed Invalid signature");
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testSwipeCardWithNullCard() throws IOException {
		creditSwipe.SwipeCard(null);

	}

	@Test
	public void testCardNotInBank() throws IOException {
		// test card with an invalid card number
		Card testCardInvalidNumber = new Card("Visa", "135792468", "bob", "123", "1234", true, true);

		creditSwipe.SwipeCard(testCardInvalidNumber);
		assertFalse("Transaction marked as fully paid with invalid card not in database", creditSwipe.isPaymentMade());

	}

	@Test
	public void testCardInsert() throws IOException {
		creditSwipe.InsertCard(JoeCard);
		assertTrue(shoppingManager.isPaid());
	}

	@Test(expected = InvalidPINException.class)
	public void testCardInsertBadPin() throws IOException {
		creditBadPinSwipe.InsertCard(JoeCard);
	}

	// .......................................................................................................................
	@Test
	public void testTapCardInvalidData() throws IOException {
		Card invalidCard = new Card("MasterCard", "000000000", "Invalid", "999", "4321", false, false);
		creditSwipe.TapCard(invalidCard);
		assertFalse("Expected the transaction to not be marked as paid with invalid card details.",
				shoppingManager.isPaid());
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testTapCardWithNullCard() throws IOException {
		creditSwipe.TapCard(null);
	}

	@Test
	public void testTapCardWithSufficientCredit() throws IOException {
		creditSwipe.TapCard(JoeCard);
		assertTrue("Expected the transaction to be marked as paid with sufficient credit.", shoppingManager.isPaid());
	}

	@Test
	public void testTapCardWithInsufficientCredit() throws IOException {
		creditSwipe.TapCard(JaneCard);
		assertFalse("Expected the transaction to not be marked as paid due to insufficient credit.",
				shoppingManager.isPaid());
	}

	@Test
	public void testTapCardWithExpiredCard() throws IOException {
		creditSwipe.TapCard(expiredCard);
		assertFalse("Expected the transaction to not be marked as paid due to expired card.", shoppingManager.isPaid());
	}

}
