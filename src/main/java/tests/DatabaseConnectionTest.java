package tests;

import database.CarTypes;
import database.DatabaseConnection;
import mainapp.Main;
import org.junit.jupiter.api.*;

import javax.sound.midi.Soundbank;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionTest {

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
    public void setup() throws IOException {
        properties.load(new FileInputStream(projectPath + "/database_info"));
        databaseROOT = properties.getProperty("database");
        username = properties.getProperty("username");
        password = properties.getProperty("password");
        carDatabaseName = properties.getProperty("cardb");

//        databaseConnection = new DatabaseConnection();

    }

    @Test
    public void shouldConnectToDatabaseAndQuery() throws SQLException {

        connection = DriverManager.getConnection(databaseROOT + carDatabaseName, username, password);
        statement = connection.createStatement();
        resultSet = statement.executeQuery("select * from car_test");

        assertNotNull(connection);
        assertNotNull(statement);
        assertNotNull(resultSet);
    }

    @Test
    public void shouldInitializeDBandCreateTable() throws SQLException, IOException {
        databaseConnection = new DatabaseConnection();
        databaseConnection.insertData("Test1", "NewTest1", 15, ECONOMY, "Blue");
        databaseConnection.insertData("Test2", "NewTest2", 15, STANDARD, "Blue");
        databaseConnection.insertData("Test3", "NewTest3", 15, SUV, "Blue");

        // Check if there are some cars into the database
        Assertions.assertFalse(databaseConnection.getAllCarTypes().isEmpty());

        // If the number of elements inside the HashMap are exactly 3 then pass the test
        Assertions.assertEquals(3, databaseConnection.getAllCarTypes().size());

        // Check if the key set it's the same as it should be
        System.out.println(databaseConnection.getAllCarTypes().keySet());
        Assertions.assertEquals(SUV, databaseConnection.getAllCarTypes().keySet().toArray()[0]);
        Assertions.assertEquals(ECONOMY, databaseConnection.getAllCarTypes().keySet().toArray()[1]);
        Assertions.assertEquals(STANDARD, databaseConnection.getAllCarTypes().keySet().toArray()[2]);

        // Process to remove the created Cars
        connection = DriverManager.getConnection(databaseROOT + carDatabaseName, username, password);
        statement = connection.createStatement();

        assertNotNull(connection);
        assertNotNull(statement);


        try {
            System.out.println("\nDeleting the created tests...");
            int rowsChanges = statement.executeUpdate("delete from car_test where brand='Test1' or brand='Test2' or brand='Test3'");
        }
        catch (Exception e) {
            System.out.println("Exception occurred when tried to delete the created Tests");
            e.printStackTrace();
        }
    }


    // Check if all the occurrences of the ECONOMY type have the values under 18 euro/day
    @Test
    public void shouldHavePriceLessThan18ForEconomy() throws SQLException {

        connection = DriverManager.getConnection(databaseROOT + carDatabaseName, username, password);
        statement = connection.createStatement();
        resultSet = statement.executeQuery("select * from car_test order by id");

        assertNotNull(connection);
        assertNotNull(statement);
        assertNotNull(resultSet);

        while (resultSet.next()) {
            if (resultSet.getString("type").equals(ECONOMY)) {
                Assertions.assertTrue(19 > resultSet.getInt("price"));
            }
        }
    }

    // Check if all the occurrences of the STANDARD type have the values under 30 euro/day
    @Test
    public void shouldHavePriceLessThan30ForStandard() throws SQLException {

        connection = DriverManager.getConnection(databaseROOT + carDatabaseName, username, password);
        statement = connection.createStatement();
        resultSet = statement.executeQuery("select * from car_test order by id");

        assertNotNull(connection);
        assertNotNull(statement);
        assertNotNull(resultSet);

        while (resultSet.next()) {
            if (resultSet.getString("type").equals(STANDARD)) {
                Assertions.assertTrue(31 > resultSet.getInt("price"));
            }
        }
    }

    // Check if all the occurrences of the SUV type have the values over 33 euro/day
    @Test
    public void shouldHavePriceMoreThan33ForSUV() throws SQLException {

        connection = DriverManager.getConnection(databaseROOT + carDatabaseName, username, password);
        statement = connection.createStatement();
        resultSet = statement.executeQuery("select * from car_test order by id");

        assertNotNull(connection);
        assertNotNull(statement);
        assertNotNull(resultSet);

        while (resultSet.next()) {
            if (resultSet.getString("type").equals(SUV)) {
                Assertions.assertTrue(32 < resultSet.getInt("price"));
            }
        }
    }


    @AfterEach
    public void executeAfterEachTest() {
        System.out.println("Executed after each test.");
    }


}