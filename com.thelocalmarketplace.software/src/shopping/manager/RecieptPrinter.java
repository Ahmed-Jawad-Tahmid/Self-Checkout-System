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

package shopping.manager;

import java.math.BigDecimal;
import java.util.Map;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.ReceiptPrinterListener;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.thelocalmarketplace.hardware.Product;

//import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.NoPowerException;

import product.ProductInfo;

public class RecieptPrinter {
	
	private IReceiptPrinter iReceiptPrinter;

	public RecieptPrinter(IReceiptPrinter printer) {
		iReceiptPrinter = printer;
	}
	
    /**
     * Generates a receipt string based on the list of groceries and their costs.
     *
     * @param groceries     Dictionary mapping products to their quantities.
     * @param groceryCosts  Total cost of groceries.
     * @return String representing the receipt.
     */
    public static String getReciept(Map<Product, ProductInfo> groceries, BigDecimal groceryCosts)  {
        StringBuilder receipt = new StringBuilder();

        // Header
        receipt.append("----- Receipt -----\n");

        // Items
        for (Map.Entry<Product, ProductInfo> entry : groceries.entrySet()) {
            Product product = entry.getKey();
            ProductInfo productInfo = entry.getValue();
            BigDecimal cost = productInfo.getPrice();
            int count = productInfo.getCount();
            receipt.append(productInfo.getName()).append(" x").append(count).append(": $").append(cost).append("\n");
        }

        // Total
        receipt.append("Total: $").append(groceryCosts).append("\n");

        // Footer
        receipt.append("Thank you for shopping with us!\n");

        return receipt.toString();
    }
    
 // Implementing IReceiptPrinter interface methods
    
    public void print(char c) throws EmptyDevice, OverloadedDevice {
        // Implementation for printing a character
        // Not provided in the given code
    }

    
    public void cutPaper() {
        // Implementation for cutting paper
        // Not provided in the given code
    	iReceiptPrinter.cutPaper();
    }
    
    
    public String removeReceipt() {
        // Implementation for removing receipt
        // Not provided in the given code
    	iReceiptPrinter.removeReceipt();
        return null;
    }
    
    // Implementing ReceiptPrinterListener interface methods
    public void thePrinterIsOutOfPaper() {
        // Handle out of paper event
        System.out.println("The printer is out of paper.");
    }

  
    public void thePrinterIsOutOfInk() {
        // Handle out of ink event
        System.out.println("The printer is out of ink.");
    }

    
    public void thePrinterHasLowInk() {
        // Handle low ink event
        System.out.println("The printer has low ink.");
    }

    
    public void thePrinterHasLowPaper() {
        // Handle low paper event
        System.out.println("The printer has low paper.");
    }

   
    public void paperHasBeenAddedToThePrinter() {
        // Handle paper added event
        System.out.println("Paper has been added to the printer.");
    }

    
    public void inkHasBeenAddedToThePrinter() {
        // Handle ink added event
        System.out.println("Ink has been added to the printer.");
    }

    
    public abstract class IDeviceListner implements ReceiptPrinterListener{
    	
    	@Override
        public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
            System.out.println("Device has been enabled.");
        }
    	
    	@Override
        public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
            throw new NoPowerException();
        }
    	
    	@Override
        public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
            System.out.println("Device has been turned on.");
        }
    	
    	@Override
    	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
	    	 throw new NoPowerException();
	    	 }
    	
    	
    	
    }
    
    
}
