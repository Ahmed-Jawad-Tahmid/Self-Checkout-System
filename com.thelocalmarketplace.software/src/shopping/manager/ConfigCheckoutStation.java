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
import java.util.Currency;

public class ConfigCheckoutStation {
    private Currency currency;
    private BigDecimal[] banknoteDenominations;
    private BigDecimal[] coinDenominations;
    private int banknoteStorageUnitCapacity;
    private int coinStorageUnitCapacity;
    private int coinTrayCapacity;
    private int coinDispenserCapacity;
    private BigDecimal bagWeightThreshold;

    public ConfigCheckoutStation() {
        // Set default values
        this.currency = Currency.getInstance("CAD"); // Default currency
        this.banknoteDenominations = new BigDecimal[]{new BigDecimal(1),new BigDecimal(5),new BigDecimal(10), new BigDecimal(20), new BigDecimal(100)};
        this.coinDenominations = new BigDecimal[]{new BigDecimal("0.01"), new BigDecimal("0.05"), new BigDecimal("0.10"), new BigDecimal("0.25"), new BigDecimal("1.00")};
        this.banknoteStorageUnitCapacity = 20;
        this.coinStorageUnitCapacity = 100;
        this.coinTrayCapacity = 50;
        this.coinDispenserCapacity = 100;
        this.bagWeightThreshold = new BigDecimal("5.0");
    }

    // Setters for each configuration option
    public ConfigCheckoutStation setCurrency(String currency2) {
        this.currency = Currency.getInstance(currency2);
        return this;
    }

    public ConfigCheckoutStation setCurrency(Currency currency) {
        this.currency = currency;
        return this;
    }

    public ConfigCheckoutStation setBanknoteDenominations(BigDecimal[] banknoteDenoms) {
        this.banknoteDenominations = banknoteDenoms;
        return this;
    }

    public ConfigCheckoutStation setCoinDenominations(BigDecimal[] coinDenoms) {
        this.coinDenominations = coinDenoms;
        return this;
    }

    public ConfigCheckoutStation setBanknoteStorageUnitCapacity(int capacity) {
        this.banknoteStorageUnitCapacity = capacity;
        return this;
    }

    public ConfigCheckoutStation setCoinStorageUnitCapacity(int capacity) {
        this.coinStorageUnitCapacity = capacity;
        return this;
    }

    public ConfigCheckoutStation setCoinTrayCapacity(int capacity) {
        this.coinTrayCapacity = capacity;
        return this;
    }

    public ConfigCheckoutStation setCoinDispenserCapacity(int capacity) {
        this.coinDispenserCapacity = capacity;
        return this;
    }

    public ConfigCheckoutStation setBagWeightThreshold(BigDecimal threshold) {
        this.bagWeightThreshold = threshold;
        return this;
    }

    // Getters for each configuration option
    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal[] getBanknoteDenominations() {
        return banknoteDenominations;
    }

    public BigDecimal[] getCoinDenominations() {
        return coinDenominations;
    }

    public int getBanknoteStorageUnitCapacity() {
        return banknoteStorageUnitCapacity;
    }

    public int getCoinStorageUnitCapacity() {
        return coinStorageUnitCapacity;
    }

    public int getCoinTrayCapacity() {
        return coinTrayCapacity;
    }

    public int getCoinDispenserCapacity() {
        return coinDispenserCapacity;
    }

    public BigDecimal getBagWeightThreshold() {
        return bagWeightThreshold;
    }
}
