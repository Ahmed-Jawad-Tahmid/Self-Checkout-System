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


import com.jjjwelectronics.scale.IElectronicScale;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import java.math.BigDecimal;
import shopping.manager.ShoppingManager;
import shopping.manager.logic.AbstractShoppingLogic;
import com.jjjwelectronics.OverloadedDevice;


/**
 * The AddOwnBags class handles the scenario where customers add their own bags
 * to the bagging area of a self-checkout system. It extends AbstractShoppingLogic
 * to integrate with the overall shopping logic, including updating the weight
 * calculation to account for added bags.
 */
public class AddOwnBags extends AbstractShoppingLogic {
    
    /**
     * 
     * @param shoppingManager shopping manager associated with checkout station.
     */
    public AddOwnBags(ShoppingManager shoppingManager) {
        super(shoppingManager);
    }

    /**
     * Signals customer intends to add own bags
     * method handles initial action of adding bags
     */
    @Override
    public void userConfirmation() {
        super.getShoppingManager().addBag(); // Notify the shopping manager about the added bag
        System.out.println("SYSTEM: Add own bags selected");
        System.out.println("SYSTEM: Please add your bags");
    }

    /**
     * called after bags have been added by customer
     * Performs necessary checks or updates based on bag addition
     */
    public void bagsHaveBeenAdded() {
        try {
            if (super.getShoppingManager().hasWeightDiscrepancy()) {
                BigDecimal currentWeight = super.getShoppingManager().getWeight();
                handleBagsTooHeavy(currentWeight);
            } else {
                System.out.println("SYSTEM: You may continue shopping");
            }
        } catch (OverloadedDevice e) {
            System.out.println("SYSTEM: The scale is overloaded. Attendant has been notified.");
        }
    }

    /**
     * Handles scenarios where weight in bagging area differs
     *
     * @param currentWeight The weight detected by the scale
     */

    public void handleBagsTooHeavy(BigDecimal currentWeight) {
        System.out.println("SYSTEM: The weight in the bagging area is too heavy.");
         
        super.getShoppingManager().requestForHelp("Weight discrepancy detected due to bags. Attendant has been notified."); 

        if (super.getShoppingManager().isAttendantApproved()){
            enableStation();

        } else {
            System.out.println("SYSTEM: Approval pending from attendant. Please wait.");
        }

        super.shoppingManager.setAttendantApproved(false);

    }

    
}





