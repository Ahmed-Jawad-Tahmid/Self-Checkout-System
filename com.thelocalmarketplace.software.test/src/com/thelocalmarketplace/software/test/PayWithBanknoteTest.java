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

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.CustomerView;
import com.thelocalmarketplace.software.panels.CartPanel;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import payment.PayWithBanknote;
import shopping.manager.ShoppingManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;

public class PayWithBanknoteTest {

    private ShoppingManager shoppingManager;
    private PayWithBanknote payWithBanknote;

    // Define SimpleProduct as an inner class of the test class
    public static class SimpleProduct extends BarcodedProduct {
        private static long barcodeCounter = 0;

        public SimpleProduct(long price, boolean isPerUnit, double expectedWeight) {
            super(generateBarcode(), "description", price, expectedWeight);
        }

        private static Barcode generateBarcode() {
            String counterString = String.valueOf(barcodeCounter++);
            Numeral[] code = new Numeral[counterString.length()];
            for (int i = 0; i < counterString.length(); i++) {
                code[i] = Numeral.valueOf(Byte.parseByte(counterString.substring(i, i + 1)));
            }
            return new Barcode(code);
        }
    }
    
    public static class SimpleItem extends Item {
    	public SimpleItem(Mass mass) {
    		super(mass);
    	}
    }

    @Before
    public void setUp() throws CashOverloadException {
        shoppingManager = new ShoppingManager("Bronze");
        payWithBanknote = new PayWithBanknote(shoppingManager);
        Currency currency = Currency.getInstance("USD");
        
        CartPanel.setView(new CustomerView(shoppingManager));
        
        for (BigDecimal key : shoppingManager.getStation().getBanknoteDispensers().keySet()) {
        	Banknote goodBanknote = new Banknote(currency, key);
        	shoppingManager.getStation().getBanknoteDispensers().get(key).load(goodBanknote, goodBanknote, goodBanknote, goodBanknote, goodBanknote, goodBanknote, goodBanknote, goodBanknote, goodBanknote);
        }
        
    }

    private void addProductToShoppingManager(long priceInCents) {
        SimpleProduct product = new SimpleProduct(priceInCents, true, 400);
        SimpleItem item = new SimpleItem(new Mass(BigInteger.valueOf(400000000)));
        // Assuming an appropriate method to add a product, affecting groceriesCost
        shoppingManager.addProduct(product, "product");
        shoppingManager.getStation().getBaggingArea().addAnItem(item);
    }

    @Test
    public void testGoodBanknote_PayExactAmount() throws DisabledException, CashOverloadException {
        addProductToShoppingManager(20); // Adding a product priced at $20.00
        Currency currency = Currency.getInstance("CAD");
        BigDecimal paymentAmount = new BigDecimal("20");
        
        Banknote banknote = new Banknote(currency, paymentAmount);
        
		shoppingManager.getStation().getBanknoteInput().receive(banknote);

        Assert.assertTrue(shoppingManager.isGroceriesPaid());
    }

    @Test
    public void testGoodBanknote_PayMoreThanAmount() throws CashOverloadException, DisabledException {
        addProductToShoppingManager(15); // Adding a product priced at $15.00
        Currency currency = Currency.getInstance("CAD");
        BigDecimal paymentAmount = new BigDecimal("20");
        
        Banknote banknote = new Banknote(currency, paymentAmount);
        shoppingManager.getStation().getBanknoteInput().receive(banknote);

        Assert.assertTrue(shoppingManager.isGroceriesPaid());
    }

    @Test
    public void testGoodBanknote_PartialPayment() throws DisabledException, CashOverloadException {
        addProductToShoppingManager(30); // Adding a product priced at $30.00
        Currency currency = Currency.getInstance("CAD");
        BigDecimal paymentAmount = new BigDecimal("20"); // Less than total cost

        Banknote banknote = new Banknote(currency, paymentAmount);
        shoppingManager.getStation().getBanknoteInput().receive(banknote);
        
        Assert.assertFalse(shoppingManager.isGroceriesPaid());
    }

    @Test
    public void testGoodBanknote_PartialPaymentWithoutCompleting() throws DisabledException, CashOverloadException {
        addProductToShoppingManager(50); // Adding a product priced at $50.00
        Currency currency = Currency.getInstance("CAD");
        BigDecimal firstPaymentAmount = new BigDecimal("50"); // Significantly less than total cost

        Banknote banknote = new Banknote(currency, firstPaymentAmount);
        shoppingManager.getStation().getBanknoteInput().receive(banknote);
        
        Assert.assertFalse(shoppingManager.isGroceriesPaid());
    }

    @Test
    public void testGoodBanknote_MultiplePayments() throws DisabledException, CashOverloadException {
        addProductToShoppingManager(50); // Adding a product priced at $50.00
        Currency currency = Currency.getInstance("CAD");
        
        Banknote banknote = new Banknote(currency, new BigDecimal("20"));
        Banknote banknote2 = new Banknote(currency, new BigDecimal("10"));
        
		shoppingManager.getStation().getBanknoteInput().receive(banknote);
		shoppingManager.getStation().getBanknoteInput().receive(banknote2);
		shoppingManager.getStation().getBanknoteInput().receive(banknote);
        

        // Verifying if the groceries are marked as paid
        Assert.assertTrue(shoppingManager.isGroceriesPaid());
    }

    @Test
    public void testBadBanknote_InvalidNote() {
        addProductToShoppingManager(20); // Adding a product priced at $20.00
        Currency currency = Currency.getInstance("USD");

        payWithBanknote.badBanknote(null); // Attempt to pay with an invalid note

        // The groceries should not be marked as paid since the payment was invalid
        Assert.assertFalse(shoppingManager.isGroceriesPaid());
    }
    @Test
    public void testSequentialInvalidNotes() throws DisabledException, CashOverloadException {
        addProductToShoppingManager(20); // $20.00 product
        Currency currency = Currency.getInstance("CAD");

        Banknote banknote = new Banknote(Currency.getInstance("USD"), new BigDecimal("20"));
        
        // Try invalid notes, removing the dangling bank notes after each try
        shoppingManager.getStation().getBanknoteInput().receive(banknote);
        shoppingManager.getStation().getBanknoteInput().removeDanglingBanknote();
        shoppingManager.getStation().getBanknoteInput().receive(banknote);
        shoppingManager.getStation().getBanknoteInput().removeDanglingBanknote();

        // The groceries should still not be marked as paid
        Assert.assertFalse(shoppingManager.isGroceriesPaid());
        
        Banknote banknote2 = new Banknote(currency, new BigDecimal("20"));

        // Then, make a valid payment
        shoppingManager.getStation().getBanknoteInput().receive(banknote2);
        Assert.assertTrue(shoppingManager.isGroceriesPaid());
    }

    // ......................................................................................
    @Test
    public void testProduceChangeViaGoodBanknote() throws DisabledException, CashOverloadException {
        // Setup: Adding a product that costs $20.00
        addProductToShoppingManager(15); // $15.00
        Currency currency = Currency.getInstance("CAD");

        // Action: Simulating overpayment with a $20.00 banknote
        BigDecimal overPaymentAmount = new BigDecimal("20");
        Banknote banknote = new Banknote(currency, overPaymentAmount);
        
        shoppingManager.getStation().getBanknoteInput().receive(banknote);

        // Assertion: Verify groceries are marked as paid
        Assert.assertTrue("Groceries should be marked as paid after overpayment", shoppingManager.isGroceriesPaid());
    }

    @Test
    public void testProduceChangeViaCumulativeOverpayment() throws DisabledException, CashOverloadException {
        // Setup: Adding a product that costs $30.00
        addProductToShoppingManager(30); // $30.00
        Currency currency = Currency.getInstance("CAD");

        // Action: Simulating cumulative overpayment through multiple banknotes
        BigDecimal firstPaymentAmount = new BigDecimal("20"); // First payment
        Banknote banknote = new Banknote(currency, firstPaymentAmount);
        shoppingManager.getStation().getBanknoteInput().receive(banknote);

        BigDecimal secondPaymentAmount = new BigDecimal("20"); // Second payment, leading to overpayment
        Banknote banknote2 = new Banknote(currency, secondPaymentAmount);
        shoppingManager.getStation().getBanknoteInput().receive(banknote2);

        // Assertion: Verify groceries are marked as paid after cumulative overpayment
        Assert.assertTrue("Groceries should be marked as paid after cumulative overpayment",
                shoppingManager.isGroceriesPaid());

    }

    @Test
    public void testBadBanknote_AfterSuccessfulPayment() throws DisabledException, CashOverloadException {
        // Setup: Adding a product priced at $20.00 and paying exactly
        addProductToShoppingManager(20); // $20.00
        Currency currency = Currency.getInstance("CAD");
        BigDecimal exactPaymentAmount = new BigDecimal("20");
        
        Banknote banknote = new Banknote(currency, exactPaymentAmount);
        shoppingManager.getStation().getBanknoteInput().receive(banknote);

        // Asserting the groceries have been marked as paid
        Assert.assertTrue("Groceries should be marked as paid after exact payment", shoppingManager.isGroceriesPaid());

        // Attempt to pay with an invalid banknote after successful payment
        Banknote banknote2 = new Banknote(Currency.getInstance("USD"), new BigDecimal("20"));
        shoppingManager.getStation().getBanknoteInput().receive(banknote2);

        // Since groceries are already paid for, we expect no change in payment status,
        // but it's valuable to note if any unintended behavior is triggered.
        Assert.assertTrue(
                "Groceries should remain marked as paid after invalid banknote attempt post successful payment",
                shoppingManager.isGroceriesPaid());

    }

    @Test
    public void testBadBanknote_WithoutAnyProducts() throws DisabledException, CashOverloadException {
        // No products added to the shopping manager at this point.
        Currency currency = Currency.getInstance("CAD");

        // Attempt to pay with an invalid banknote before adding any products
        Banknote banknote = new Banknote(Currency.getInstance("USD"), new BigDecimal("20"));
        shoppingManager.getStation().getBanknoteInput().receive(banknote);

        // Check to ensure the system correctly identifies that no payment is needed and
        // maintains correct state.
        Assert.assertFalse("Groceries should not be marked as paid when no products have been added.",
                shoppingManager.isGroceriesPaid());

        // This test also indirectly checks for potential exceptions or errors in
        // handling a payment process
        // when the system might not be in a ready state to process payments.
    }

    @Test
    public void testChangeDispensingAccuracy() throws DisabledException, CashOverloadException {
        // Given
    	TestBankNoteDispensationSlotListener listener = new TestBankNoteDispensationSlotListener();
    	shoppingManager.getStation().getBanknoteOutput().attach(listener);
        addProductToShoppingManager(15); // $15.00
        Currency currency = Currency.getInstance("CAD");
        BigDecimal overPaymentAmount = new BigDecimal("20"); // Overpayment to trigger change

        // When
        Banknote banknote = new Banknote(currency, overPaymentAmount);
        shoppingManager.getStation().getBanknoteInput().receive(banknote);

        // Then
        Assert.assertTrue("Groceries should be marked as paid", shoppingManager.isGroceriesPaid());
        // Here you would verify the change was correctly dispensed, possibly by
        // checking the state of the dispensers or logs
        Assert.assertEquals(TestBankNoteDispensationSlotListener.totalChangeDispensed, BigDecimal.valueOf(5));
        
    }
}