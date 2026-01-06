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
                System.out.println(
                        "ID: " + car.getId()
                      + " | Brand: " + car.getBrand()
                      + " | Type: " + carType
                      + " | Price/Day: $" + car.getPricePerDay()
                );

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

    // ============== RENTAL MANAGEMENT METHODS ==============

    // Rent a car
    public Rental rentCar(String carId, Customer customer, int days) {

        Car car = findCarById(carId);

        // Validation: car exists
        if (car == null) {
            System.out.println("ERROR: Car not found with ID: " + carId);
            return null;
        }

        // Validation: car is available
        if (!car.isAvailable()) {
            System.out.println("ERROR: Car is not available (already rented).");
            return null;
        }

        // Validation: days is positive
        if (days <= 0) {
            System.out.println("ERROR: Rental days must be positive.");
            return null;
        }

        // Create rental
        String rentalId = "R" + (rentals.size() + 1);
        Rental rental = new Rental(rentalId, car, customer, days);

        // Mark car as unavailable
        car.setAvailable(false);

        // Add to rentals list
        rentals.add(rental);

        // Success message
        System.out.println("\n===== RENTAL SUCCESSFUL =====");
        System.out.println("Rental ID: " + rental.getRentalId());
        System.out.println("Customer: " + customer.getName());
        System.out.println("Car: " + car.getBrand() + " (" + car.getId() + ")");
        System.out.println("Days: " + days);
        System.out.println("Total Fee: $" + rental.getTotalFee());
        System.out.println("==============================\n");

        return rental;
    }

    // Return a car
    public void returnCar(String rentalId) {

        Rental rental = null;
        for (Rental r : rentals) {
            if (r.getRentalId().equals(rentalId)) {
                rental = r;
                break;
            }
        }

        // Validation: rental exists
        if (rental == null) {
            System.out.println("ERROR: Rental not found with ID: " + rentalId);
            return;
        }

        // Validation: not already returned
        if (rental.isReturned()) {
            System.out.println("ERROR: This rental has already been returned.");
            return;
        }

        // Mark rental as returned
        rental.setReturned(true);

        // Mark car as available again
        rental.getCar().setAvailable(true);

        // Success message
        System.out.println("\n===== CAR RETURNED =====");
        System.out.println("Rental ID: " + rental.getRentalId());
        System.out.println("Car: " + rental.getCar().getBrand() + " (" + rental.getCar().getId() + ")");
        System.out.println("Customer: " + rental.getCustomer().getName());
        System.out.println("Total Fee: $" + rental.getTotalFee());
        System.out.println("========================\n");
    }

    // ============== SEARCH & FILTER METHODS ==============

    // Search cars by brand
    public List<Car> searchByBrand(String brand) {
        List<Car> results = new ArrayList<>();
        for (Car car : cars.values()) {
            if (car.getBrand().toLowerCase().contains(brand.toLowerCase()) && car.isAvailable()) {
                results.add(car);
            }
        }
        return results;
    }

    // Search gas cars by fuel type
    public List<Car> searchByFuelType(String fuelType) {
        List<Car> results = new ArrayList<>();
        for (Car car : cars.values()) {
            if (car instanceof GasCar && car.isAvailable()) {
                GasCar gasCar = (GasCar) car;
                if (gasCar.getFuelType().equalsIgnoreCase(fuelType)) {
                    results.add(car);
                }
            }
        }
        return results;
    }

    // ============== GETTERS (FOR CSV EXPORT) ==============

    // Get all rentals
    public List<Rental> getAllRentals() {
        return new ArrayList<>(rentals);
    }

    // ✅ THIS IS THE ONLY NEW METHOD NEEDED FOR CSV
    public Collection<Car> getAllCars() {
        return cars.values();
    }
}
