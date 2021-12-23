package mainapp;

import database.CarTypes;
import database.DatabaseConnection;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

public class Main {

    final private static String ECONOMY = CarTypes.ECONOMY.toString();
    final private static String STANDARD = CarTypes.STANDARD.toString();
    final private static String SUV = CarTypes.SUV.toString();
    private static boolean quit = false;
    private static DatabaseConnection databaseConnection;
    final private static char EURO = '\u20ac';


    public static void main(String[] args) throws IOException {

        databaseConnection = new DatabaseConnection();
//        populateDatabase(databaseConnection);

        System.out.println("Welcome to the Rent-a-Car! You are the admin.");
        Scanner scanner = new Scanner(System.in);

        while (!quit) {

            System.out.println("\nWhat do you wish to do?");
            displayOptions();
            try {
                int userOption = Integer.parseInt(scanner.nextLine());
                processOption(userOption);
            }
            catch (Exception e) {
                System.out.println("You chose an invalid option " + e.getMessage());
                System.out.println("Please choose a number.");
            }

        }

    }


    /**
     * A method to display the options available for the user in the main menu.
     */
    private static void displayOptions() {
        System.out.println("\n");
        System.out.println("\t1. Add a car.");
        System.out.println("\t2. Update the price for a car.");
        System.out.println("\t3. Show all the cars with their details, the cars are sorted by their rent price.");
        System.out.println("\t4. Show the type and number of the cars available.");
        System.out.println("\t5. Delete a car.");
        System.out.println("\t6. Exit.");
        System.out.println("\n");
    }

    /**
     * A method to display the options available for the type of the car.
     */
    private static void displayTypeOptions() {
        System.out.println("\n");
        System.out.println("\t1. Economy.");
        System.out.println("\t2. Standard.");
        System.out.println("\t3. SUV.");
        System.out.println("\t4. Go back.");
    }

    /**
     * A helper method to process the option choose by the user
     * @param value The number that the user chosen.
     */
    private static void processOption(int value) {
        switch (value) {
            case 1:
                addCar(databaseConnection);
                break;
            case 2:
                editPriceOfCar(databaseConnection);
                break;
            case 3:
                showAllCarsSortedByRentPrice(databaseConnection);
                break;
            case 4:
                showTypeAndNumberOfCars(databaseConnection);
                break;
            case 5:
                deleteCar(databaseConnection);
                break;
            case 6:
                System.out.println("Have a nice day!");
                quit = true;
                break;
            default:
                System.out.println("Invalid option...");
                displayOptions();
                break;
        }
    }

    /**
     * A helper function to process the options for the type of the car
     * @param value The value that the user chosen.
     * @return It should return the type of the car, only 3 possible values - Economy, Standard and SUV
     */
    private static String processTypeOptions(int value) {
        String typeOfCar = "";
        switch (value) {
            case 1:
                typeOfCar = ECONOMY;
                break;
            case 2:
                typeOfCar = STANDARD;
                break;
            case 3:
                typeOfCar = SUV;
                break;
            case 4:
                typeOfCar = "Special";
                break;
            default:
                System.out.println("Invalid option...");
                break;
        }

        return typeOfCar;
    }

    /**
     * A method to add the user input into the database as a car row.
     * @param dbConnection This is the instance of the database.
     */
    private static void addCar(DatabaseConnection dbConnection) {
        Scanner addCarScanner = new Scanner(System.in);
        Scanner priceScanner = new Scanner(System.in);
        String brand, model, type, color;
        String chooseType = "";
        double price;

        System.out.print("Add a brand: ");
        brand = addCarScanner.nextLine();

        System.out.print("Add a model: ");
        model = addCarScanner.nextLine();

        System.out.print("Add a price: ");
        price = priceScanner.nextDouble();


        while (chooseType.isEmpty()) {
            System.out.print("Pick a type: ");
            displayTypeOptions();
            type = addCarScanner.nextLine();
            chooseType = processTypeOptions(Integer.parseInt(type));
            if (chooseType.equals("Special")) {
                break;
            }
        }

        if (!chooseType.equals("Special")) {
            System.out.print("Pick a color: ");
            color = addCarScanner.nextLine();

            dbConnection.insertData(brand, model, price, chooseType, color);
        }

    }

    /**
     * A method to edit a car's price by it's ID1
     * @param dbConnection This is the instance of the database.
     */
    private static void editPriceOfCar(DatabaseConnection dbConnection) {
        Scanner editScanner = new Scanner(System.in);

        int id;
        double price;
        String answerr;

        System.out.print("Select a car ID in order to change it's price: ");
        id = editScanner.nextInt();
        System.out.println("Selected the car with ID " + id);
        dbConnection.displayCarDetails(id);
        System.out.print("Now select a new price for renting: ");
        price = editScanner.nextDouble();
        System.out.println("Proceeding to update the price to " + price + EURO + "...");
        Scanner yesOrNo = new Scanner(System.in);
        System.out.println("[Y/N] Are you sure you want to update the price to the car at the value of " + price + EURO);
        answerr = yesOrNo.nextLine().toUpperCase();

        if (answerr.equals("Y")) {
            dbConnection.updatePriceById(id, price);
        }


    }


    /**
     * A method to show all the cars available. They are sorted by their price per day.
     * @param dbConnection  This is the instance of the database.
     */
    private static void showAllCarsSortedByRentPrice(DatabaseConnection dbConnection) {
        dbConnection.getAllCars();
    }

    /**
     * A method to show all the cars types and their total numbers
     * @param dbConnection This is the instance of the database.
     */
    private static void showTypeAndNumberOfCars(DatabaseConnection dbConnection) {
        Map<String, Integer> typeAndNumOfCar = dbConnection.getAllCarTypes();
        System.out.println(typeAndNumOfCar);
        for (String key : typeAndNumOfCar.keySet()) {
            System.out.println("\t" + key + ": " +typeAndNumOfCar.get(key) + " cars");
        }
    }

    /**
     * A method to delete an entire row by giving as input only the ID of the car we want to delete.
     * @param dbConnection This is the instance of the database.
     */
    private static void deleteCar(DatabaseConnection dbConnection) {
        Scanner deleteScanner = new Scanner(System.in);
        int id;
        String yesOrNo = "";
        System.out.println("Select a car ID in order to delete the row:");
        id = deleteScanner.nextInt();
        System.out.println("Selected the car with ID " + id);

        Scanner areYouSure = new Scanner(System.in);
        System.out.println("[Y/N] Are you sure you want to delete the car: ");
        dbConnection.displayCarDetails(id);
        yesOrNo = areYouSure.nextLine().toUpperCase();

        if (yesOrNo.equals("Y")) {
            dbConnection.deleteCarById(id);
        }

    }

    /**
     * A quick way to populate the database with nice data so the user can interact with it.
     * @param dbConnection This is the instance of the database.
     */
    public static void populateDatabase(DatabaseConnection dbConnection) {
        // Mercedes-Benz brand
        dbConnection.insertData("Mercedes-Benz", "Sedan", 20, STANDARD, "Khaki");
        dbConnection.insertData("Mercedes-Benz", "GLK", 20, SUV, "Light Blue");
        dbConnection.insertData("Mercedes-Benz", "Roadster", 70, STANDARD, "Red");
        dbConnection.insertData("Mercedes-Benz", "Coupes", 40, ECONOMY, "Khaki");
        dbConnection.insertData("Mercedes-Benz", "Wagon", -5, STANDARD, "Gray");

        // Hyundai brand
        dbConnection.insertData("Hyundai", "Ioniq 5", 14.4, ECONOMY, "Light Gray");
        dbConnection.insertData("Hyundai", "Azera", 27.4, STANDARD, "Cool Dark Blue");
        dbConnection.insertData("Hyundai", "Sonata", -5, SUV, "Yellowish");
        dbConnection.insertData("Hyundai", "Akcent", 30, SUV, "White");
        dbConnection.insertData("Hyundai", "Veloster", -5, ECONOMY, "Red");

        // Porsche brand
        dbConnection.insertData("Porsche", "718 Cayman", 43.2, ECONOMY, "Black");
        dbConnection.insertData("Porsche", "718 Boxster T", 16.4, ECONOMY, "Red");
        dbConnection.insertData("Porsche", "718 Boxster GTS 4.0", 500.3, SUV, "Green");
        dbConnection.insertData("Porsche", "911 Carrera 4S", 24.99, STANDARD, "Awesome Violet");
        dbConnection.insertData("Porsche", "Taycan", 14.99, STANDARD, "Pink");

        // Ferrari brand
        dbConnection.insertData("Ferrari", "812 Superfast", 10.2, ECONOMY, "Crimson");
        dbConnection.insertData("Ferrari", "SF90 Spider", 17.99, ECONOMY, "Yellow");
        dbConnection.insertData("Ferrari", "SF90 Stradale", 99.99, SUV, "Cyan");
        dbConnection.insertData("Ferrari", "Portofino M", 42.1, STANDARD, "Gray");
        dbConnection.insertData("Ferrari", "F8 Tributo", 29.45, STANDARD, "Red");

    }
}
