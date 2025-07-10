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

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import shopping.manager.RecieptPrinter;
import com.thelocalmarketplace.hardware.Product;
import product.ProductInfo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class RecieptPrinterTest {

    private Map<Product, ProductInfo> groceries;
    private BigDecimal groceryCosts;

    // Stub class for Product that extends the abstract Product class
    private static class ProductStub extends Product {
        private String name;

        public ProductStub(String name, long price, boolean isPerUnit) {
            super(price, isPerUnit); // Call to the super constructor of Product
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    @Before
    public void setUp() {
        // Initially was new HashMap, however, there was an error that caused the receipt to print in a random order
        // Made a LinkedHashMap to solve this error
        groceries = new LinkedHashMap<>();

        // Assuming weight is not relevant for the receipt in this test scenario
        // If weight is relevant, adjust the test data accordingly
        Product bread = new ProductStub("Bread", 1L, true); // True indicates price is per unit
        ProductInfo breadInfo = new ProductInfo(2, BigDecimal.ZERO, "Bread"); // 2 units of bread, weight irrelavent

        Product milk = new ProductStub("Milk", 2L, true); // True indicates price is per unit
        ProductInfo milkInfo = new ProductInfo(1, BigDecimal.ZERO, "Milk"); // 1 unit of milk, weight irrelavent

        groceries.put(bread, breadInfo);
        groceries.put(milk, milkInfo);

        groceryCosts = new BigDecimal(4); // Total cost, simplified for this example
    }

    @Test
    public void testGetReceipt() {
        String expectedReceipt = "----- Receipt -----\n" +
                "Bread x2: $1\n" +
                "Milk x1: $2\n" +
                "Total: $4\n" +
                "Thank you for shopping with us!\n";

        String actualReceipt = RecieptPrinter.getReciept(groceries, groceryCosts);

        assertEquals("The generated receipt does not match the expected format.", expectedReceipt, actualReceipt);
    }

    @Test
    public void testGetReceipt_Empty() {
        groceries.clear();
        groceryCosts = BigDecimal.ZERO;

        String expected = "----- Receipt -----\nTotal: $0\nThank you for shopping with us!\n";
        String actual = RecieptPrinter.getReciept(groceries, groceryCosts);
        assertEquals(expected, actual);
    }
    @Test
    public void testGetReceipt_OneProduct() {
        groceries.clear();
        groceryCosts = BigDecimal.ZERO;

        Product product = new ProductStub("Apple", 1L, true);
        ProductInfo productInfo = new ProductInfo(1, BigDecimal.ZERO, "Apple");
        groceries.put(product, productInfo);
        groceryCosts = groceryCosts.add(new BigDecimal(product.getPrice()));

        String expected = "----- Receipt -----\nApple x1: $1\nTotal: $1\nThank you for shopping with us!\n";
        String actual = RecieptPrinter.getReciept(groceries, groceryCosts);
        assertEquals(expected, actual);
    }
    @Test
    public void testGetReceipt_MultipleProducts() {
        groceries.clear();
        groceryCosts = BigDecimal.ZERO;

        Product product1 = new ProductStub("Apple", 1L, true);
        ProductInfo productInfo1 = new ProductInfo(2, BigDecimal.ZERO, "Apple");
        groceries.put(product1, productInfo1);
        groceryCosts = groceryCosts.add(new BigDecimal(product1.getPrice() * productInfo1.getCount()));

        Product product2 = new ProductStub("Banana", 2L, true);
        ProductInfo productInfo2 = new ProductInfo(3, BigDecimal.ZERO, "Banana");
        groceries.put(product2, productInfo2);
        groceryCosts = groceryCosts.add(new BigDecimal(product2.getPrice() * productInfo2.getCount()));

        String expected = "----- Receipt -----\nApple x2: $1\nBanana x3: $2\nTotal: $8\nThank you for shopping with us!\n";
        String actual = RecieptPrinter.getReciept(groceries, groceryCosts);
        assertEquals(expected, actual);
    }

    
}

