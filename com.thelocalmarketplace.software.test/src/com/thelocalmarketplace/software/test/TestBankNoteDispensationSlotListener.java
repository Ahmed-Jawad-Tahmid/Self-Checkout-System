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
import java.util.List;

import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteDispensationSlotObserver;

public class TestBankNoteDispensationSlotListener implements BanknoteDispensationSlotObserver {
	public static BigDecimal totalChangeDispensed = BigDecimal.ZERO;

	@Override
	public void enabled(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disabled(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnedOn(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnedOff(IComponent<? extends IComponentObserver> component) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void banknoteDispensed(BanknoteDispensationSlot slot, List<Banknote> banknotes) {
		// TODO Auto-generated method stub
		totalChangeDispensed = BigDecimal.ZERO;
		for (Banknote banknote : banknotes) {
			totalChangeDispensed = totalChangeDispensed.add(banknote.getDenomination());
		}
	}

	@Override
	public void banknotesRemoved(BanknoteDispensationSlot slot) {
		// TODO Auto-generated method stub
		
	}
	
	public BigDecimal getTotalChangeDispensed() {
		return totalChangeDispensed;
	}
	
}
