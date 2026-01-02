package app;

import model.*;
import service.CarInventory;

public class Main {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   CAR RENTAL SYSTEM - DEMO");
        System.out.println("========================================\n");
        
        // ============== STEP 1: Create Inventory ==============
        System.out.println("--- Step 1: Initializing Car Inventory ---\n");
        CarInventory inventory = new CarInventory();
        
        // ============== STEP 2: Add Cars ==============
        System.out.println("--- Step 2: Adding Cars to Inventory ---\n");
        
        // Add Electric Cars
        Car tesla1 = new ElectricCar("E001", "Tesla Model 3", 100.0, 75.0);
        Car tesla2 = new ElectricCar("E002", "Tesla Model Y", 120.0, 80.0);
        Car nissan = new ElectricCar("E003", "Nissan Leaf", 70.0, 62.0);
        
        inventory.addCar(tesla1);
        inventory.addCar(tesla2);
        inventory.addCar(nissan);
        
        // Add Gas Cars
        Car bmw = new GasCar("G001", "BMW X5", 150.0, "Diesel");
        Car toyota = new GasCar("G002", "Toyota Camry", 80.0, "Gasoline");
        Car mercedes = new GasCar("G003", "Mercedes C-Class", 130.0, "Diesel");
        Car honda = new GasCar("G004", "Honda Civic", 75.0, "Gasoline");
        
        inventory.addCar(bmw);
        inventory.addCar(toyota);
        inventory.addCar(mercedes);
        inventory.addCar(honda);
        
        System.out.println();
        
        // ============== STEP 3: Display Available Cars ==============
        System.out.println("--- Step 3: Display All Available Cars ---");
        inventory.displayAvailableCars();
        
        // ============== STEP 4: Create Customers ==============
        System.out.println("--- Step 4: Creating Customers ---\n");
        
        Customer customer1 = new Customer("CUST001", "John Smith", "555-1234");
        Customer customer2 = new Customer("CUST002", "Sarah Johnson", "555-5678");
        Customer customer3 = new Customer("CUST003", "Mike Davis", "555-9012");
        
        System.out.println("Customer created: " + customer1.getName() + " (ID: " + customer1.getCustomerId() + ")");
        System.out.println("Customer created: " + customer2.getName() + " (ID: " + customer2.getCustomerId() + ")");
        System.out.println("Customer created: " + customer3.getName() + " (ID: " + customer3.getCustomerId() + ")");
        System.out.println();
        
        // ============== STEP 5: Rent Cars ==============
        System.out.println("--- Step 5: Processing Rental Transactions ---\n");
        
        // Rental 1: John rents Tesla Model 3 for 5 days
        System.out.println(">> John wants to rent Tesla Model 3 (E001) for 5 days");
        Rental rental1 = inventory.rentCar("E001", customer1, 5);
        
        // Rental 2: Sarah rents BMW X5 for 7 days
        System.out.println(">> Sarah wants to rent BMW X5 (G001) for 7 days");
        Rental rental2 = inventory.rentCar("G001", customer2, 7);
        
        // Rental 3: Mike rents Toyota Camry for 3 days
        System.out.println(">> Mike wants to rent Toyota Camry (G002) for 3 days");
        Rental rental3 = inventory.rentCar("G002", customer3, 3);
        
        // ============== STEP 6: Try to Rent Already Rented Car (Should Fail) ==============
        System.out.println("--- Step 6: Attempting to Rent Already Rented Car ---\n");
        System.out.println(">> Sarah tries to rent Tesla Model 3 (E001) - already rented by John");
        Rental failedRental = inventory.rentCar("E001", customer2, 4);
        System.out.println();
        
        // ============== STEP 7: Display Available Cars (After Rentals) ==============
        System.out.println("--- Step 7: Display Available Cars (After Rentals) ---");
        inventory.displayAvailableCars();
    }
}