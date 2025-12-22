package service;

import model.*;
import java.util.*;

public class CarInventory {
    // Data structures to manage cars and rentals
    private Map<String, Car> cars;           // Key = carId, Value = Car object
    private List<Rental> rentals;            // List of all rentals
    
    // Constructor
    public CarInventory() {
        this.cars = new HashMap<>();
        this.rentals = new ArrayList<>();
    }
    
    // ============== CAR MANAGEMENT METHODS ==============
    
    // Add a car to inventory
    public void addCar(Car car) {
        cars.put(car.getId(), car);
        System.out.println("Car added: " + car.getBrand() + " (ID: " + car.getId() + ")");
    }
    
    // Remove a car from inventory
    public void removeCar(String carId) {
        Car removedCar = cars.remove(carId);
        if (removedCar != null) {
            System.out.println("Car removed: " + removedCar.getBrand() + " (ID: " + carId + ")");
        } else {
            System.out.println("Car not found with ID: " + carId);
        }
    }
    
    // Find a car by ID
    public Car findCarById(String carId) {
        return cars.get(carId);
    }
    
    // Display all available cars
    public void displayAvailableCars() {
        System.out.println("\n========== AVAILABLE CARS ==========");
        boolean hasAvailable = false;
        
        for (Car car : cars.values()) {
            if (car.isAvailable()) {
                hasAvailable = true;
                String carType = (car instanceof ElectricCar) ? "Electric" : "Gas";
                System.out.println("ID: " + car.getId() + 
                                 " | Brand: " + car.getBrand() + 
                                 " | Type: " + carType + 
                                 " | Price/Day: $" + car.getPricePerDay());
                
                // Display specific info based on car type
                if (car instanceof ElectricCar) {
                    ElectricCar eCar = (ElectricCar) car;
                    System.out.println("   Battery: " + eCar.getBatteryCapacity() + " kWh");
                } else if (car instanceof GasCar) {
                    GasCar gCar = (GasCar) car;
                    System.out.println("   Fuel Type: " + gCar.getFuelType());
                }
                System.out.println();
            }
        }
        
        if (!hasAvailable) {
            System.out.println("No cars currently available.");
        }
        System.out.println("=====================================\n");
    }
}