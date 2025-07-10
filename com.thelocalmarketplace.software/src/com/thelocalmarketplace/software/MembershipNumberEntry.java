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

public class MembershipNumberEntry {

  // Can be added to GUI  
  public void signalNumberEntry() {
        System.out.println("Please enter your membership number by typing, swiping, or scanning your membership card.");
    }

    // Methods for the different ways a membership number can be entered
    public void enterNumberByTyping(String membershipNumber) {
        if (isValidNumber(membershipNumber)) {
            System.out.println("Membership number entered by typing: " + membershipNumber);
            // Process the number here.
        } else {
            System.out.println("Invalid membership number. Please try again.");
        }
    }

    public void enterNumberBySwiping(String membershipNumber) {
        if (isValidNumber(membershipNumber)) {
            System.out.println("Membership number entered by swiping: " + membershipNumber);
            // Process the number here.
        } else {
            System.out.println("Error reading card. Please try again or enter the number manually.");
        }
    }

    public void enterNumberByScanning(String membershipNumber) {
        if (isValidNumber(membershipNumber)) {
            System.out.println("Membership number entered by scanning: " + membershipNumber);
            // Process the number here.
        } else {
            System.out.println("Error scanning card. Please try again or enter the number manually.");
        }
    }


    private boolean isValidNumber(String membershipNumber) {
        return membershipNumber != null && !membershipNumber.isEmpty() && membershipNumber.length() == 9; // Num not empty and 9 digits long
    }

    public static void main(String[] args) {
        MembershipNumberEntry entrySystem = new MembershipNumberEntry();

        entrySystem.signalNumberEntry();
        entrySystem.enterNumberByTyping("123456789");
        entrySystem.enterNumberBySwiping("000000002");
        entrySystem.enterNumberByScanning("000000003");
    }
}
