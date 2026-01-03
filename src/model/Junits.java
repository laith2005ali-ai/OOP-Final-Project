package service;

import model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CarInventoryTest {
    
    private CarInventory inventory;
    private Car tesla;
    private Car bmw;
    private Car toyota;
    private Customer customer;
    
    // For testing console output
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        inventory = new CarInventory();
        tesla = new ElectricCar("E001", "Tesla Model 3", 100.0, 75.0);
        bmw = new GasCar("G001", "BMW X5", 150.0, "Diesel");
        toyota = new GasCar("G002", "Toyota Camry", 80.0, "Gasoline");
        customer = new Customer("CUST001", "John Smith", "555-1234");
        
        // Capture console output
        System.setOut(new PrintStream(outputStream));
    }
    
    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }
    
    // ============== CAR MANAGEMENT TESTS ==============
    
    @Test
    void testAddCar() {
        // Act
        inventory.addCar(tesla);
        
        // Assert: Car can be found
        Car found = inventory.findCarById("E001");
        assertNotNull(found);
        assertEquals(tesla, found);
    }
    
    @Test
    void testAddMultipleCars() {
        // Act
        inventory.addCar(tesla);
        inventory.addCar(bmw);
        inventory.addCar(toyota);
        
        // Assert: All cars can be found
        assertNotNull(inventory.findCarById("E001"));
        assertNotNull(inventory.findCarById("G001"));
        assertNotNull(inventory.findCarById("G002"));
    }
    
    @Test
    void testFindCarById_Found() {
        // Arrange
        inventory.addCar(tesla);
        
        // Act
        Car found = inventory.findCarById("E001");
        
        // Assert
        assertNotNull(found);
        assertEquals("E001", found.getId());
        assertEquals("Tesla Model 3", found.getBrand());
    }
    
    @Test
    void testFindCarById_NotFound() {
        // Act
        Car found = inventory.findCarById("X999");
        
        // Assert
        assertNull(found);
    }
    
    @Test
    void testRemoveCar_Success() {
        // Arrange
        inventory.addCar(tesla);
        
        // Act
        inventory.removeCar("E001");
        
        // Assert: Car no longer exists
        Car found = inventory.findCarById("E001");
        assertNull(found);
    }
    
    @Test
    void testRemoveCar_NotFound() {
        // Act: Try to remove non-existent car
        inventory.removeCar("X999");
        
        // Assert: Check console output
        String output = outputStream.toString();
        assertTrue(output.contains("Car not found"));
    }
    
    @Test
    void testDisplayAvailableCars_EmptyInventory() {
        // Act
        inventory.displayAvailableCars();
        
        // Assert: Should show "no cars available"
        String output = outputStream.toString();
        assertTrue(output.contains("No cars currently available"));
    }
    
    @Test
    void testDisplayAvailableCars_WithCars() {
        // Arrange
        inventory.addCar(tesla);
        inventory.addCar(bmw);
        
        // Act
        inventory.displayAvailableCars();
        
        // Assert: Should show both cars
        String output = outputStream.toString();
        assertTrue(output.contains("E001"));
        assertTrue(output.contains("Tesla Model 3"));
        assertTrue(output.contains("G001"));
        assertTrue(output.contains("BMW X5"));
    }
    
    // ============== RENTAL MANAGEMENT TESTS ==============
    
    @Test
    void testRentCar_Success() {
        // Arrange
        inventory.addCar(tesla);
        
        // Act
        Rental rental = inventory.rentCar("E001", customer, 5);
        
        // Assert
        assertNotNull(rental);
        assertEquals("R1", rental.getRentalId());
        assertEquals(tesla, rental.getCar());
        assertEquals(customer, rental.getCustomer());
        assertEquals(5, rental.getDays());
        assertFalse(rental.isReturned());
        
        // Car should be unavailable
        assertFalse(tesla.isAvailable());
    }
    
    @Test
    void testRentCar_CarNotFound() {
        // Act: Try to rent non-existent car
        Rental rental = inventory.rentCar("X999", customer, 5);
        
        // Assert
        assertNull(rental);
        
        // Check error message
        String output = outputStream.toString();
        assertTrue(output.contains("ERROR"));
        assertTrue(output.contains("not found"));
    }
    
    @Test
    void testRentCar_CarAlreadyRented() {
        // Arrange
        inventory.addCar(tesla);
        inventory.rentCar("E001", customer, 5); // First rental
        
        // Act: Try to rent same car again
        Customer customer2 = new Customer("CUST002", "Jane Doe", "555-5678");
        Rental rental2 = inventory.rentCar("E001", customer2, 3);
        
        // Assert
        assertNull(rental2);
        
        // Check error message
        String output = outputStream.toString();
        assertTrue(output.contains("not available"));
    }
    
    @Test
    void testRentCar_InvalidDays() {
        // Arrange
        inventory.addCar(tesla);
        
        // Act: Try to rent for 0 days
        Rental rental = inventory.rentCar("E001", customer, 0);
        
        // Assert
        assertNull(rental);
        
        // Check error message
        String output = outputStream.toString();
        assertTrue(output.contains("must be positive"));
    }
    
    @Test
    void testRentCar_AutoIncrementRentalId() {
        // Arrange
        inventory.addCar(tesla);
        inventory.addCar(bmw);
        inventory.addCar(toyota);
        
        Customer c1 = new Customer("C001", "Alice", "111");
        Customer c2 = new Customer("C002", "Bob", "222");
        Customer c3 = new Customer("C003", "Charlie", "333");
        
        // Act: Rent 3 cars
        Rental r1 = inventory.rentCar("E001", c1, 5);
        Rental r2 = inventory.rentCar("G001", c2, 7);
        Rental r3 = inventory.rentCar("G002", c3, 3);
        
        // Assert: IDs should auto-increment
        assertEquals("R1", r1.getRentalId());
        assertEquals("R2", r2.getRentalId());
        assertEquals("R3", r3.getRentalId());
    }
    
    @Test
    void testReturnCar_Success() {
        // Arrange
        inventory.addCar(tesla);
        Rental rental = inventory.rentCar("E001", customer, 5);
        
        // Act
        inventory.returnCar("R1");
        
        // Assert: Rental marked as returned
        assertTrue(rental.isReturned());
        
        // Car should be available again
        assertTrue(tesla.isAvailable());
    }
    
    @Test
    void testReturnCar_RentalNotFound() {
        // Act: Try to return non-existent rental
        inventory.returnCar("R999");
        
        // Assert: Check error message
        String output = outputStream.toString();
        assertTrue(output.contains("Rental not found"));
    }
    
    @Test
    void testReturnCar_AlreadyReturned() {
        // Arrange
        inventory.addCar(tesla);
        Rental rental = inventory.rentCar("E001", customer, 5);
        inventory.returnCar("R1"); // First return
        
        // Act: Try to return again
        inventory.returnCar("R1");
        
        // Assert: Check error message
        String output = outputStream.toString();
        assertTrue(output.contains("already been returned"));
    }
    
    @Test
    void testCompleteRentalWorkflow() {
        // Complete workflow: Add → Rent → Return
        
        // 1. Add car
        inventory.addCar(tesla);
        assertTrue(tesla.isAvailable());
        
        // 2. Rent car
        Rental rental = inventory.rentCar("E001", customer, 5);
        assertNotNull(rental);
        assertFalse(tesla.isAvailable());
        assertFalse(rental.isReturned());
        
        // 3. Return car
        inventory.returnCar("R1");
        assertTrue(tesla.isAvailable());
        assertTrue(rental.isReturned());
        
        // 4. Can rent again
        Customer customer2 = new Customer("CUST002", "Jane", "555-5678");
        Rental rental2 = inventory.rentCar("E001", customer2, 3);
        assertNotNull(rental2);
        assertEquals("R2", rental2.getRentalId());
    }
    
    // ============== SEARCH & FILTER TESTS ==============
    
    @Test
    void testSearchByBrand_Found() {
        // Arrange
        inventory.addCar(tesla);
        inventory.addCar(new ElectricCar("E002", "Tesla Model Y", 120.0, 80.0));
        inventory.addCar(bmw);
        
        // Act
        List<Car> teslas = inventory.searchByBrand("Tesla");
        
        // Assert
        assertEquals(2, teslas.size());
        assertTrue(teslas.stream().allMatch(c -> c.getBrand().contains("Tesla")));
    }
    
    @Test
    void testSearchByBrand_NotFound() {
        // Arrange
        inventory.addCar(tesla);
        
        // Act
        List<Car> hondas = inventory.searchByBrand("Honda");
        
        // Assert
        assertTrue(hondas.isEmpty());
    }
    
    @Test
    void testSearchByBrand_CaseInsensitive() {
        // Arrange
        inventory.addCar(tesla);
        
        // Act
        List<Car> results1 = inventory.searchByBrand("tesla");
        List<Car> results2 = inventory.searchByBrand("TESLA");
        List<Car> results3 = inventory.searchByBrand("TeSLa");
        
        // Assert: All should find the car
        assertEquals(1, results1.size());
        assertEquals(1, results2.size());
        assertEquals(1, results3.size());
    }
    
    @Test
    void testSearchByBrand_OnlyAvailableCars() {
        // Arrange
        inventory.addCar(tesla);
        inventory.addCar(new ElectricCar("E002", "Tesla Model Y", 120.0, 80.0));
        
        // Rent one Tesla
        inventory.rentCar("E001", customer, 5);
        
        // Act
        List<Car> available = inventory.searchByBrand("Tesla");
        
        // Assert: Only one available (E002)
        assertEquals(1, available.size());
        assertEquals("E002", available.get(0).getId());
    }
    
    @Test
    void testSearchByFuelType_Diesel() {
        // Arrange
        inventory.addCar(bmw); // Diesel
        inventory.addCar(toyota); // Gasoline
        inventory.addCar(new GasCar("G003", "Mercedes", 130.0, "Diesel"));
        
        // Act
        List<Car> dieselCars = inventory.searchByFuelType("Diesel");
        
        // Assert
        assertEquals(2, dieselCars.size());
    }
    
    @Test
    void testSearchByFuelType_Gasoline() {
        // Arrange
        inventory.addCar(bmw); // Diesel
        inventory.addCar(toyota); // Gasoline
        
        // Act
        List<Car> gasolineCars = inventory.searchByFuelType("Gasoline");
        
        // Assert
        assertEquals(1, gasolineCars.size());
        assertEquals("G002", gasolineCars.get(0).getId());
    }
    
    @Test
    void testSearchByFuelType_ExcludesElectricCars() {
        // Arrange
        inventory.addCar(tesla); // Electric
        inventory.addCar(bmw); // Diesel
        
        // Act
        List<Car> dieselCars = inventory.searchByFuelType("Diesel");
        
        // Assert: Only BMW, not Tesla
        assertEquals(1, dieselCars.size());
        assertEquals("G001", dieselCars.get(0).getId());
    }
    
    @Test
    void testSearchByFuelType_OnlyAvailableCars() {
        // Arrange
        inventory.addCar(bmw); // Diesel
        inventory.addCar(new GasCar("G003", "Mercedes", 130.0, "Diesel"));
        
        // Rent BMW
        inventory.rentCar("G001", customer, 5);
        
        // Act
        List<Car> available = inventory.searchByFuelType("Diesel");
        
        // Assert: Only Mercedes available
        assertEquals(1, available.size());
        assertEquals("G003", available.get(0).getId());
    }
    
    @Test
    void testGetAllRentals_Empty() {
        // Act
        List<Rental> rentals = inventory.getAllRentals();
        
        // Assert
        assertNotNull(rentals);
        assertTrue(rentals.isEmpty());
    }
    
    @Test
    void testGetAllRentals_WithRentals() {
        // Arrange
        inventory.addCar(tesla);
        inventory.addCar(bmw);
        
        Customer c1 = customer;
        Customer c2 = new Customer("CUST002", "Jane", "555-5678");
        
        // Act: Create rentals
        inventory.rentCar("E001", c1, 5);
        inventory.rentCar("G001", c2, 7);
        
        List<Rental> rentals = inventory.getAllRentals();
        
        // Assert
        assertEquals(2, rentals.size());
    }
    
    @Test
    void testGetAllRentals_DefensiveCopy() {
        // Arrange
        inventory.addCar(tesla);
        inventory.rentCar("E001", customer, 5);
        
        // Act: Get rentals and try to modify
        List<Rental> rentals = inventory.getAllRentals();
        rentals.clear(); // Try to clear the list
        
        // Assert: Original list unchanged
        List<Rental> rentalsAgain = inventory.getAllRentals();
        assertEquals(1, rentalsAgain.size());
    }
    
    // ============== INTEGRATION TESTS ==============
    
    @Test
    void testMultipleRentalsAndReturns() {
        // Arrange: Add 3 cars
        inventory.addCar(tesla);
        inventory.addCar(bmw);
        inventory.addCar(toyota);
        
        Customer c1 = new Customer("C001", "Alice", "111");
        Customer c2 = new Customer("C002", "Bob", "222");
        Customer c3 = new Customer("C003", "Charlie", "333");
        
        // Act: Rent all 3
        Rental r1 = inventory.rentCar("E001", c1, 5);
        Rental r2 = inventory.rentCar("G001", c2, 7);
        Rental r3 = inventory.rentCar("G002", c3, 3);
        
        // All rented
        assertFalse(tesla.isAvailable());
        assertFalse(bmw.isAvailable());
        assertFalse(toyota.isAvailable());
        
        // Return 2 of them
        inventory.returnCar("R1");
        inventory.returnCar("R3");
        
        // Assert states
        assertTrue(tesla.isAvailable());      // Returned
        assertFalse(bmw.isAvailable());       // Still rented
        assertTrue(toyota.isAvailable());     // Returned
        
        assertTrue(r1.isReturned());
        assertFalse(r2.isReturned());
        assertTrue(r3.isReturned());
    }
    
    