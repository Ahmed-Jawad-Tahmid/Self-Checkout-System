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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import payment.PayWithDebit;
import powerutility.PowerGrid;
import shopping.manager.ConfigCheckoutStation;
import shopping.manager.ShoppingManager;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.AbstractCardReader;
import com.thelocalmarketplace.hardware.external.CardIssuer;

import com.jjjwelectronics.card.Card.CardData;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.io.ByteArrayInputStream;
import java.io.InputStream;


public class PayWithDebitTest {

    private PayWithDebit payWithDebit;
    private Card testCard;
    private ShoppingManager sm;
    private ConcreteCardReader cardReader;
    private CardIssuer issuer;
    private Card validCard;
   

    @Before
    public void setUp1() throws Exception {
        ShoppingManager shoppingManager = new ShoppingManager("Gold", new ConfigCheckoutStation());
        Calendar expiryDate = Calendar.getInstance();
        expiryDate.add(Calendar.YEAR, 5); // Set the expiry date to 5 years in the future

        CardIssuer cardIssuer = new CardIssuer("Test Bank", 1) {
            @Override
            public long authorizeHold(String cardNumber, double amount) {
                return cardNumber.equals("1234567890123456") ? 123L : -1L;
            }

            @Override
            public boolean postTransaction(String cardNumber, long holdNumber, double actualAmount) {
                return cardNumber.equals("1234567890123456");
            }
        };

        AbstractCardReader cardReaderStub = createCardReaderStub();

        payWithDebit = new PayWithDebit(shoppingManager, cardIssuer, cardReaderStub);

        testCard = new Card("Visa", "1234567890123456", "John Doe", "123", "1234", true, true);
    }

    private AbstractCardReader createCardReaderStub() {
        return new AbstractCardReader() {
            @Override
            public Card.CardData swipe(Card card) throws IOException {
                // Simulate a successful swipe by returning the card's own data
                return card.new CardSwipeData(); // Utilize the CardSwipeData inner class
            }

            @Override
            public void enable() {
                // Mock enable functionality
            }

            @Override
            public void disable() {
                // Mock disable functionality
            }
        };
    }

    @Test
    public void testSwipeCardSuccessfulTransaction() {
        // Mock user input
        String input = "John Doe"; // Simulate valid signature input

        // Set up mock scanner with the input
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        try {
            payWithDebit.SwipeCard(testCard);
            assertTrue("Transaction was not marked as fully paid", payWithDebit.isPaidFull());
        } catch (Exception e) {
            fail("Unexpected exception during card swipe: " + e.getMessage());
        } finally {
            // Reset System.in to its original state
            System.setIn(System.in);
        }
    }

    @Test(expected = NullPointerException.class)
    public void testSwipeNullCardThrowsException() throws Exception {
    	
        payWithDebit.SwipeCard(null);
    }

    @Test
    public void testSwipeCardInvalidCardNumber() {
    	// Mock user input
        String input = "John Doe"; // Simulate valid signature input

        // Set up mock scanner with the input
        InputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);
        // test card with an invalid card number
        Card testCardInvalidNumber = new Card("Visa", "1234", "John Doe", "123", "1234", true, true);
        try {
            payWithDebit.SwipeCard(testCardInvalidNumber);
            assertFalse("Transaction marked as fully paid with invalid card number", payWithDebit.isPaidFull());
        } catch (Exception e) {
            fail("Unexpected exception during card swipe: " + e.getMessage());
        }
    }

    @Test
    public void testDirectPaymentCompletesTransaction() throws IOException {
        // Prepare the card data as if the card was swiped successfully.
        CardData cardData = testCard.new CardSwipeData(); // Simulating swipe data.

        // Directly call the pay method as if a transaction occurred.
        payWithDebit.pay(cardData); // Assuming pay does not require specific amount.

        // Since we cannot directly manipulate groceriesCost or amountDue,
        // and the setUp method doesn't specify these amounts, the assertions will
        // focus on the state change indicating a successful payment process.
        assertTrue("Payment should be marked as fully paid", payWithDebit.isPaidFull());
        // Verifying amountPaid is not straightforward without knowing groceriesCost,
        // but we can check if amountDue is zero which implies full payment.
        assertEquals("The amount due after payment should be zero", BigDecimal.ZERO, payWithDebit.getAmountDue());
    }

    @Test(expected = NullPointerException.class)
    public void testDirectPaymentWithNullCardDataThrowsException() throws IOException {
        // Directly call the pay method with null to simulate error handling.
        payWithDebit.pay(null);
    }

    @Test
    public void testAmountsAfterPayment() throws IOException {
        // Directly simulate a successful payment scenario
        CardData cardData = testCard.new CardSwipeData(); // Simulating swipe data.
        payWithDebit.pay(cardData); // Assuming groceriesCost is set in setUp method.

        // Assert conditions based on the expected behavior of your pay method
        assertTrue("The payment should be marked as complete", payWithDebit.isPaidFull());
        assertEquals("Amount due should be zero after full payment", BigDecimal.ZERO, payWithDebit.getAmountDue());
        assertNotNull("Amount paid should not be null after payment", payWithDebit.getAmountPaid());
        // You might want to assert the exact value of amountPaid depending on
        // groceriesCost
    }

    @Test
    public void testDataFromACardHasBeenRead_ValidDebitCard() {
        // Create a Card instance
        Card validDebitCard = new Card("debit", "1234567890123456", "Card Holder", "123", "1234", true, true);
        try {
            // Simulate swiping the card to get CardData
            Card.CardData validDebitCardData = validDebitCard.swipe();
            payWithDebit.theDataFromACardHasBeenRead(validDebitCardData);
            // If no exception is thrown, the test passes for valid debit card data.
        } catch (IOException e) {
            fail("IOException should not be thrown for a valid swipe.");
        } catch (SecurityException e) {
            fail("SecurityException should not be thrown for debit card data.");
        }
    }

    @Test(expected = SecurityException.class)
    public void testDataFromACardHasBeenRead_InvalidCardType() {
        // Create a Card instance with an invalid type
        Card invalidCard = new Card("credit", "1234567890123456", "Card Holder", "123", "1234", true, true);
        try {
            // Simulate swiping the card to get CardData
            Card.CardData invalidCardData = invalidCard.swipe();
            // This call is expected to throw SecurityException for non-debit card data
            payWithDebit.theDataFromACardHasBeenRead(invalidCardData);
        } catch (IOException e) {
            fail("IOException should not be thrown for an invalid card swipe.");
        }
    }
    
    @Before
    public void setUp() {
        ConfigCheckoutStation config = new ConfigCheckoutStation(); // Setup configuration
        sm = new ShoppingManager("Bronze", config);
        issuer = new CardIssuer("TestIssuer", 10);
        cardReader = new ConcreteCardReader();
        payWithDebit = new PayWithDebit(sm, issuer, cardReader);
        validCard = new Card("debit", "Card Number", "Card Holder Name", "CVV", "Pin", true, true);

        // Access the singleton instance of PowerGrid
        PowerGrid powerGrid = PowerGrid.instance();
        cardReader.plugIn(powerGrid); // Plug the card reader into the power grid
        cardReader.turnOn(); // Turn on the card reader
        // Ensure the card reader is not disabled (if necessary, based on your application logic)
        if (cardReader.isDisabled()) {
            cardReader.enable();
        }
    }

    @Test
    public void testAmountDueInitializedCorrectly() {
        // Using a valid station type as per SelfCheckoutStationFactory
        ConfigCheckoutStation config = new ConfigCheckoutStation(); // Assuming this exists and sets up the station
        ShoppingManager sm = new ShoppingManager("Bronze", config); // Changed to "Bronze" which is a valid station type
        CardIssuer issuer = new CardIssuer("IssuerName", 10); // Create a CardIssuer with a name and maximum hold count
        
        // Set up a card reader
        ConcreteCardReader cardReader = new ConcreteCardReader();

        // Assuming the shopping manager's getGroceriesCost method will return 100 (or whatever is appropriate)
        PayWithDebit payWithDebit = new PayWithDebit(sm, issuer, cardReader);

        // We have to assume that PayWithDebit uses the ShoppingManager's getGroceriesCost for amountDue
        BigDecimal expectedAmountDue = sm.getGroceriesCost();
        
        // We must ensure that the PayWithDebit instance's amountDue field is initialized with the expected value
        Assert.assertEquals("The amount due should be initialized to the groceries cost", expectedAmountDue, payWithDebit.getAmountDue());
    }

    @Test(expected = NullPointerException.class)
    public void testCardSwipeWithNullCard() throws Exception {
        payWithDebit.SwipeCard(null); // This should throw a NullPointerException
    }
    
    @Test
    public void testTapCardSuccessfulTransaction() {
        try {
            payWithDebit.TapCard(testCard);
            assertTrue("Transaction was not marked as fully paid", payWithDebit.isPaidFull());
        } catch (Exception e) {
            fail("Unexpected exception during card tap: " + e.getMessage());
        }
    }
    
    @Test(expected = NullPointerException.class)
    public void insertCard_NullCard_ThrowsNullPointerException() throws Exception {
        payWithDebit.InsertCard(null);
    }


    @Test
    public void testSwipeCardSuccess() throws Exception {
        System.setIn(new ByteArrayInputStream("John Doe\n".getBytes()));
        
        payWithDebit.SwipeCard(testCard);

        assertTrue("The payment should be marked as complete after swiping a valid card", payWithDebit.isPaidFull());
    }

    @Test
    public void testTapCardSuccess() throws Exception {
        payWithDebit.TapCard(testCard);

        assertTrue("The payment should be marked as complete after tapping a valid card", payWithDebit.isPaidFull());
    }

    @Test
    public void testGetPinSuccess() {
        // Extend PayWithDebit to access protected method getPin
        class PayWithDebitTestWrapper extends PayWithDebit {
            public PayWithDebitTestWrapper(ShoppingManager sm, CardIssuer issuer, AbstractCardReader cardReader) {
                super(sm, issuer, cardReader);
            }

            public String testGetPin() {
                return this.getPin(); // Now we can call the protected method
            }
        }

        System.setIn(new ByteArrayInputStream("1234\n".getBytes()));

        PayWithDebitTestWrapper testWrapper = new PayWithDebitTestWrapper(sm, issuer, cardReader);
        assertEquals("1234", testWrapper.testGetPin());
    }

    @Test
    public void testGetSignatureSuccess() {
        // Mock user's signature input
        System.setIn(new ByteArrayInputStream("John Doe\n".getBytes()));

        class PayWithDebitTestWrapper extends PayWithDebit {
            public PayWithDebitTestWrapper(ShoppingManager sm, CardIssuer issuer, AbstractCardReader cardReader) {
                super(sm, issuer, cardReader);
            }

            public boolean testGetSignature(String cardholder) {
                return this.getSignature(cardholder);
            }
        }

        PayWithDebitTestWrapper testWrapper = new PayWithDebitTestWrapper(sm, issuer, cardReader);
        assertTrue("Signature should be verified successfully", testWrapper.testGetSignature("John Doe"));
    }
}

class ConcreteCardReader extends AbstractCardReader {
    @Override
    public Card.CardData swipe(Card card) throws IOException {
        if (card == null) {
            throw new NullPointerException("Card cannot be null");
        }
        // Ensure no operation here throws NoPowerException before this check
        return super.swipe(card);
    }
}
