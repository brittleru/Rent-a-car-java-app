package tests;

import database.CarTypes;
import database.DatabaseConnection;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class WrongArgumentsTest {

    final private static String projectPath = System.getProperty("user.dir");
    private static Properties properties = new Properties();

    private static String databaseROOT;
    private static String username;
    private static String password;
    private static String carDatabaseName;

    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet = null;

    final private static String ECONOMY = CarTypes.ECONOMY.toString();
    final private static String STANDARD = CarTypes.STANDARD.toString();
    final private static String SUV = CarTypes.SUV.toString();

    private static DatabaseConnection databaseConnection;

    final private static String LOREM_TEST = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. " +
            "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a" +
            " galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but " +
            "also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s" +
            " with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing " +
            "software like Aldus PageMaker including versions of Lorem Ipsum.";


    @BeforeEach
    public void setup() throws IOException, SQLException {
        properties.load(new FileInputStream(projectPath + "/database_info"));
        databaseROOT = properties.getProperty("database");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
        carDatabaseName = properties.getProperty("cardb");


        connection = DriverManager.getConnection(databaseROOT + carDatabaseName, username, password);
        statement = connection.createStatement();
    }


    // If you pass a negative parameter, it will throw an IllegalArgumentException
    @Test(expected=IllegalArgumentException.class)
    public void testInsertCreateCarWithNegative() throws IOException {
        databaseConnection = new DatabaseConnection();
        databaseConnection.insertData("Test1", "NewTest1", -45, ECONOMY, "Blue");
    }


    // If you pass a the brand parameter with a string which has more than 200 characters,
    // It will throw an IllegalArgumentException
    @Test(expected=IllegalArgumentException.class)
    public void testInsertLongTextOnBrand() throws IOException {
        databaseConnection = new DatabaseConnection();
        databaseConnection.insertData(LOREM_TEST, "NewTest1", 45, ECONOMY, "Blue");
    }


    // If you pass a the model parameter with a string which has more than 200 characters,
    // It will throw an IllegalArgumentException
    @Test(expected=IllegalArgumentException.class)
    public void testInsertLongTextOnModel() throws IOException {
        databaseConnection = new DatabaseConnection();
        databaseConnection.insertData("Test1", LOREM_TEST, 45, ECONOMY, "Blue");
    }


    // If you pass a the type parameter with a string which is not one of the three values: ECONOMY, STANDARD, SUV,
    // It will throw an IllegalArgumentException
    @Test(expected=IllegalArgumentException.class)
    public void testInsertCheckOneOfThreeOptionsForType() throws IOException {
        databaseConnection = new DatabaseConnection();
        databaseConnection.insertData("Test1", "NewTest1", 45, "Meow", "Blue");
    }


    // If you pass a the color parameter with a string which has more than 200 characters,
    // It will throw an IllegalArgumentException
    @Test(expected=IllegalArgumentException.class)
    public void testInsertLongTextOnColor() throws IOException {
        databaseConnection = new DatabaseConnection();
        databaseConnection.insertData("Test1", "NewTest1", 45, ECONOMY, LOREM_TEST);
    }


    // If you pass a an negative parameter to the update price method,
    // It will throw an IllegalArgumentException
    @Test(expected=IllegalArgumentException.class)
    public void testUpdatePriceWithNegative() throws IOException {
        databaseConnection = new DatabaseConnection();
        databaseConnection.insertData("Test1", "NewTest1", 45, ECONOMY, "Red");
        databaseConnection.updatePriceById(1, -45);
    }

    // If you pass a an negative parameter to the update price method,
    // It will throw an IllegalArgumentException
    @Test(expected=IllegalArgumentException.class)
    public void testUpdateId0() throws IOException {
        databaseConnection = new DatabaseConnection();
        databaseConnection.updatePriceById(-4, 45);
    }


    // If you pass a an negative parameter to the delete ID method,
    // It will throw an IllegalArgumentException
    @Test(expected=IllegalArgumentException.class)
    public void testDeleteId0() throws IOException {
        databaseConnection = new DatabaseConnection();
        databaseConnection.deleteCarById(-4);
    }

}
