package shopping.manager.logic;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.ReceiptPrinterListener;
import com.tdc.CashOverloadException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;

import shopping.manager.ShoppingManager;

public class RefillLogic implements ReceiptPrinterListener {
	private ShoppingManager sm;
	private boolean canRefillInk = true;
	private boolean canRefillPaper = true;
	private int inkRefilled;
	private int paperRefilled;

	public RefillLogic(ShoppingManager sm) {
		this.sm = sm;
	}

	/**
	 * Refills the chosen bank note dispenser (Meant to simulate an attendant
	 * physically refilling)
	 * 
	 * @param denomination      The denomination of the target dispenser
	 * @param amountOfBankNotes The amount of bank notes desired to refill
	 */
	public void refillBanknoteDispenser(BigDecimal denomination, int amountOfBankNotes) {
		sm.getStation().getPrinter().register(this);
		for (int i = 0; i < amountOfBankNotes; i++) {
			Banknote note = new Banknote(Currency.getInstance(Locale.CANADA), denomination);
			try {
				this.sm.getStation().getBanknoteDispensers().get(denomination).load(note);
			} catch (CashOverloadException e) {
				// TODO Auto-generated catch block
				System.out.println("Could not add all banknotes, the dispenser is now full");
				break;
			}
		}
	}

	/**
	 * Refills the all bankNote dispensers to the max (Meant to simulate an
	 * attendant physically refilling)
	 * 
	 */
	public void refillAllBanknoteDispensersMax() {
		sm.getStation().getPrinter().register(this);
		for (BigDecimal key : sm.getStation().getBanknoteDispensers().keySet()) {
			for (int i = 0; i < sm.getStation().getBanknoteDispensers().get(key).getCapacity(); i++) {
				Banknote note = new Banknote(Currency.getInstance(Locale.CANADA), key);
				try {
					this.sm.getStation().getBanknoteDispensers().get(key).load(note);
				} catch (CashOverloadException e) {
					// TODO Auto-generated catch block
					break;
				}
			}
		}
		System.out.println("The Banknote Dispensers are now full");

	}

	/**
	 * Refills the chosen coin dispenser (Meant to simulate an attendant physically
	 * refilling)
	 */
	public void refillCoinDispener(BigDecimal denomination, int amountOfCoins) {
		sm.getStation().getPrinter().register(this);
		for (int i = 0; i < amountOfCoins; i++) {
			Coin coin = new Coin(Currency.getInstance(Locale.CANADA), denomination);
			try {
				this.sm.getStation().getCoinDispensers().get(denomination).load(coin);
			} catch (CashOverloadException e) {
				// TODO Auto-generated catch block
				System.out.println("Could not add all coins, the dispenser is now full");
				break;
			}
		}
	}

	/**
	 * Refills the all coin dispensers to the max (Meant to simulate an attendant
	 * physically refilling)
	 */
	public void refillAllCoinDispensersMax() {
		sm.getStation().getPrinter().register(this);
		for (BigDecimal key : sm.getStation().getCoinDispensers().keySet()) {
			for (int i = 0; i < sm.getStation().getCoinDispensers().get(key).getCapacity(); i++) {
				Coin coin = new Coin(Currency.getInstance(Locale.CANADA), key);
				try {
					this.sm.getStation().getCoinDispensers().get(key).load(coin);
				} catch (CashOverloadException e) {
					// TODO Auto-generated catch block
					break;
				}
			}
		}
		System.out.println("All the coin dispensers are now full");
	}

	/**
	 * Refills the receipt printer's ink
	 */
	public void refillPrinterInk() {
		sm.getStation().getPrinter().register(this);
		try {
			if (canRefillInk) {
				this.sm.getStation().getPrinter().addInk((int) Math.floor((1 << 20) * 0.9));
				canRefillInk = false;
				System.out.println("The ink is now full");
			} else {
				System.out.println("The printer has enough ink");
				inkRefilled = 0;
			}
		} catch (OverloadedDevice e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR: The ink overloaded (THIS SHOULD NEVER HAPPEN)");
		}
	}

	/**
	 * Refills the paper in the receipt printer
	 */
	public void refillPrinterPaper() {
		sm.getStation().getPrinter().register(this);
		try {
			if (canRefillPaper) {
				this.sm.getStation().getPrinter().addPaper((int) Math.floor((1 << 10) * 0.9));
				canRefillPaper = false;
				System.out.println("The paper is now full");
			} else {
				System.out.println("The printer has enough paper");
				paperRefilled = 0;
			}
		} catch (OverloadedDevice e) {
			// TODO Auto-generated catch block
			System.out.println("Error: The paper overloaded (THIS SHOULD NEVER HAPPEN)");
		}
	}

	public int getInkRefilled() {
		return inkRefilled;
	}

	public int getPaperRefilled() {
		return paperRefilled;
	}

	@Override
	public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void thePrinterIsOutOfPaper() {
		// TODO Auto-generated method stub
		canRefillPaper = true;
	}

	@Override
	public void thePrinterIsOutOfInk() {
		// TODO Auto-generated method stub
		canRefillInk = true;
	}

	@Override
	public void thePrinterHasLowInk() {
		canRefillInk = true;

	}

	@Override
	public void thePrinterHasLowPaper() {
		canRefillPaper = true;

	}

	@Override
	public void paperHasBeenAddedToThePrinter() {
		// TODO Auto-generated method stub
		canRefillPaper = false;
		paperRefilled = (int) Math.floor((1 << 10) * 0.9);
	}

	@Override
	public void inkHasBeenAddedToThePrinter() {
		// TODO Auto-generated method stub
		canRefillInk = false;
		inkRefilled = (int) Math.floor((1 << 20) * 0.9);
	}
}
