
# Self-Checkout System

This project simulates the hardware and software of a supermarket self‑checkout station.
It contains three Eclipse projects:

- **com.thelocalmarketplace.hardware_0.3.3** – a hardware simulation library
  with devices such as scanners, scales and payment peripherals.
- **com.thelocalmarketplace.software** – the application code providing
  an attendant screen and customer touch‑screens.
- **com.thelocalmarketplace.software.test** – JUnit 4 test suite for the software.

## Features

- **Multiple station types**  
  `SelfCheckoutStationFactory` builds Bronze, Silver or Gold stations
  with configurable currency and denominations.
- **User Interfaces**  
  `AttendantView` shows a login panel and dashboard for up to three customer
  stations, each running a `CustomerView`. Customer panels include
  item scanning, bagging, payment options and more.​
- **Product management**  
  `Main` seeds a small product database of barcoded and PLU items and then
  starts the attendant interface.​
- **Payment modules**  
  Support for coins, banknotes, credit and debit cards.
  The debit implementation handles swipes, inserts, taps and authorization via a
  `CardIssuer`.​
- **Maintenance utilities**  
  `RefillLogic` refills banknote/coin dispensers and printer ink or paper when
  needed.


## How to Run

1. Compile the hardware library:

   ```bash
   javac -d bin $(find com.thelocalmarketplace.hardware_0.3.3 -name '*.java')
 
2. Compile the software project (make sure the hardware `bin` directory is on the classpath):
   ```bash

   javac -cp bin -d com.thelocalmarketplace.software/bin \
    $(find com.thelocalmarketplace.software/src -name '*.java')

3. Start the application by running the `Main` class:


	```bash
	 java -cp com.thelocalmarketplace.software/bin:bin \
	 com.thelocalmarketplace.software.Main