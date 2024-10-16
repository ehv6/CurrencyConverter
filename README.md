# Currency Converter

A simple Java application that allows users to convert amounts between different currencies using real-time exchange rates from the Frankfurter API.

## Features
- User-friendly graphical interface for currency conversion
- Dynamic population of currency options from the API
- Conversion based on real-time exchange rates
- Displays conversion results clearly

## Requirements
- Java Development Kit (JDK) 8 or later
- Dependencies managed by Maven

## Installation
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd CurrencyConverter
2. Build the project: If you're using Maven, you can build the project by running:
   mvn clean install
3. Run the application: You can run the application using:
mvn exec:java -Dexec.mainClass="com.CurrencyConverterUI"

**Usage**
Enter the currency you want to convert from and to in the provided text fields.
Input the amount you wish to convert.
Click the "Convert" button to see the result.

**API Used**
Frankfurter API: Provides currency exchange rates and conversion functionalities.

**Contributing**
Contributions are welcome! Please feel free to submit a pull request or open an issue for any suggestions or improvements.

**License**
This project is licensed under the MIT License - see the LICENSE file for details.

**Acknowledgements**
Thanks to the developers of the Frankfurter API for providing a simple and reliable service for currency conversion.

