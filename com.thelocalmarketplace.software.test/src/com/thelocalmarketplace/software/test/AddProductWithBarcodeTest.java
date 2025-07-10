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
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import org.junit.Test;
import product.*;
import org.junit.Before;
import java.util.NoSuchElementException;

import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import shopping.interfaces.IProductListener;
import shopping.manager.ShoppingManager;

import org.junit.Assert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * Unit tests for the AddProductWithBarcode class.
 * 
 */

public class AddProductWithBarcodeTest {

    private AddProductWithBarcode addProductWithBarcode;
    
    /**
     * Sets up the test environment by pre-filling the database with barcoded items
     * and instantiating the AddProductWithBarcode object.
     */

    @Before
    public void setUp() {
        // Pre-fill the database with barcoded items
        ProductTestUtils.fillDatabasesBarcoded();
        
        // Instantiate the AddProductWithBarcode object
        addProductWithBarcode = new AddProductWithBarcode();
    }
    
    /**
     * Tests the addition of a product with a valid barcode.
     *
     * @param None
     * @return None
     */

    @Test
    public void testAddProductWithValidBarcode() {
        // Create a valid barcode
        Numeral[] numerals = new Numeral[] {
                Numeral.valueOf((byte) 1),
                Numeral.valueOf((byte) 2),
                Numeral.valueOf((byte) 3),
                Numeral.valueOf((byte) 4),
                Numeral.valueOf((byte) 5)
        };
        Barcode barcode = new Barcode(numerals);

        // Simulate scanning a barcode
        addProductWithBarcode.aBarcodeHasBeenScanned(null, barcode);

        assertNotNull(ProductDatabases.BARCODED_PRODUCT_DATABASE.get(barcode));
    }
    
    /**
     * Tests the addition of a product with an invalid barcode.
     *
     * @param None
     * @return NoSuchElementException if an invalid barcode is provided
     */
    

    @Test(expected = NoSuchElementException.class)
    public void testAddProductWithInvalidBarcode() {
        // Create a barcode that does not exist in the database
        Barcode barcode = new Barcode(new Numeral[]{
            Numeral.valueOf((byte) 9),
            Numeral.valueOf((byte) 8),
            Numeral.valueOf((byte) 7),
            Numeral.valueOf((byte) 6),
            Numeral.valueOf((byte) 5)
        });

        // Simulate scanning a barcode
        addProductWithBarcode.aBarcodeHasBeenScanned(null, barcode);
    }
    
    /**
     * Verifies that a product is added correctly when the barcode scanner is enabled.
     *
     * @param None
     * @return None
     */

    @Test
    public void testAddProductWhenEnabled() {
        final List<Product> addedProducts = new ArrayList<>();

        IProductListener listener = (product, name) -> addedProducts.add(product);
        addProductWithBarcode.addListener(listener);
        
        // Ensure the device is enabled
        addProductWithBarcode.aDeviceHasBeenEnabled(null);

        // Create a valid barcode that corresponds to an item in the database
        Barcode validBarcode = new Barcode(new Numeral[]{
            Numeral.valueOf((byte) 1),
            Numeral.valueOf((byte) 2),
            Numeral.valueOf((byte) 3),
            Numeral.valueOf((byte) 4),
            Numeral.valueOf((byte) 5)
        });

        // Simulate scanning a valid barcode
        addProductWithBarcode.aBarcodeHasBeenScanned(null, validBarcode);

        // Assert that one product was added
        Assert.assertEquals(1, addedProducts.size());
    }
    
    /**
     * Tests that no product is added when the barcode scanner is disabled.
     *
     * @param None
     * @return None
     */

    @Test
    public void testDeviceDisabledDoesNotAddProduct() {
        // Initially disable the device
        addProductWithBarcode.aDeviceHasBeenDisabled(null);

        // Create a dummy listener that adds to a list to check if any products are added
        final List<Product> addedProducts = new ArrayList<>();

        IProductListener dummyListener = new IProductListener() {
            @Override
            public void addProduct(Product product, String name) {
                addedProducts.add(product);
            }
        };
    
        // Add the listener
        addProductWithBarcode.addListener(dummyListener);

        // Create a barcode that corresponds to an item in the database
        Barcode barcode = new Barcode(new Numeral[]{
            Numeral.valueOf((byte) 1),
            Numeral.valueOf((byte) 2),
            Numeral.valueOf((byte) 3),
            Numeral.valueOf((byte) 4),
            Numeral.valueOf((byte) 5)
        });

        // Simulate scanning a barcode with the device disabled
        addProductWithBarcode.aBarcodeHasBeenScanned(null, barcode);

        // Assert that no products were added
        Assert.assertTrue(addedProducts.isEmpty());
    }
    
    /**
     * Verifies that after removing a listener, it no longer receives product add events.
     *
     * @param None
     * @return None
     */

    @Test
    public void testRemoveListener() {
        final List<Product> addedProducts = new ArrayList<>();

        IProductListener listener = (product, name) -> addedProducts.add(product);
        addProductWithBarcode.addListener(listener);
        addProductWithBarcode.removeListener(listener);

        // Create a valid barcode that corresponds to an item in the database
        Barcode validBarcode = new Barcode(new Numeral[]{
            Numeral.valueOf((byte) 1),
            Numeral.valueOf((byte) 2),
            Numeral.valueOf((byte) 3),
            Numeral.valueOf((byte) 4),
            Numeral.valueOf((byte) 5)
        });

        // Simulate scanning a valid barcode
        addProductWithBarcode.aBarcodeHasBeenScanned(null, validBarcode);

        // Assert that the listener did not receive any products
        Assert.assertTrue(addedProducts.isEmpty());
    }
    
    /**
     * Verifies that when multiple listeners are added, they all receive the product add events.
     *
     * @param None
     * @return None
     */

    @Test
    public void testMultipleListeners() {
        final List<Product> addedProducts1 = new ArrayList<>();
        final List<Product> addedProducts2 = new ArrayList<>();

        IProductListener listener1 = (product, name) -> addedProducts1.add(product);
        IProductListener listener2 = (product, namet) -> addedProducts2.add(product);
        
        addProductWithBarcode.addListener(listener1);
        addProductWithBarcode.addListener(listener2);

        // Create a valid barcode that corresponds to an item in the database
        Barcode validBarcode = new Barcode(new Numeral[]{
            Numeral.valueOf((byte) 1),
            Numeral.valueOf((byte) 2),
            Numeral.valueOf((byte) 3),
            Numeral.valueOf((byte) 4),
            Numeral.valueOf((byte) 5)
        });

        // Simulate scanning a valid barcode
        addProductWithBarcode.aBarcodeHasBeenScanned(null, validBarcode);

        // Assert that both listeners received the product
        Assert.assertEquals(1, addedProducts1.size());
        Assert.assertEquals(1, addedProducts2.size());
    }

    /**
     * Verifies that no product is added when the barcode scanner is turned off.
     *
     * @param None
     * @return None
     */

    @Test
        public void testAddProductWhenTurnedOff() {
            final List<Product> addedProducts = new ArrayList<>();
    
            IProductListener listener = (product, name) -> addedProducts.add(product);
            addProductWithBarcode.addListener(listener);
    
            // Turn off the device
            addProductWithBarcode.aDeviceHasBeenTurnedOff(null);
    
            // Create a valid barcode that corresponds to an item in the database
            Barcode validBarcode = new Barcode(new Numeral[]{
                Numeral.valueOf((byte) 1),
                Numeral.valueOf((byte) 2),
                Numeral.valueOf((byte) 3),
                Numeral.valueOf((byte) 4),
                Numeral.valueOf((byte) 5)
            });
    
            // Simulate scanning a valid barcode
            addProductWithBarcode.aBarcodeHasBeenScanned(null, validBarcode);
    
            // Assert that no products were added
            Assert.assertTrue(addedProducts.isEmpty());
        }
    
    
    // test for scanning with handheld scanner
    @Test
    public void testHandheldScannerBronze() {
    	// initialize self checkout station and manager
    	ShoppingManager manager = new ShoppingManager("Bronze");
    	    	
    	// create barcoded item
        Barcode validBarcode = new Barcode(new Numeral[]{
                Numeral.valueOf((byte) 8),
                Numeral.valueOf((byte) 9),
                Numeral.valueOf((byte) 0),
                Numeral.valueOf((byte) 1),
                Numeral.valueOf((byte) 2)
            });
        
    	BarcodedItem apple = new BarcodedItem(validBarcode, new Mass(400000000));
    	Product appleProduct = ProductTestUtils.getProductByDescription("apple");

    	
    	// scan item with handheld scanner
    	manager.getStation().getHandheldScanner().scan(apple);
    	
    	// assert true that grocery list contains apple
    	Map<Product, ProductInfo> groceries = manager.getGroceries();
    	Assert.assertTrue("Apple not found in groceries", groceries.containsKey(appleProduct));
    	
    	// assert that the expected weight is correct
    	BigDecimal expectedWeight = BigDecimal.valueOf(400);
    	Assert.assertTrue(expectedWeight.compareTo(groceries.get(appleProduct).getWeight()) == 0);
    }
    
    // test if devices turn are disabled properly when an object is scanned
    @Test
    public void testHandheldScannerDisable() {
    	// initialize self checkout station and manager
    	ShoppingManager manager = new ShoppingManager("Bronze");
    	    	
    	// create barcoded item
        Barcode validBarcode = new Barcode(new Numeral[]{
                Numeral.valueOf((byte) 8),
                Numeral.valueOf((byte) 9),
                Numeral.valueOf((byte) 0),
                Numeral.valueOf((byte) 1),
                Numeral.valueOf((byte) 2)
            });
      
    	BarcodedItem apple = new BarcodedItem(validBarcode, new Mass(400000));
    	
    	// scan item with handheld scanner
    	manager.getStation().getHandheldScanner().scan(apple);
    	
    	// assertTrue that devices have been disabled
    	Assert.assertTrue(manager.getStation().getMainScanner().isDisabled());
    	Assert.assertTrue(manager.getStation().getHandheldScanner().isDisabled());
    }
    
}
