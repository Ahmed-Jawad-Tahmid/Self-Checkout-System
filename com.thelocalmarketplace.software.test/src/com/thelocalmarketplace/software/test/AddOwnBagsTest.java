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

import java.math.BigDecimal;
import shopping.manager.ShoppingManager;
import payment.AddOwnBags;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
// This class tests the AddOwnBags class
public class AddOwnBagsTest {
    
    private AddOwnBags addOwnBags;
    private ShoppingManagerStub shoppingManagerStub;

    @Before
    public void setUp() {
        shoppingManagerStub = new ShoppingManagerStub("Bronze");
        addOwnBags = new AddOwnBags(shoppingManagerStub);
    }

    // Test to confirm a bag is added by the customer
    @Test
    public void testUserConfirmation() {
        addOwnBags.userConfirmation();
        Assert.assertEquals(1, shoppingManagerStub.getBagCount());
    }
    
    // Checks that handleBagsTooHeavy is called if there is a weight discrepancy
    @Test
    public void testBagsHaveBeenAddedWithWeightDiscrepancy() {
        shoppingManagerStub.setHasWeightDiscrepancy(true);
        addOwnBags.bagsHaveBeenAdded();
        assertTrue(shoppingManagerStub.isHandleBagsTooHeavyCalled());
    }
    
    // Checks that bagsHaveBeenAdded does not call an attendant if there is no weight discrepancy
    @Test
    public void testBagsHaveBeenAddedWithoutWeightDiscrepancy() {
        shoppingManagerStub.setHasWeightDiscrepancy(false);
        addOwnBags.bagsHaveBeenAdded();
        assertFalse(shoppingManagerStub.isAttendantApproved());
    }
    // Test to check that enable station is called when bags are too heavy and attendant approves

    @Test
    public void testHandleBagsTooHeavyAttendantApproved() {
        addOwnBags.handleBagsTooHeavy(new BigDecimal(1000));
        shoppingManagerStub.setAttendantApproved(true);
        assertTrue(shoppingManagerStub.isAttendantApproved());
    }
    // Test to check the scenario when bags are too heavy but the attendant has not approved
    @Test
    public void testHandleBagsTooHeavyAttendantNotApproved() {
        shoppingManagerStub.setAttendantApproved(false);
        addOwnBags.handleBagsTooHeavy(new BigDecimal(1000));
        assertFalse(shoppingManagerStub.isEnableStationCalled());
        assertFalse(shoppingManagerStub.isAttendantApproved());
    }

    private class ShoppingManagerStub extends ShoppingManager {
        // A stub of ShoppingManager created for testing
        public ShoppingManagerStub(String stationType) {
            super(stationType);
        }
                
        private boolean hasWeightDiscrepancy;
        private int bagCount = 0;
        private boolean attendantApproved = false;
        private boolean enableStationCalled = false;
        private boolean handleBagsTooHeavyCalled = true;

        public void setHasWeightDiscrepancy(boolean hasWeightDiscrepancy) {
            this.hasWeightDiscrepancy = hasWeightDiscrepancy;
        }

        @Override
	    public void setAttendantApproved(boolean attendantApproved) {
		    this.attendantApproved = attendantApproved;
	    }
        @Override
        public boolean isAttendantApproved() {
            return attendantApproved;
        }

        @Override
        public boolean hasWeightDiscrepancy() {
            return hasWeightDiscrepancy;
        }
        public void addBag() {
            this.bagCount++;
        }

        public int getBagCount() {
            return bagCount;
        }

        public void enableStation() {
            this.enableStationCalled = true;
        }

        
        public boolean isEnableStationCalled() {
            return enableStationCalled;
        }

        
        public void handleBagsTooHeavy(BigDecimal weight) {
            this.handleBagsTooHeavyCalled = false;
        }
    
        public boolean isHandleBagsTooHeavyCalled() {
            return handleBagsTooHeavyCalled;
        }

        }
    }



