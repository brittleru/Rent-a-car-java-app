## Rent a car App

### Pre-requisites

* Java 17
* Maven
* an SQL database of your choice (MySQL, Oracle, PostgreSQL etc)
* an IDE of your choice (IntellijIDEA, Eclipse etc)

### Requirements

You must implement the back end system of an application used for renting cars. The application will store the data in an SQL database of your choice.

The application needs to provide implementation for the following use cases:

1. Add a car – allows a user to add a new car in the database. The following business rules are defined for this use case:
* The cars are stored in a table in the database
* If the car type is ECONOMY, the price cannot be more than 18 EUR / day.
* If the car type is STANDARD, the price cannot be more than 30 EUR / day.
* If the car type is SUV, the price cannot be less than 33 EUR / day.
* The following table contains the list of attributes of an item, along with the validations that need to be implemented for each attribute:

 Attribute name | Validation
----------- | -----------
 id         | integer positive value, primary key, generated automatically in the database
 brand      | text, with maximum length of 200 characters
 model      | text, with maximum length of 200 characters
 price      | real positive value
 type       | can have only one of these values: ECONOMY, STANDARD, SUV
 color      | text, with maximum length of 50 characters

2. Update the price of a car - allows a user to update the price of a car stored in the database. The user must specify the id of the car they want to update. 

3. Retrieve the list of all cars - allows the user to retrieve the list of all cars details from the database. The cars should be sorted by price.

4. Retrieve the list of all car types and the number of cars for each type from the database. 

Unit tests should be implemented, with a coverage of at least 70% per line.

### Notes
The application may be implemented as any of these options:
* a console application, using only Java SE
* a REST API exposing endpoints for each use case, using Spring / Spring Boot


