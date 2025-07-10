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

import java.util.Map;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

/**
 * 
 * Utility class for managing test data related to products.
 * 
 */

public class ProductTestUtils {

    private final static String[] ITEM_DESCRIPTIONS = {"Milk", "Bread", "Meat", "Cheese", "Juice", "Soda", "Water", "apple", "banana"};

    /**
     * Fills the barcoded product database with some sample data.
     *
     * @param None
     * @return None
     */
    
    public static final void fillDatabasesBarcoded() {
        if (!ProductDatabases.BARCODED_PRODUCT_DATABASE.isEmpty())
            return;

        for (int i = 0; i < ITEM_DESCRIPTIONS.length; i++) {
            // Generate barcodes using a pattern based on the loop index
            Barcode itemCode = new Barcode(new Numeral[] {
                    Numeral.valueOf((byte)(i + 1)), 
                    Numeral.valueOf((byte)((i + 2) % 10)), 
                    Numeral.valueOf((byte)((i + 3) % 10)),
                    Numeral.valueOf((byte)((i + 4) % 10)),
                    Numeral.valueOf((byte)((i + 5) % 10))
            });
            BarcodedProduct p = new BarcodedProduct(itemCode, ITEM_DESCRIPTIONS[i], i + 1, (i + 1) * 50);
            ProductDatabases.BARCODED_PRODUCT_DATABASE.put(itemCode, p);
        }
    }


    /**
     * Fills the PLU product database with some sample data.
     *
     * @param None
     * @return None
     */
    
    public static final void fillDatabasesPLU() {

        if(ProductDatabases.PLU_PRODUCT_DATABASE.isEmpty() == false)
            return;

        PriceLookUpCode string = new PriceLookUpCode("1234");
        String description = "Hello";
        PLUCodedProduct Tomato = new PLUCodedProduct(string, description, (long) 5.00);

        ProductDatabases.PLU_PRODUCT_DATABASE.put(string, Tomato);

    }
    
    /**
     * Main method for testing the functionality of the class.
     *
     * @param args The command line arguments (not used).
     * @return None
     */

    public static void main(String[] args) {
    	ProductTestUtils.fillDatabasesBarcoded();
    	for(Map.Entry<Barcode, BarcodedProduct> prod : ProductDatabases.BARCODED_PRODUCT_DATABASE.entrySet()) {
    		System.out.println(prod.getKey());
    		System.out.println(prod.getValue().getDescription());
    	}
    }
    
    /**
     * Retrieves a barcoded product from the database based on its description.
     *
     * @param description The description of the product to retrieve.
     * @return The barcoded product corresponding to the given description, or null if not found.
     */

	public static BarcodedProduct getProductByDescription(String string) {
		for (Map.Entry<Barcode, BarcodedProduct> prod : ProductDatabases.BARCODED_PRODUCT_DATABASE.entrySet()) {
			if (prod.getValue().getDescription().equals(string)) {
				return prod.getValue();
			}
		}
		return null;
	}

}