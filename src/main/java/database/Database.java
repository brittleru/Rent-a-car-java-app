package database;

import java.util.Map;

public interface Database {
    void insertData(String brand, String model, double price, String type, String color);
    void updatePriceById(int id, double price);
    void getAllCars();
    Map<String, Integer> getAllCarTypes();
}
