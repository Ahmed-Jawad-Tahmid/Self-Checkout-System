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

import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.CustomerView;
import com.thelocalmarketplace.software.panels.CartPanel;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.PowerGrid;
import shopping.manager.ShoppingManager;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 
 * Test class for the ShoppingManager class.
 * 
 */

public class ShoppingManagerTest {
	
	/**
     * Represents a simple product used for testing.
     */
	
	class SimpleProduct extends Product {
	    public SimpleProduct(long price, boolean isPerUnit) {
	        super(price, isPerUnit);
	    }
	}
	
	/**
     * Represents an actual item used for testing.
     */

	class ActualItem extends Item {
		public ActualItem(Mass mass) {
			super(mass);
		}
	}
	
	/**
     * Initializes a ShoppingManager object for testing.
     *
     * @return The initialized ShoppingManager object.
     */
	
	public ShoppingManager initShoppingManager() {
    	ShoppingManager manager = new ShoppingManager("Bronze");
        CartPanel.setView(new CustomerView(manager));
    	
    	manager.getStation().plugIn(PowerGrid.instance());
    	manager.getStation().turnOn();
        return manager;
    }
	
	/**
     * Tests adding a product to increase the expected weight.
     */

    @Test
    public void testAddingProductIncreasesExpectedWeight() {
    	ShoppingManager manager = initShoppingManager();
    	ProductTestUtils.fillDatabasesBarcoded();
    	
    	BarcodedProduct apple = ProductTestUtils.getProductByDescription("apple");
    	BigDecimal appleWeight = BigDecimal.valueOf((long) apple.getExpectedWeight());
    	
        manager.addProduct(apple, "apple");

        assertEquals("Expected weight should be " + appleWeight +  " after adding one apple", appleWeight.doubleValue(),
                manager.getExpectedWeight().doubleValue(), 0.01);
    }
    
    /**
     * Tests adding multiple products and verifying the expected weight.
     */

    @Test
    public void testAddingMultipleProducts() {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
        
        Barcode validBarcode = new Barcode(new Numeral[]{
                Numeral.valueOf((byte) 8),
                Numeral.valueOf((byte) 9),
                Numeral.valueOf((byte) 0),
                Numeral.valueOf((byte) 1),
                Numeral.valueOf((byte) 2)
            });
        
    	BarcodedItem appleItem = new BarcodedItem(validBarcode, new Mass(400000000));
        BarcodedProduct apple = ProductTestUtils.getProductByDescription("apple");
        BarcodedProduct banana = ProductTestUtils.getProductByDescription("banana");
        BigDecimal appleWeight = BigDecimal.valueOf((long) apple.getExpectedWeight());
        BigDecimal bananaWeight = BigDecimal.valueOf((long) banana.getExpectedWeight());
        
        
        manager.addProduct(apple, "apple"); // 400 grams
    	manager.getStation().getBaggingArea().addAnItem(appleItem);
        manager.addProduct(banana, "banana"); // 450 grams
        BigDecimal expectedWeight = appleWeight.add(bananaWeight);

        assertEquals("Expected weight should be 850.0 after adding an apple and a banana", expectedWeight.doubleValue(),
                manager.getExpectedWeight().doubleValue(), 0.01);
    }
    
    /**
     * Tests setting groceries as paid.
     */

    @Test
    public void testSetGroceriesPaid() {
        ShoppingManager manager = initShoppingManager();
        manager.notifyPayment();
        assertTrue("Groceries should be marked as paid", manager.isGroceriesPaid());
    }
    
    /**
     * Tests getting the total cost of groceries.
     */

    @Test
    public void testGetGroceriesCost() {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
        BarcodedProduct apple = ProductTestUtils.getProductByDescription("apple");
        BigDecimal appleWeight = BigDecimal.valueOf((long) apple.getExpectedWeight());
    
       
        manager.addProduct(apple, "apple"); // 400 grams

        assertEquals("Groceries cost should be " + apple.getPrice() + " after adding one apple", apple.getPrice(),
                manager.getGroceriesCost().doubleValue(), 0.01);
    }
    
    /**
     * Tests whether the station is not null.
     */

    @Test
    public void testGetStation_NotNull() {
        ShoppingManager manager = initShoppingManager();
        assertNotNull("Station should not be null", manager.getStation());
    }
    
    /**
     * Tests whether the station is an instance of SelfCheckoutStation.
     */

    @Test
    public void testGetStation_IsInstanceOfSelfCheckoutStation() {
        ShoppingManager manager = initShoppingManager();
        assertTrue("Station should be instance of SelfCheckoutStation",
                manager.getStation() instanceof AbstractSelfCheckoutStation);
    }
    
    /**
     * Tests whether there is a discrepancy in weight.
     *
     * @throws OverloadedDevice If the device is overloaded.
     */
    
    @Test
	public void testHasDiscrepancyLess() throws OverloadedDevice {
		ShoppingManager manager = initShoppingManager();
		ProductTestUtils.fillDatabasesBarcoded();
		
		//adding products to the list
        BarcodedProduct apple = ProductTestUtils.getProductByDescription("apple");
        BarcodedProduct banana = ProductTestUtils.getProductByDescription("banana");
        
        BigDecimal appleWeight = BigDecimal.valueOf((long) apple.getExpectedWeight());
        BigDecimal bananaWeight = BigDecimal.valueOf((long) banana.getExpectedWeight());
        ActualItem realBanana = new ActualItem(new Mass(bananaWeight));
        
        manager.addProduct(apple, "apple"); // 400 grams
        try {
        	manager.addProduct(banana, "banana"); // 450 grams
        }
        catch(IllegalStateException e) {
        	
        }
        
        //only add the banana to the bagging area to get a discrepancy
        manager.getStation().getBaggingArea().addAnItem(realBanana);

        
        boolean actual = manager.hasShoppingDiscrepancy();
        assertTrue("There should be a discrepancy", actual);
		
	}
    
    /**
     * Tests whether there is a discrepancy in weight with multiple items.
     *
     * @throws OverloadedDevice If the device is overloaded.
     */
    
    @Test
	public void testHasDiscrepancyMore() throws OverloadedDevice {
		ShoppingManager manager = initShoppingManager();
		ProductTestUtils.fillDatabasesBarcoded();
		
		//adding products to the list
        BarcodedProduct apple = ProductTestUtils.getProductByDescription("apple");
        BarcodedProduct banana = ProductTestUtils.getProductByDescription("banana");
        
        BigDecimal appleWeight = BigDecimal.valueOf((long) apple.getExpectedWeight());
        BigDecimal bananaWeight = BigDecimal.valueOf((long) banana.getExpectedWeight());
        
        ActualItem realApple = new ActualItem(new Mass(appleWeight));
        ActualItem realBanana = new ActualItem(new Mass(bananaWeight));
		
		//add 2 bananas to the bagging area to get a discrepancy
		manager.getStation().getBaggingArea().addAnItem(realApple);
		manager.getStation().getBaggingArea().addAnItem(realBanana);

		boolean actual = manager.hasShoppingDiscrepancy();
		assertTrue("There should be a discrepancy", actual);
	}
    
    /**
     * Tests whether there is no discrepancy in weight.
     *
     * @throws OverloadedDevice If the device is overloaded.
     */
    
    @Test
    public void testHasNoDiscrepancy() throws OverloadedDevice {
    	ShoppingManager manager = initShoppingManager();
    	ProductTestUtils.fillDatabasesBarcoded();
		
    	//adding products to the list
        BarcodedProduct apple = ProductTestUtils.getProductByDescription("apple");
        BarcodedProduct banana = ProductTestUtils.getProductByDescription("banana");
        
        BigDecimal appleWeight = BigDecimal.valueOf((long) apple.getExpectedWeight());
        BigDecimal bananaWeight = BigDecimal.valueOf((long) banana.getExpectedWeight());
        
        ActualItem realApple = new ActualItem(new Mass(appleWeight));
        ActualItem realBanana = new ActualItem(new Mass(bananaWeight));

        manager.addProduct(apple, "apple"); // 400 grams
        //add the items to the bagging area
        manager.getStation().getBaggingArea().addAnItem(realApple);
        manager.addProduct(banana, "banana"); // 450 grams
        manager.getStation().getBaggingArea().addAnItem(realBanana);
        
        
        boolean actual = manager.hasShoppingDiscrepancy();
        assertFalse("There should be no discrepancy", actual);
    }
    
    /**
     * Tests the behavior when mass on scale changes with discrepancy.
     *
     * @throws OverloadedDevice If the device is overloaded.
     */
        
    @Test
    public void MassOnScaleChangedWithDiscrepancyTest() throws OverloadedDevice {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
    	
    	BarcodedProduct apple = ProductTestUtils.getProductByDescription("apple");
    	BigDecimal appleWeight = BigDecimal.valueOf((long) apple.getExpectedWeight());
        manager.addProduct(apple, "apple");

        Numeral[] numeral = { Numeral.one, Numeral.three };
        BarcodedItem item = new BarcodedItem(new Barcode(numeral), new Mass(BigInteger.valueOf(Mass.MICROGRAMS_PER_GRAM).multiply(BigInteger.valueOf(320))));

        manager.getStation().getBaggingArea().addAnItem(item);

        manager.theMassOnTheScaleHasChanged(manager.getStation().getBaggingArea(), item.getMass());

	    //If there is a weight discrepancy, disable scanner.
        assertTrue(manager.getStation().getMainScanner().isDisabled());

	    //If there is a weight discrepancy, disable coin slot.
        assertTrue(manager.getStation().getCoinSlot().isDisabled());
    }
    
    /**
     * Tests the behavior when mass on scale changes without discrepancy.
     *
     * @throws OverloadedDevice If the device is overloaded.
     */

    @Test
    public void MassOnScaleChangedWithoutDiscrepancyTest() throws OverloadedDevice {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
    	
    	BarcodedProduct apple = ProductTestUtils.getProductByDescription("apple");
    	BigDecimal appleWeight = BigDecimal.valueOf((long) apple.getExpectedWeight());
        manager.addProduct(apple, "apple");

        Numeral[] numeral = { Numeral.one, Numeral.three };
        BarcodedItem item = new BarcodedItem(new Barcode(numeral), new Mass(BigInteger.valueOf(Mass.MICROGRAMS_PER_GRAM).multiply(BigInteger.valueOf(400))));

        manager.getStation().getBaggingArea().addAnItem(item);

        manager.theMassOnTheScaleHasChanged(manager.getStation().getBaggingArea(), item.getMass());

	    // If there is no weight discrepancy, ensure scanner is enabled.
        assertFalse(manager.getStation().getMainScanner().isDisabled());

	    // If there is no weight discrepancy, ensure coin slot is enabled.
        assertFalse(manager.getStation().getCoinSlot().isDisabled());
    }
    
    /**
     * Tests getting the weight when no products are added.
     *
     * @throws OverloadedDevice If the device is overloaded.
     */

    @Test
    public void testGetWeightNoProducts() throws OverloadedDevice {
        ShoppingManager manager = initShoppingManager();
        BigDecimal weight = manager.getExpectedWeight();
        assertEquals("Weight should be 0 when no products are added", 0.0, weight.doubleValue(), 0.01);
    }

    @Test
    public void testRemoveProductCorrectlyUpdatesCost() {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
        

        Barcode validBarcode = new Barcode(new Numeral[]{
                Numeral.valueOf((byte) 8),
                Numeral.valueOf((byte) 9),
                Numeral.valueOf((byte) 0),
                Numeral.valueOf((byte) 1),
                Numeral.valueOf((byte) 2)
            });
        
    	BarcodedItem appleItem = new BarcodedItem(validBarcode, new Mass(400000000));
        
        BarcodedProduct apple = ProductTestUtils.getProductByDescription("apple");
        assertNotNull("Product apple should exist in database", apple);
        
        BigDecimal weightPerApple = new BigDecimal("400"); // Assuming weight is correctly fetched as 400g
        BigDecimal pricePerApple = new BigDecimal("8"); // Assuming price is correctly fetched as 8
        
        // Add apples to simulate customer action
        manager.addProduct(apple, "apple"); // Adds one apple

    	manager.getStation().getBaggingArea().addAnItem(appleItem);

        // Assert initial cost before removal is as expected (for one apple)
        assertEquals("Initial groceries cost before removal should be price of one apple", pricePerApple, manager.getGroceriesCost());

        // Remove the apple
        manager.removeProduct(apple, 1);

        // Expected cost after removing the apple should be 0, as no apples remain
        BigDecimal expectedCostAfterRemoval = BigDecimal.ZERO;

        // Validate that the groceries cost is updated correctly after removal
        assertEquals("Expected cost after removal does not match", 0, expectedCostAfterRemoval.compareTo(manager.getGroceriesCost()));
    }
    
    // test if scanners and slot are re-enabled when making the bulky item request and attendant approves
    @Test
    public void testReEnabledWhenHandlingBulkyItem() {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
        
        BarcodedProduct appleProduct = ProductTestUtils.getProductByDescription("apple");
        BigDecimal appleWeight = BigDecimal.valueOf((long) appleProduct.getExpectedWeight());
        
        manager.setAddBulkyItemLogic();
        manager.setAttendantApproved(true);
        manager.addProduct(appleProduct, "apple"); // 400 grams

    	Assert.assertFalse(manager.getStation().getMainScanner().isDisabled());
    	Assert.assertFalse(manager.getStation().getHandheldScanner().isDisabled());
    	Assert.assertFalse(manager.getStation().getCoinSlot().isDisabled());
    }
    
    // test if expected weight remains unchanged, and no weight discrepancy occurs if attendant approves
    @Test
    public void testExpWeightUnchangedWhenHandlingBulkyItem() {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
        
        BarcodedProduct appleProduct = ProductTestUtils.getProductByDescription("apple");
        BigDecimal appleWeight = BigDecimal.valueOf((long) appleProduct.getExpectedWeight());
        
        manager.setAddBulkyItemLogic();
        manager.setAttendantApproved(true);
        manager.addProduct(appleProduct, "apple"); // 400 grams

    	Assert.assertTrue(manager.getExpectedWeight().compareTo(BigDecimal.ZERO) == 0);
    	Assert.assertFalse(manager.hasShoppingDiscrepancy());
    }
    
    // test if there is a shopping discrepancy if attendant does not approve
    @Test(expected = UnsupportedOperationException.class)
    public void testExpWeightChangedWhenHandlingBulkyItem() {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
        
        BarcodedProduct appleProduct = ProductTestUtils.getProductByDescription("apple");
        BigDecimal appleWeight = BigDecimal.valueOf((long) appleProduct.getExpectedWeight());
        
        manager.setAddBulkyItemLogic();
        manager.setAttendantApproved(false);
        manager.addProduct(appleProduct, "apple"); // 400 grams

    	Assert.assertTrue(manager.getExpectedWeight().compareTo(BigDecimal.valueOf(400)) == 0);
    }
    
    // test if attendant approves, exp weight does not change, and customer still puts it in bagging area; should be weightDiscrepancy
    @Test
    public void testWeightDiscWhenHandlingBulkyItem() {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
        
        BarcodedProduct appleProduct = ProductTestUtils.getProductByDescription("apple");
        BigDecimal appleWeight = BigDecimal.valueOf((long) appleProduct.getExpectedWeight());
        
        manager.setAddBulkyItemLogic();
        manager.setAttendantApproved(true);
        manager.addProduct(appleProduct, "apple"); // 400 grams
        
        Barcode validBarcode = new Barcode(new Numeral[]{
                Numeral.valueOf((byte) 8),
                Numeral.valueOf((byte) 9),
                Numeral.valueOf((byte) 0),
                Numeral.valueOf((byte) 1),
                Numeral.valueOf((byte) 2)
            });
    	BarcodedItem appleItem = new BarcodedItem(validBarcode, new Mass(400000000));
    	manager.getStation().getBaggingArea().addAnItem(appleItem);

    	Assert.assertTrue(manager.getExpectedWeight().compareTo(BigDecimal.ZERO) == 0);
    	Assert.assertTrue(manager.hasShoppingDiscrepancy());
    }
    
    // test not able to remove bulky item
    @Test(expected = UnsupportedOperationException.class)
    public void testWeightChangeRemovingBulkyItem() {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
        
        BarcodedProduct appleProduct = ProductTestUtils.getProductByDescription("apple");
        BigDecimal appleWeight = BigDecimal.valueOf((long) appleProduct.getExpectedWeight());
        
        manager.setAddBulkyItemLogic();
        manager.setAttendantApproved(true);
        manager.addProduct(appleProduct, "apple"); // 400 grams
        
    	Assert.assertTrue(manager.getExpectedWeight().compareTo(BigDecimal.ZERO) == 0);

        manager.setAddBulkyItemLogic();
        manager.removeProduct(appleProduct, 1);
    }
    
    // test remove item | expected weight should change
    @Test
    public void testExpectedWeightChangeWhenRemoved() {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
        
        BarcodedProduct appleProduct = ProductTestUtils.getProductByDescription("apple");
        BigDecimal appleWeight = BigDecimal.valueOf((long) appleProduct.getExpectedWeight());
        
        manager.addProduct(appleProduct, "apple"); // 400 grams

        Barcode validBarcode = new Barcode(new Numeral[]{
                Numeral.valueOf((byte) 8),
                Numeral.valueOf((byte) 9),
                Numeral.valueOf((byte) 0),
                Numeral.valueOf((byte) 1),
                Numeral.valueOf((byte) 2)
            });
    	BarcodedItem appleItem = new BarcodedItem(validBarcode, new Mass(400000000));
    	manager.getStation().getBaggingArea().addAnItem(appleItem);

    	// remove apple from groceries
    	manager.removeProduct(appleProduct, 1);
    	Assert.assertFalse(manager.getExpectedWeight() == appleWeight);

    }
    
    // test remove item | groceries cost should change
    @Test
    public void testCostChangeWhenRemoved() {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
        
        BarcodedProduct appleProduct = ProductTestUtils.getProductByDescription("apple");
        BigDecimal appleWeight = BigDecimal.valueOf((long) appleProduct.getExpectedWeight());
        
        manager.addProduct(appleProduct, "apple"); // 400 grams

        Barcode validBarcode = new Barcode(new Numeral[]{
                Numeral.valueOf((byte) 8),
                Numeral.valueOf((byte) 9),
                Numeral.valueOf((byte) 0),
                Numeral.valueOf((byte) 1),
                Numeral.valueOf((byte) 2)
            });
    	BarcodedItem appleItem = new BarcodedItem(validBarcode, new Mass(400000000));
    	manager.getStation().getBaggingArea().addAnItem(appleItem);

    	// remove apple from groceries
    	manager.removeProduct(appleProduct, 1);
    	Assert.assertNotEquals(manager.getGroceriesCost(), BigDecimal.valueOf(appleProduct.getPrice()));
    }
    
    // test remove item | groceries list should change
    @Test
    public void testGroceriesChangeWhenRemoved() {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
        
        BarcodedProduct appleProduct = ProductTestUtils.getProductByDescription("apple");
        BigDecimal appleWeight = BigDecimal.valueOf((long) appleProduct.getExpectedWeight());
        
        manager.addProduct(appleProduct, "apple"); // 400 grams

        Barcode validBarcode = new Barcode(new Numeral[]{
                Numeral.valueOf((byte) 8),
                Numeral.valueOf((byte) 9),
                Numeral.valueOf((byte) 0),
                Numeral.valueOf((byte) 1),
                Numeral.valueOf((byte) 2)
            });
    	BarcodedItem appleItem = new BarcodedItem(validBarcode, new Mass(400000000));
    	manager.getStation().getBaggingArea().addAnItem(appleItem);

    	// remove apple from groceries
    	manager.removeProduct(appleProduct, 1);
    	Assert.assertFalse(manager.getGroceries().containsKey(appleProduct));
    }
    
    // test if weight discrepancy when remove item from order but not from scale
    @Test
    public void testRemoveFromListNotScale() throws OverloadedDevice {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
        
        BarcodedProduct appleProduct = ProductTestUtils.getProductByDescription("apple");
        BigDecimal appleWeight = BigDecimal.valueOf((long) appleProduct.getExpectedWeight());
        
        manager.addProduct(appleProduct, "apple"); // 400 grams

        Barcode validBarcode = new Barcode(new Numeral[]{
                Numeral.valueOf((byte) 8),
                Numeral.valueOf((byte) 9),
                Numeral.valueOf((byte) 0),
                Numeral.valueOf((byte) 1),
                Numeral.valueOf((byte) 2)
            });
    	BarcodedItem appleItem = new BarcodedItem(validBarcode, new Mass(400000000));
    	manager.getStation().getBaggingArea().addAnItem(appleItem);

    	// remove apple from groceries
    	manager.removeProduct(appleProduct, 1);
    	Assert.assertTrue(manager.hasWeightDiscrepancy());
    }
    
    @Test
    public void testAddProductCorrectlyUpdatesWeightAndCost() {
        ShoppingManager manager = initShoppingManager();
        ProductTestUtils.fillDatabasesBarcoded();
        
        // Retrieve a product from the test database
        BarcodedProduct product = ProductTestUtils.getProductByDescription("apple");
        assertNotNull("Product should not be null", product);
        
        BigDecimal weight = new BigDecimal("400"); // 400g for simplicity
        BigDecimal expectedCost = new BigDecimal(product.getPrice()); // Assuming cost is equal to the price for simplicity

        // Act: Add the product to the shopping manager
        manager.addProduct(product, "product");

        // Assert: Verify that the expected weight has been updated correctly
        assertEquals("Expected weight should match the product weight after addition",
                    weight, manager.getExpectedWeight());

        // Assert: Verify that the groceries cost has been updated correctly
        assertEquals("Total groceries cost should match the product price after addition",
                    expectedCost, manager.getGroceriesCost());
    }

   
}
