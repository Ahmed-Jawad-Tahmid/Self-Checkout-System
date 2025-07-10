package com.thelocalmarketplace.software.test;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import shopping.manager.ConfigCheckoutStation;
import shopping.manager.ShoppingManager;
import shopping.manager.logic.RefillLogic;

public class RefillLogicTest {
	private ShoppingManager sm;
	private RefillLogic logic;

	@Before
	public void setUp() {
		sm = new ShoppingManager("Gold", new ConfigCheckoutStation());
		logic = new RefillLogic(sm);
	}

	@Test
	public void testValidBanknoteRefill() {
		// Try refilling the $10 bills
		logic.refillBanknoteDispenser(BigDecimal.TEN, 20);
		assertEquals(sm.getStation().getBanknoteDispensers().get(BigDecimal.TEN).size(), 20);
	}
	
	@Test
	public void testBanknoteOverflowRefill() {
		// Try refilling the $10 bills with over 1000 bills
		logic.refillBanknoteDispenser(BigDecimal.TEN, 1050);
		
		// Should still fill it up all the way, less the extra bills
		assertEquals(sm.getStation().getBanknoteDispensers().get(BigDecimal.TEN).size(), 1000);
	}
	
	@Test
	public void testValidCoinRefill() {
		// Try refilling the $0.01 coins
		logic.refillCoinDispener(new BigDecimal("0.01"), 20);
		assertEquals(sm.getStation().getCoinDispensers().get(BigDecimal.valueOf(0.01)).size(), 20);
	}
	
	@Test
	public void testCoinOverflowRefill() {
		// Try refilling the $0.01 coins with over 100 coins
		logic.refillCoinDispener(new BigDecimal("0.01"), 1500);
		
		// Should still fill it up all the way, less the extra bills
		assertEquals(sm.getStation().getCoinDispensers().get(BigDecimal.valueOf(0.01)).size(), 100);
	}
	

	@Test
	public void testValidInkRefill() {
		// Try refilling the ink
		logic.refillPrinterInk();
		assertEquals(logic.getInkRefilled(), (1 << 20) * 0.9, 1);
	}
	
	@Test
	public void testOverflowInkRefill() {
		// Try refilling the ink twice
		logic.refillPrinterInk();
		assertEquals(logic.getInkRefilled(), (1 << 20) * 0.9, 1);
		
		logic.refillPrinterInk();
		assertEquals(logic.getInkRefilled(), 0, 1);
		
	}
	
	@Test
	public void testValidPaperRefill() {
		// Try refilling the ink
		logic.refillPrinterPaper();
		assertEquals(logic.getPaperRefilled(), (1 << 10) * 0.9, 1);
	}
	
	@Test
	public void testOverflowPaperRefill() {
		// Try refilling the ink twice
		logic.refillPrinterPaper();
		assertEquals(logic.getPaperRefilled(), (1 << 10) * 0.9, 1);
		
		logic.refillPrinterPaper();
		assertEquals(logic.getPaperRefilled(), 0, 1);
		
	}
}