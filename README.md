## Solution

### Technologies used

* Java 17
* Maven
* MySQL
* IntellijIDEA

### Tasks

At first, for this CRUD application I've created an Interface named Database with the most important methods such as: <code>insertData();</code>, it will be responsable for the creation of attributes in the database (Task 1), then <code>updatePriceById();</code> which will update the price of a car stored in the database by specifying the ID of the car (Task 2), after that the <code>getAllCars();</code> method responsible for visualizing the data, where the cars are sorted by their rent price (Task 3), and finally the <code>getAllCarTypes();</code> method which we will use to visualize how many cars are in the database for each type of car (Task 4). After this I've created a class named <code>DatabaseConnection</code> in order to implement those methods. At first, I've downloaded the dependencies for the database but then I've configured the Maven file and it was better. 

#### Task 1
Here I've wrote a file <code>database_info</code> with the credentials for the database in order to avoid hardcoding them in the java files, after that I've used system property to avoid hardcoding the path. After that I've instantiated into the constructor the database information in order to call two methods into it <code>initializeDB()</code> a method to instantiate the connection and the statement and <code>createTable()</code> method in which I've created the functionality for every attribute and used a PreparedStatement to execute the creation of the table. </br></br>
After that I've implemented the method <code>insertData()</code>. In order to respect the ranges of prices fees for every type of car I've created a helper method named <code>fixPriceRange()</code> and in order to get the type of the car I've created another helper function <code>getTypeOfCarById()</code>.


#### Task 2
During this task I've created a method <code>updatePriceById()</code> to do the deed. Here I've also used the helper function <code>fixPriceRange()</code> in case the user change the price to one that doesn't respect the upper and lower boundaries for the type of car. In order to let the user visualize what he tries to change and how the changes affected the table I've created a method <code>displayCarDetails()</code> which was a way to visualize an entire row given the car ID in case that there is no car by that ID then the program will state that.


#### Task 3
Here I've overrided the method <code>getAllCars()</code> where I've used a <code>TreeMap<String, ArrayList<String>></code> to have a list of sorted prices and their data, a <code>TreeSet<String></code> for the same reason but to have only the values of the prices and to be sure I've in the Query I've ordered by price and wrote a visualization print of the cars and their details.


#### Task 4
At this task I've implemented the <code>getAllCarTypes()</code> method with a <code>HashMap<String, Integer></code> return type and used the <code>Collections.frequency()</code> method to put the number of times a certain type occurred.


#### Bonus task (Delete Query)
To finish the CRUD functionality I've also implemented a method called <code>deleteCarById</code> which works as the update method but this one deletes the row with a given ID.


#### Unit tests
In order to cover as much of the code, I wrote two classes to do the tests <code>DatabaseConnectionTest</code> and <code>WrongArgumentsTest</code>.</br>
For the first one I've tested if:
- One can connect to the database and do a simple query
- One can initialize database and if it can insert rows into the table (after creation they were deleted)
- All the occurrences of the ECONOMY type respected the upper boundary (18 euro/day)
- All the occurrences of the STANDARD type respected the upper boundary (30 euro/day)
- All the occurrences of the SUV type respected the lower boundary (33 euro/day)

And for the second one I've tested mostly the IllegalArgumentException for example if someone pass:
- A negative parameter for the price in the insert method
- A brand parameter with more than 200 characters in the insert method
- A model parameter with more than 200 characters in the insert method
- A type parameter which is not one of the following: ECONOMY, STANDARD, SUV in the insert method
- A color parameter with more than 50 characters in the insert method
- A negative parameter for the price in the update method
- A negative ID for the update method
- A negative ID for the delete method

Both of those classes instantiated their needs before each test by using the decorator @BeforeEach.

### Main application
For the main application I've developed a console app, in which I've generated some cars (20) and then, it was a menu of options and the user could interact with those in order to administrate the Rent a Car service. 


### Results Images

#### The database
![database](https://github.com/brittleru/Rent-a-car-java-app/blob/main/imgs/database.png?raw=true)

#### The terminal results
![task1](https://github.com/brittleru/Rent-a-car-java-app/blob/main/imgs/start.png?raw=true)
![task2](https://github.com/brittleru/Rent-a-car-java-app/blob/main/imgs/start2.png?raw=true)
![task3](https://github.com/brittleru/Rent-a-car-java-app/blob/main/imgs/start3.png?raw=true)
![task4](https://github.com/brittleru/Rent-a-car-java-app/blob/main/imgs/start4.png?raw=true)
![bonus task + exit screen](https://github.com/brittleru/Rent-a-car-java-app/blob/main/imgs/start5.png?raw=true)



