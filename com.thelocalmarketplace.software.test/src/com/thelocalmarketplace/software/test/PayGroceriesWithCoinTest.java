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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinSlot;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;

import payment.PayGroceriesWithCoin;
import shopping.interfaces.IPaymentListener;
import shopping.manager.ConfigCheckoutStation;
import shopping.manager.ShoppingManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

//Creates setup for Billing
class ShoppingManagerStub extends ShoppingManager {
    public ShoppingManagerStub(String stationType) {
        super(stationType);
    }
    
    public ShoppingManagerStub(String stationType, ConfigCheckoutStation config) {
    	super(stationType, config);
    }

    private boolean isPaid = false;

    @Override
    public BigDecimal getGroceriesCost() {
        //Returns a fixed cost for groceries to simplify the test setup
        return new BigDecimal(100);
    }

    @Override
    public void notifyPayment() {
        //Marks transaction as paid for testing validation logic
        isPaid = true;
    }

    public boolean isPaid() {
        //Exposes the payment status for assertion in tests
        return isPaid;
    }

    public void setGroceriesCost(BigDecimal valueOf) {
        groceriesCost = valueOf;
    }
}

//Dummy listener
class PaymentListenerStub implements IPaymentListener {
	private boolean notified = false;
	
	@Override
	public void notifyPayment() {
		notified = true;
		
	}
	
	public boolean isNotified() {
		return notified;
	}

	@Override
	public void notifyPaidAmountChanged() {}

	@Override
	public void notifyDanglingBanknote() {}
}


public class PayGroceriesWithCoinTest {
    private PayGroceriesWithCoin payGroceriesWithCoin;
    private ShoppingManagerStub shoppingManagerStub;
    private CoinValidator coinValidator;
    private List<Coin> coins;
    private CoinSlot coinSlot;

    @Before
    public void setUp() {
        //Initializes test fixtures before every test execution
        //CoinValidator setup with a specific currency and denominations for comprehensive testing.
        BigDecimal[] coinDenominations = {
                new BigDecimal("0.01"),
                new BigDecimal("0.05"),
                new BigDecimal("0.10"),
                new BigDecimal("0.25"),
                new BigDecimal("0.50"),
                new BigDecimal("1.00"),
                new BigDecimal("10.0"),
                new BigDecimal("30.0")
            };
        
        ConfigCheckoutStation config = new ConfigCheckoutStation();
        config.setCoinDenominations(coinDenominations);
        config.setCurrency("USD");
        shoppingManagerStub = new ShoppingManagerStub("Bronze", config);
        //Uses stub to bypass hardware interaction
        payGroceriesWithCoin = new PayGroceriesWithCoin(shoppingManagerStub);
        payGroceriesWithCoin.addListener(shoppingManagerStub);
        this.coinValidator = shoppingManagerStub.getStation().getCoinValidator();
        
        // make a bunch of coins
        coins = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            coins.add(new Coin(Currency.getInstance("USD"), BigDecimal.valueOf(10.00)));
        }
        coins.add(new Coin(Currency.getInstance("USD"), BigDecimal.valueOf(30.00)));
        
        coinSlot = shoppingManagerStub.getStation().getCoinSlot();
    }
    
    private void payForGroceries() {
    	BigDecimal[] coinsInserted = {
    			new BigDecimal("25.00"),
    			new BigDecimal("25.00"),
    			new BigDecimal("25.00"),
    			new BigDecimal("25.00")
    	};
    	
    	for (BigDecimal coinValue : coinsInserted) {
    		// Each insertion simulates adding to the total payment.
    		payGroceriesWithCoin.validCoinDetected(coinValidator, coinValue);
    	}
    }

    ////Validates the response in an invalid coin scenario
    @Test
    public void testInvalidCoinDetected() {
    	Coin invalidCoin = new Coin(Currency.getInstance("CAD"), BigDecimal.valueOf(15));
    	try {
			coinSlot.receive(invalidCoin);
		} catch (DisabledException | CashOverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        List<Coin> coinList = shoppingManagerStub.getStation().getCoinTray().collectCoins();
        System.out.println("coin list = " + coinList);
        Assert.assertTrue(coinList.get(0).getValue().compareTo(BigDecimal.valueOf(15)) == 0);
    }

    //Ensures there is correct behaviour when valid coins are entered to simulate a complete payment
    @Test
    public void testValidCoinPaymentDetected() {
        payForGroceries();

        // Check if the groceries are marked as paid after inserting enough coins
        Assert.assertTrue(shoppingManagerStub.isPaid());
    }

    // ensures that if not enough coins are inserted to cover the total cost, the payment is not marked complete
    @Test
    public void testPartialPaymentNotComplete() {

        // total cost is 100 as set up in ShoppingManagerStub
        BigDecimal[] coinsInserted = {
            new BigDecimal("25.00"),
            new BigDecimal("25.00"),
            new BigDecimal("25.00") // Total only 75, not enough to cover the total cost
        };
        
        for (BigDecimal coinValue : coinsInserted) {
            payGroceriesWithCoin.validCoinDetected(coinValidator, coinValue);
        }
    
        // Payment should not be complete as the total paid is less than the total cost
        Assert.assertFalse(shoppingManagerStub.isPaid());
    }
    
    // simulate an overpayment scenario where the customer inserts more money than the cost of the groceries
    @Test
    public void testOverPayment() throws NoCashAvailableException{
        // Insert more coins than necessary
        BigDecimal[] coinsInserted = {
            new BigDecimal("50.00"),
            new BigDecimal("75.00"), 
            //new BigDecimal("50.00") // Total 150, over the necessary amount set in the stub
        };
        
        for (BigDecimal coinValue : coinsInserted) {
            payGroceriesWithCoin.validCoinDetected(coinValidator, coinValue);
        }
        Assert.assertTrue(shoppingManagerStub.isPaid());
    }

	@Test
	public void testExactPaymentDetected() {
	    // Assuming the exact cost of groceries is 100 (set 100 in stub)
	    BigDecimal[] coinsInserted = {
	        new BigDecimal("25.00"),
	        new BigDecimal("25.00"),
	        new BigDecimal("25.00"),
	        new BigDecimal("25.00")
	    };
	
	    for (BigDecimal coinValue : coinsInserted) {
	        payGroceriesWithCoin.validCoinDetected(coinValidator, coinValue);
	    }
	    // Verify that groceries are marked as paid
	    Assert.assertTrue("Groceries should be marked as paid for exact payment", shoppingManagerStub.isPaid());
	}

    @Test
	public void testNoPaymentDetected() {
	    // No coins inserted
	    
	    // Verify that groceries are not marked as paid without any payment
	    Assert.assertFalse("Groceries should not be marked as paid without payment", shoppingManagerStub.isPaid());
	}

    // checks for multiple invalid coin insertion
    @Test
    public void testMultipleInvalidCoins() {
        Coin invalidCoin = new Coin(Currency.getInstance("CAD"), BigDecimal.valueOf(100.0));
        try {
			shoppingManagerStub.getStation().getCoinSlot().receive(invalidCoin);
		} catch (DisabledException | CashOverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Ensures that the payment status is not affected by invalid coins
        Assert.assertFalse(shoppingManagerStub.isPaid());
    }

    // checks for a mix of valid and invalid coints
    @Test
    public void testMixOfValidAndInvalidCoins() {
    	// insert 8 valid coins
    	for (int i = 0; i < 8; i++) {
    		try {
				shoppingManagerStub.getStation().getCoinSlot().receive(coins.get(0));
			} catch (DisabledException | CashOverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	// shouldnt be paid yet since we have only paid 80
        Assert.assertFalse(shoppingManagerStub.isPaid());

    	// insert 1 invalid coin.
        Coin invalidCoin = new Coin(Currency.getInstance("CAD"), BigDecimal.valueOf(30.0));
        try {
			shoppingManagerStub.getStation().getCoinSlot().receive(invalidCoin);
		} catch (DisabledException | CashOverloadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Payment should not be complete as invalid coins do not contribute to the total
        Assert.assertFalse(shoppingManagerStub.isPaid());
    }


    @Test
    public void testRepeatedPaymentAttemptsAfterCompletion() {
        // Complete the payment first
        BigDecimal[] coinsInsertedForCompletion = {
            new BigDecimal("50.00"),
            new BigDecimal("50.00")
        };
        
        for (BigDecimal coinValue : coinsInsertedForCompletion) {
            payGroceriesWithCoin.validCoinDetected(coinValidator, coinValue);
        }

        // Assert payment was initially marked as complete
        Assert.assertTrue("Payment should be marked as complete", shoppingManagerStub.isPaid());

        // Attempt to insert more coins after payment is complete
        BigDecimal[] additionalCoinsInserted = {
            new BigDecimal("25.00")
        };

        for (BigDecimal coinValue : additionalCoinsInserted) {
            payGroceriesWithCoin.validCoinDetected(coinValidator, coinValue);
        }

        // Check if payment status remains unchanged after attempting further payment
        Assert.assertTrue("Payment status should not change after completion", shoppingManagerStub.isPaid());
    }

    @Test
    public void testMultipleValidCoins() {
        BigDecimal[] coinsInsertedSimultaneously = {
            new BigDecimal("50.00"),
            new BigDecimal("50.00") 
        };
        for (BigDecimal coinValue : coinsInsertedSimultaneously) {
            payGroceriesWithCoin.validCoinDetected(coinValidator, coinValue);
        }
        Assert.assertTrue("Payment should be marked as complete after simultaneous valid coin insertions", shoppingManagerStub.isPaid());
    }
    
    // test for overpayment | correct amount of change
    @Test
    public void testForCorrectChange() {
        // Insert more coins than necessary
        CoinSlot coinSlot = shoppingManagerStub.getStation().getCoinSlot();
        for (Coin coin : coins) {
            try {
            	
                shoppingManagerStub.getStation().getCoinDispensers().get(BigDecimal.valueOf(10.00)).load(coin);
				coinSlot.receive(coin);
			} catch (DisabledException | CashOverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    
        // collect the coins from cointray
        List<Coin> coinList = shoppingManagerStub.getStation().getCoinTray().collectCoins();
        Assert.assertTrue(coinList.get(0).getValue().equals(BigDecimal.valueOf(10.00)));
    }
    
    // test for overpayment | not enough coins in dispenser. Tray should be empty
    @Test
    public void testNotEnoughChange() throws NoCashAvailableException{
        CoinSlot coinSlot = shoppingManagerStub.getStation().getCoinSlot();
        for (Coin coin : coins) {
            try {
				coinSlot.receive(coin);
			} catch (DisabledException | CashOverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        Assert.assertTrue(shoppingManagerStub.getStation().getCoinTray().collectCoins().isEmpty());    
    }

    
}  
