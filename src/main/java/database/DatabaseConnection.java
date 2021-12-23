package database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class DatabaseConnection implements Database {

    // To avoid hardcoded database information
    final private static String projectPath = System.getProperty("user.dir");
    private static Properties properties = new Properties();

    // Variables for database information
    private static String databaseROOT;
    private static String username;
    private static String password;
    private static String carDatabaseName;

    // Database functionality variables
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;
    private static CallableStatement callableStatement;
    private static ResultSet resultSet = null;

    final private static String ECONOMY = CarTypes.ECONOMY.toString();
    final private static String STANDARD = CarTypes.STANDARD.toString();
    final private static String SUV = CarTypes.SUV.toString();
    final private static char EURO = '\u20ac';

    public DatabaseConnection () throws IOException {
        properties.load(new FileInputStream(projectPath + "/database_info"));
        databaseROOT = properties.getProperty("database");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
        carDatabaseName = properties.getProperty("cardb");
        initializeDB();
        createTable();

    }

    /**
     * This function will delete an entire car row by giving the ID of the car
     * @param id The ID of the car you want to delete
     */
    public void deleteCarById(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("Can't delete a car with a negative ID");
        }

        System.out.println("\nBefore the delete...");
        boolean doCarExist = displayCarDetails(id);

        // Delete the car
        if (doCarExist) {
            System.out.println("\nDeleting the car with the ID: " + id + "...");
            try {
                int rowsChanges = statement.executeUpdate("delete from car_test where id='" + id + "'");
                System.out.println("\nAfter the delete...");
                displayCarDetails(id);
            }
            catch (Exception e) {
                System.out.println("Exception occurred when tried to delete the car with ID: " + id);
                e.printStackTrace();
            }
        }
    }


    /**
     * This function will be used to update the price of a car stored in the database.
     * The user must specify the ID of the car they want to update.
     * @param id The user must specify which car he wants to select by it's ID.
     * @param price The user must choose how much the new price will be.
     */
    @Override
    public void updatePriceById(int id, double price) {

        if (id <= 0) {
            throw new IllegalArgumentException("Can't update a car price with a negative ID");
        }
        // Hot fixed the price when performed update
        price = fixPriceRange(price, getTypeOfCarById(id));

        try {
            // Call helper method to display the details of the specified car
            System.out.println("\nWill update the object with the contents: ");
            if(displayCarDetails(id)) {
                // Updating the price of the car
                System.out.println("\nUpdating...");
                int rowsChanges = statement.executeUpdate("update car_test set price='" + price + "' " +
                        "where id='" + id + "'");

                System.out.println("\nUpdated car content for " + rowsChanges + " rows:");
                displayCarDetails(id);
            }
        }
        catch (Exception e) {
            System.out.println();
        }
    }


    /**
     * This function will display all the cars along with their details sorted by their price.
     */
    @Override
    public void getAllCars() {

        Map<String, ArrayList<String>> fullDetailsOfTheCar = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return (int) Math.round(Double.parseDouble(o1) - Double.parseDouble(o2));
            }
        });
        // Used collections to ensure that we have the list ordered by the price
        SortedSet<String> allCarsSortedByPrice = new TreeSet<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return (int) Math.round(Double.parseDouble(o1) - Double.parseDouble(o2));
            }
        });

        try {
            System.out.println("\nDisplaying all cars...\n");
            resultSet = statement.executeQuery("select * from car_test order by price");

            while (resultSet.next()) {
                ArrayList<String> currentDetails = new ArrayList<>();

                System.out.println("\t" + resultSet.getString("brand") + " " +
                        resultSet.getString("model") + " costs: " +
                        resultSet.getInt("price") + EURO + "/day it's " +
                        resultSet.getString("type") + " type and has " +
                        resultSet.getString("color").toLowerCase() + " color.");

                currentDetails.add("ID: " + resultSet.getInt("id"));
                currentDetails.add("Brand: " + resultSet.getString("brand"));
                currentDetails.add("Model: " + resultSet.getString("model"));
                currentDetails.add("Type: " + resultSet.getString("type"));
                currentDetails.add("Color: " + resultSet.getString("color"));

//                System.out.println(currentDetails);
                allCarsSortedByPrice.add(resultSet.getString("price"));
                fullDetailsOfTheCar.put(resultSet.getString("price"), currentDetails);
            }
        }
        catch (Exception e) {
            System.out.println("Exception occurred when tried to read the cars details...");
            e.printStackTrace();
        }

        System.out.println("\nThe list of prices --> " + allCarsSortedByPrice);
        System.out.println("The list of prices and the car details --> " + fullDetailsOfTheCar);
    }

    /**
     * This function will display all the possible types of cars and
     * their corespondent number of cars (e.g., SUV: 3 cars)
     * @return it should return an HashMap with the key being the type
     * of the car and the value the number of times that type occurred
     */
    @Override
    public Map<String, Integer> getAllCarTypes () {
        try {
            Map<String, Integer> typeAndNumOfCar = new HashMap<>();
            System.out.println("\nDisplaying all types of cars and their occurrences...");
            resultSet = statement.executeQuery("select id, type from car_test order by id");
            ArrayList<String> timesOfTypes = new ArrayList<>();
            while (resultSet.next()) {
                // System.out.println(resultSet.getInt("id") + ": " + resultSet.getString("type"));
                timesOfTypes.add(resultSet.getString("type"));
            }


            typeAndNumOfCar.put(ECONOMY, Collections.frequency(timesOfTypes, ECONOMY));
            typeAndNumOfCar.put(STANDARD, Collections.frequency(timesOfTypes, STANDARD));
            typeAndNumOfCar.put(SUV, Collections.frequency(timesOfTypes, SUV));

            return typeAndNumOfCar;
        }
        catch (Exception e) {
            System.out.println("Exception occurred when tried to read the cars types...");
            e.printStackTrace();
        }


        return null;
    }

    /**
     * This function insert a car into the database
     * @param brand The brand producer of the car (e.g., Tesla)
     * @param model Which model the car is (e.g., a Dacia car can have the model "Sandero"
     * @param price How much it costs to rent a card [Euro/day]
     * @param type Can only be one of those: ECONOMY STANDARD, SUV
     * @param color The color of the car (e.g., Green)
     */
    @Override
    public void insertData(String brand, String model, double price, String type, String color) {

        if (brand.length() > 200) {
            throw new IllegalArgumentException("Can't create a car with a the brand longer than 200 characters");
        }
        else if (model.length() > 200) {
            throw new IllegalArgumentException("Can't create a car with a the model longer than 200 characters");
        }
        else if (!type.equals(ECONOMY) && !type.equals(STANDARD) && !type.equals(SUV)) {
            throw new IllegalArgumentException("It must be one of the three choices for the type");
        }
        else if (color.length() > 50) {
            throw new IllegalArgumentException("Can't create a car with a color name longer than 50 characters");
        }

        boolean insertOk = true;
        double verifyPrice = fixPriceRange(price, type);
        try {
            System.out.println("\nInserting a new car into the database...");
            String insertTableCommand = "insert into car_test (brand, model, price, type, color) " +
                    "values ('" + brand + "', '" + model + "', '" + verifyPrice + "', '" + type + "', '" + color + "')";
            preparedStatement = connection.prepareStatement(insertTableCommand);
            preparedStatement.executeUpdate();
        }
        catch (Exception e) {
            insertOk = false;
            System.out.println("Exception occurred when inserted the new car:");
            e.printStackTrace();
        }
        if (insertOk) {
            System.out.println("New " + brand + " car added.");
        }
    }


    /**
     * This method will create a table in the database with the given restrictions for the attributes
     */
    private void createTable() {
        try {
            String createTableCommand = "create table if not exists car_test" +
                    "(id BIGINT NOT NULL AUTO_INCREMENT," +
                    "brand varchar(200)," +
                    "model varchar(200)," +
                    "price double unsigned CHECK (price > 0)," +
                    "type varchar(10)," +
                    "CONSTRAINT chk_val CHECK (type IN ('" + ECONOMY + "', '" + STANDARD + "', '" + SUV + "'))," +
                    "color varchar(50)," +
                    "PRIMARY KEY(id))";
            preparedStatement = connection.prepareStatement(createTableCommand);
            preparedStatement.executeUpdate();
            System.out.println("Successfully created the car table");
        }
        catch (Exception e) {
            System.out.println("Can't create the table, exception occurred...");
            e.printStackTrace();
        }

    }


    /**
     * This method is used to instantiate all the needed functionality for the database
     */
    private void initializeDB() {

        try {
            // Instantiate connection to the database
            connection = DriverManager.getConnection(databaseROOT + carDatabaseName, username, password);
            System.out.println("Successfully connected to the database: " + carDatabaseName + "!\n");

            // Instantiate the statement
            statement = connection.createStatement();
        }
        catch (Exception e) {
            System.out.println("Can't initialize the database, exception occurred...");
            e.printStackTrace();
        }
    }


    /**
     * Helper function to display all the details of a car by a given ID
     * @param id ID of the requested car
     */
    public boolean displayCarDetails(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("Can't access a car details with a negative ID");
        }

        StringBuilder checkExistenceOfId = new StringBuilder();

        try {
            resultSet = statement.executeQuery("select * from car_test order by id");
            while (resultSet.next()) {
                if (id == resultSet.getInt("id")) {
                    System.out.println("id:" + resultSet.getInt("id") + " | " +
                            "brand:" + resultSet.getString("brand") + " | " +
                            "model:" + resultSet.getString("model") + " | " +
                            "price:" + resultSet.getDouble("price") + " | " +
                            "type:" + resultSet.getString("type") + " | " +
                            "color:" + resultSet.getString("color"));
                    checkExistenceOfId.append(resultSet.getInt("id"));
                }
            }
            if (checkExistenceOfId.isEmpty()) {
                System.out.println("There is no car by by ID:" + id);
                return false;
            }
            return true;
        }
        catch (Exception e) {
            System.out.println("Exception occurred when tried to display the car details with the id: " + id);
            e.printStackTrace();
        }

        return true;
    }


    /**
     * Helper function to get the type of car in order to fix the price to a lower of higher bound.
     * @param id The ID of the car is needed in order to perform this action
     * @return Should return the type of car from the database with three possible outcomes: ECONOMY, STANDARD or SUV
     */
    private String getTypeOfCarById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Can't access car types which have a negative ID");
        }

        String typeOfCar = "";
        try {
            resultSet = statement.executeQuery("select * from car_test order by id");
            while (resultSet.next()) {
                if (id == resultSet.getInt("id")) {
                    typeOfCar = resultSet.getString("type");
                }
            }
            if (typeOfCar.isEmpty()) {
                return null;
            }
        }
        catch (Exception e) {
            System.out.println("Can't execute query for the car with ID: " + id);
            e.printStackTrace();
        }

        return typeOfCar;
    }

    /**
     * Helper function to fix the price range, set the price to the higher or lower bound
     * @param price The price per day to rent a car.
     * @param type The type of the car in the database (ECONOMY, STANDARD and SUV)
     * @return If the old price is in the given range should return it else return the new price
     */
    private double fixPriceRange(double price, String type) {
        if (price <= 0) {
            throw new IllegalArgumentException("Can't create a car with a negative rent fee");
        }
        double newPrice = 1.00;
        if (type.equals(ECONOMY)) {
            if (price > 18) {
                newPrice = 18;
            }
            else {
                newPrice = price;
            }
        }
        else if (type.equals(STANDARD)) {
            if (price > 30) {
                newPrice = 30;
            }
            else {
                newPrice = price;
            }
        }
        else if (type.equals(SUV)) {
            if (price < 33) {
                newPrice = 33;
            }
            else  {
                newPrice = price;
            }
        }
        else {
            newPrice = price;
        }


        return newPrice;
    }
}
