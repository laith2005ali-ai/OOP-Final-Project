package app;

import model.*;
import service.CarInventory;
import service.CSVExporter;
import java.util.Scanner;
import java.util.List;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static CarInventory inventory = new CarInventory();

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   CAR RENTAL SYSTEM - INTERACTIVE");
        System.out.println("========================================\n");

        // Initialize with some default cars (optional)
        initializeDefaultCars();

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addCar();
                    break;
                case 2:
                    displayAllCars();
                    break;
                case 3:
                    rentCar();
                    break;
                case 4:
                    returnCar();
                    break;
                case 5:
                    searchCars();
                    break;
                case 6:
                    viewRentalSummary();
                    break;
                case 7:
                    exportToCSV();
                    break;
                case 0:
                    running = false;
                    System.out.println("\n✓ Thank you for using Car Rental System!");
                    break;
                default:
                    System.out.println("\n✗ Invalid choice. Please try again.\n");
            }
        }

        scanner.close();
    }

    // ============== MENU ==============
    private static void displayMenu() {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║         MAIN MENU                  ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 1. Add New Car                     ║");
        System.out.println("║ 2. Display All Available Cars      ║");
        System.out.println("║ 3. Rent a Car                      ║");
        System.out.println("║ 4. Return a Car                    ║");
        System.out.println("║ 5. Search Cars                     ║");
        System.out.println("║ 6. View Rental Summary             ║");
        System.out.println("║ 7. Export Data to CSV              ║");
        System.out.println("║ 0. Exit                            ║");
        System.out.println("╚════════════════════════════════════╝");
    }

    // ============== 1. ADD CAR ==============
    private static void addCar() {
        System.out.println("\n--- Add New Car ---");
        
        String carId = getStringInput("Enter Car ID (e.g., E001 or G001): ");
        String brand = getStringInput("Enter Car Brand (e.g., Tesla Model 3): ");
        double pricePerDay = getDoubleInput("Enter Price Per Day (USD): ");
        
        System.out.println("Select Car Type:");
        System.out.println("1. Electric Car");
        System.out.println("2. Gas Car");
        int type = getIntInput("Enter choice (1 or 2): ");
        
        Car car = null;
        
        if (type == 1) {
            double batteryCapacity = getDoubleInput("Enter Battery Capacity (kWh): ");
            car = new ElectricCar(carId, brand, pricePerDay, batteryCapacity);
        } else if (type == 2) {
            String fuelType = getStringInput("Enter Fuel Type (Diesel/Gasoline): ");
            car = new GasCar(carId, brand, pricePerDay, fuelType);
        } else {
            System.out.println("✗ Invalid car type!");
            return;
        }
        
        inventory.addCar(car);
        System.out.println("✓ Car added successfully!\n");
    }

    // ============== 2. DISPLAY CARS ==============
    private static void displayAllCars() {
        System.out.println("\n--- All Available Cars ---");
        inventory.displayAvailableCars();
    }

    // ============== 3. RENT CAR ==============
    private static void rentCar() {
        System.out.println("\n--- Rent a Car ---");
        
        // Display available cars first
        inventory.displayAvailableCars();
        
        String carId = getStringInput("Enter Car ID to rent: ");
        
        // Get customer information
        System.out.println("\n--- Customer Information ---");
        String customerId = getStringInput("Enter Customer ID: ");
        String customerName = getStringInput("Enter Customer Name: ");
        String customerPhone = getStringInput("Enter Customer Phone: ");
        
        Customer customer = new Customer(customerId, customerName, customerPhone);
        
        int days = getIntInput("Enter number of rental days: ");
        
        // Attempt to rent
        Rental rental = inventory.rentCar(carId, customer, days);
        
        if (rental != null) {
            System.out.println("\n✓ Rental successful!");
            
            // Offer to process payment
            String processPayment = getStringInput("Process payment now? (yes/no): ");
            if (processPayment.equalsIgnoreCase("yes")) {
                String paymentId = "PAY" + rental.getRentalId().substring(1);
                Payment payment = new Payment(paymentId, rental, rental.getTotalFee());
                payment.processPayment();
                System.out.println();
            }
        }
    }

    // ============== 4. RETURN CAR ==============
    private static void returnCar() {
        System.out.println("\n--- Return a Car ---");
        
        // Display active rentals
        List<Rental> allRentals = inventory.getAllRentals();
        System.out.println("\nActive Rentals:");
        boolean hasActive = false;
        
        for (Rental rental : allRentals) {
            if (!rental.isReturned()) {
                hasActive = true;
                System.out.println("  Rental ID: " + rental.getRentalId() + 
                                 " | Car: " + rental.getCar().getBrand() + 
                                 " | Customer: " + rental.getCustomer().getName() +
                                 " | Fee: $" + rental.getTotalFee());
            }
        }
        
        if (!hasActive) {
            System.out.println("  No active rentals.");
            return;
        }
        
        String rentalId = getStringInput("\nEnter Rental ID to return: ");
        inventory.returnCar(rentalId);
    }

    // ============== 5. SEARCH CARS ==============
    private static void searchCars() {
        System.out.println("\n--- Search Cars ---");
        System.out.println("1. Search by Brand");
        System.out.println("2. Search by Fuel Type");
        int choice = getIntInput("Enter choice (1 or 2): ");
        
        if (choice == 1) {
            String brand = getStringInput("Enter brand name: ");
            List<Car> results = inventory.searchByBrand(brand);
            
            System.out.println("\nSearch Results:");
            if (results.isEmpty()) {
                System.out.println("  No cars found.");
            } else {
                System.out.println("  Found " + results.size() + " car(s):");
                for (Car car : results) {
                    System.out.println("    - " + car.getId() + ": " + car.getBrand() + 
                                     " ($" + car.getPricePerDay() + "/day)");
                }
            }
        } else if (choice == 2) {
            String fuelType = getStringInput("Enter fuel type (Diesel/Gasoline): ");
            List<Car> results = inventory.searchByFuelType(fuelType);
            
            System.out.println("\nSearch Results:");
            if (results.isEmpty()) {
                System.out.println("  No cars found.");
            } else {
                System.out.println("  Found " + results.size() + " car(s):");
                for (Car car : results) {
                    System.out.println("    - " + car.getId() + ": " + car.getBrand() + 
                                     " ($" + car.getPricePerDay() + "/day)");
                }
            }
        } else {
            System.out.println("✗ Invalid choice!");
        }
        System.out.println();
    }

    // ============== 6. RENTAL SUMMARY ==============
    private static void viewRentalSummary() {
        System.out.println("\n--- Rental Summary Report ---\n");
        
        List<Rental> allRentals = inventory.getAllRentals();
        
        if (allRentals.isEmpty()) {
            System.out.println("No rentals in the system.");
            return;
        }
        
        int activeRentals = 0;
        int completedRentals = 0;
        double totalRevenue = 0.0;
        double pendingRevenue = 0.0;
        
        System.out.println("All Rentals:");
        for (Rental rental : allRentals) {
            String status = rental.isReturned() ? "COMPLETED" : "ACTIVE";
            System.out.println("  [" + status + "] " + 
                             rental.getRentalId() + " | " +
                             rental.getCar().getBrand() + " | " +
                             rental.getCustomer().getName() + " | " +
                             rental.getDays() + " days | $" + rental.getTotalFee());
            
            if (rental.isReturned()) {
                completedRentals++;
                totalRevenue += rental.getTotalFee();
            } else {
                activeRentals++;
                pendingRevenue += rental.getTotalFee();
            }
        }
        
        System.out.println("\nStatistics:");
        System.out.println("  Total Rentals: " + allRentals.size());
        System.out.println("  Active Rentals: " + activeRentals);
        System.out.println("  Completed Rentals: " + completedRentals);
        System.out.println("  Revenue from Completed: $" + totalRevenue);
        System.out.println("  Pending Revenue: $" + pendingRevenue);
        System.out.println("  Total Potential Revenue: $" + (totalRevenue + pendingRevenue));
        System.out.println();
    }

    // ============== 7. EXPORT TO CSV ==============
    private static void exportToCSV() {
        System.out.println("\n--- Export Data to CSV ---");
        
        try {
            CSVExporter.exportCarsToCSV(inventory.getAllCars(), "cars.csv");
            CSVExporter.exportRentalsToCSV(inventory.getAllRentals(), "rentals.csv");
            System.out.println("✓ CSV files created successfully!");
            System.out.println("  - cars.csv");
            System.out.println("  - rentals.csv");
        } catch (Exception e) {
            System.out.println("✗ ERROR while exporting CSV files.");
            e.printStackTrace();
        }
        System.out.println();
    }

    // ============== HELPER METHODS ==============
    private static void initializeDefaultCars() {
        // Add some default cars to start with
        inventory.addCar(new ElectricCar("E001", "Tesla Model 3", 100.0, 75.0));
        inventory.addCar(new ElectricCar("E002", "Tesla Model Y", 120.0, 80.0));
        inventory.addCar(new GasCar("G001", "BMW X5", 150.0, "Diesel"));
        inventory.addCar(new GasCar("G002", "Toyota Camry", 80.0, "Gasoline"));
        
        System.out.println("✓ System initialized with 4 default cars.\n");
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid input. Please enter a number.");
            }
        }
    }

    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("✗ Invalid input. Please enter a valid number.");
            }
        }
    }
}