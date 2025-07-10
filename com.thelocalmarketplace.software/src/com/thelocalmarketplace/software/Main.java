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

package com.thelocalmarketplace.software;

import java.util.Calendar;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import product.VisualCatalogDatabase;
import product.VisualProductInfo;

public class Main {
	public static void main(String[] args) {
		initializeDatabase();
		
		new AttendantView();
		
	
	}

	private static void initializeDatabase() {
		// initialize database
		String[] ITEM_DESCRIPTIONS = {"Milk", "Bread", "Meat", "Cheese", "Juice", "Soda", "Water", "apple", "banana"};
		String[] ITEM_IMAGES = {"https://upload.wikimedia.org/wikipedia/commons/2/28/Milk_001.JPG", 
				"https://thumbs.dreamstime.com/b/bread-cut-14027607.jpg",								
				"https://t4.ftcdn.net/jpg/00/83/36/05/360_F_83360582_oxUzWNwMqPLewOONSG5V8Kb6kfmDkdeP.jpg", 
				"https://heygrillhey.com/wp-content/uploads/2018/12/Smoked-Cheese-Feature-500x500.png", 
				"https://www.drinkwell.ca/cdn/shop/products/Well_OJ_750_1000x.png?v=1652900719", 
				"https://media.officedepot.com/images/f_auto,q_auto,e_sharpen,h_450/products/208206/208206",
				"FAKE URL 1", 
				"FAKE URL 2", 
				"FAKE URL 3", 
		};
		
		if (!ProductDatabases.BARCODED_PRODUCT_DATABASE.isEmpty())
            return;
		
		//Barcoded Products
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
            

            BarcodedProduct pBarcode = new BarcodedProduct(itemCode, ITEM_DESCRIPTIONS[i], i + 1, (i + 1) * 50);
            ProductDatabases.BARCODED_PRODUCT_DATABASE.put(itemCode, pBarcode);
            
            //PLUcoded products
            String rawCode = "000" + i;
            PriceLookUpCode code = new PriceLookUpCode(rawCode);
            PLUCodedProduct pPLU = new PLUCodedProduct(code, ITEM_DESCRIPTIONS[i], i+1);
            ProductDatabases.PLU_PRODUCT_DATABASE.put(code, pPLU);
            
            //add to the visual catalog, which holds visual information
            if (i > 3)
            {
            	VisualCatalogDatabase.addProduct(pBarcode, ITEM_IMAGES[i], ITEM_DESCRIPTIONS[i]);  
            	System.out.println("A barcoded product is " + ITEM_DESCRIPTIONS[i]);
            }
            else {
            	VisualCatalogDatabase.addProduct(pPLU, ITEM_IMAGES[i], ITEM_DESCRIPTIONS[i]);            	
            	System.out.println("A PLU product is " + ITEM_DESCRIPTIONS[i]);
            }

        }
	}
}
