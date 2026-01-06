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
    
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        inventory = new CarInventory();
        tesla = new ElectricCar("E001", "Tesla Model 3", 100.0, 75.0);
        bmw = new GasCar("G001", "BMW X5", 150.0, "Diesel");
        toyota = new GasCar("G002", "Toyota Camry", 80.0, "Gasoline");
        customer = new Customer("CUST001", "John Smith", "555-1234");
        
        System.setOut(new PrintStream(outputStream));
    }
    
    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }
    
    // ============== CAR MANAGEMENT TESTS ==============
    
    @Test
    void testAddCar() {
        inventory.addCar(tesla);
        Car found = inventory.findCarById("E001");
        assertNotNull(found);
        assertEquals(tesla, found);
    }
    
    @Test
    void testAddMultipleCars() {
        inventory.addCar(tesla);
        inventory.addCar(bmw);
        inventory.addCar(toyota);
        
        assertNotNull(inventory.findCarById("E001"));
        assertNotNull(inventory.findCarById("G001"));
        assertNotNull(inventory.findCarById("G002"));
    }
    
    @Test
    void testFindCarById_Found() {
        inventory.addCar(tesla);
        Car found = inventory.findCarById("E001");
        
        assertNotNull(found);
        assertEquals("E001", found.getId());
        assertEquals("Tesla Model 3", found.getBrand());
    }
    
    @Test
    void testFindCarById_NotFound() {
        Car found = inventory.findCarById("X999");
        assertNull(found);
    }
    
    @Test
    void testRemoveCar_Success() {
        inventory.addCar(tesla);
        inventory.removeCar("E001");
        
        Car found = inventory.findCarById("E001");
        assertNull(found);
    }
    
    @Test
    void testRemoveCar_NotFound() {
        inventory.removeCar("X999");
        String output = outputStream.toString();
        assertTrue(output.contains("Car not found"));
    }
    
    @Test
    void testDisplayAvailableCars_EmptyInventory() {
        inventory.displayAvailableCars();
        String output = outputStream.toString();
        assertTrue(output.contains("No cars currently available"));
    }
    
    @Test
    void testDisplayAvailableCars_WithCars() {
        inventory.addCar(tesla);
        inventory.addCar(bmw);
        
        inventory.displayAvailableCars();
        
        String output = outputStream.toString();
        assertTrue(output.contains("E001"));
        assertTrue(output.contains("Tesla Model 3"));
        assertTrue(output.contains("G001"));
        assertTrue(output.contains("BMW X5"));
    }
    
    // ============== RENTAL MANAGEMENT TESTS ==============
    
    @Test
    void testRentCar_Success() {
        inventory.addCar(tesla);
        Rental rental = inventory.rentCar("E001", customer, 5);
        
        assertNotNull(rental);
        assertEquals("R1", rental.getRentalId());
        assertEquals(tesla, rental.getCar());
        assertEquals(customer, rental.getCustomer());
        assertEquals(5, rental.getDays());
        assertFalse(rental.isReturned());
        assertFalse(tesla.isAvailable());
    }
    
    @Test
    void testRentCar_CarNotFound() {
        Rental rental = inventory.rentCar("X999", customer, 5);
        assertNull(rental);
        
        String output = outputStream.toString();
        assertTrue(output.contains("ERROR"));
        assertTrue(output.contains("not found"));
    }
    
    @Test
    void testRentCar_CarAlreadyRented() {
        inventory.addCar(tesla);
        inventory.rentCar("E001", customer, 5);
        
        Customer customer2 = new Customer("CUST002", "Jane Doe", "555-5678");
        Rental rental2 = inventory.rentCar("E001", customer2, 3);
        
        assertNull(rental2);
        String output = outputStream.toString();
        assertTrue(output.contains("not available"));
    }
    
    @Test
    void testRentCar_InvalidDays() {
        inventory.addCar(tesla);
        Rental rental = inventory.rentCar("E001", customer, 0);
        
        assertNull(rental);
        String output = outputStream.toString();
        assertTrue(output.contains("must be positive"));
    }
    
    @Test
    void testRentCar_AutoIncrementRentalId() {
        inventory.addCar(tesla);
        inventory.addCar(bmw);
        inventory.addCar(toyota);
        
        Customer c1 = new Customer("C001", "Alice", "111");
        Customer c2 = new Customer("C002", "Bob", "222");
        Customer c3 = new Customer("C003", "Charlie", "333");
        
        Rental r1 = inventory.rentCar("E001", c1, 5);
        Rental r2 = inventory.rentCar("G001", c2, 7);
        Rental r3 = inventory.rentCar("G002", c3, 3);
        
        assertEquals("R1", r1.getRentalId());
        assertEquals("R2", r2.getRentalId());
        assertEquals("R3", r3.getRentalId());
    }
    
    @Test
    void testReturnCar_Success() {
        inventory.addCar(tesla);
        Rental rental = inventory.rentCar("E001", customer, 5);
        
        inventory.returnCar("R1");
        
        assertTrue(rental.isReturned());
        assertTrue(tesla.isAvailable());
    }
    
    @Test
    void testReturnCar_RentalNotFound() {
        inventory.returnCar("R999");
        String output = outputStream.toString();
        assertTrue(output.contains("Rental not found"));
    }
    
    @Test
    void testReturnCar_AlreadyReturned() {
        inventory.addCar(tesla);
        inventory.rentCar("E001", customer, 5);
        inventory.returnCar("R1");
        
        inventory.returnCar("R1");
        String output = outputStream.toString();
        assertTrue(output.contains("already been returned"));
    }
    
    @Test
    void testCompleteRentalWorkflow() {
        inventory.addCar(tesla);
        assertTrue(tesla.isAvailable());
        
        Rental rental = inventory.rentCar("E001", customer, 5);
        assertNotNull(rental);
        assertFalse(tesla.isAvailable());
        assertFalse(rental.isReturned());
        
        inventory.returnCar("R1");
        assertTrue(tesla.isAvailable());
        assertTrue(rental.isReturned());
        
        Customer customer2 = new Customer("CUST002", "Jane", "555-5678");
        Rental rental2 = inventory.rentCar("E001", customer2, 3);
        assertNotNull(rental2);
        assertEquals("R2", rental2.getRentalId());
    }
    
    // ============== SEARCH & FILTER TESTS ==============
    
    @Test
    void testSearchByBrand_Found() {
        inventory.addCar(tesla);
        inventory.addCar(new ElectricCar("E002", "Tesla Model Y", 120.0, 80.0));
        inventory.addCar(bmw);
        
        List<Car> teslas = inventory.searchByBrand("Tesla");
        
        assertEquals(2, teslas.size());
        assertTrue(teslas.stream().allMatch(c -> c.getBrand().contains("Tesla")));
    }
    
    @Test
    void testSearchByBrand_NotFound() {
        inventory.addCar(tesla);
        List<Car> hondas = inventory.searchByBrand("Honda");
        assertTrue(hondas.isEmpty());
    }
    
    @Test
    void testSearchByBrand_CaseInsensitive() {
        inventory.addCar(tesla);
        
        List<Car> results1 = inventory.searchByBrand("tesla");
        List<Car> results2 = inventory.searchByBrand("TESLA");
        List<Car> results3 = inventory.searchByBrand("TeSLa");
        
        assertEquals(1, results1.size());
        assertEquals(1, results2.size());
        assertEquals(1, results3.size());
    }
    
    @Test
    void testSearchByBrand_OnlyAvailableCars() {
        inventory.addCar(tesla);
        inventory.addCar(new ElectricCar("E002", "Tesla Model Y", 120.0, 80.0));
        
        inventory.rentCar("E001", customer, 5);
        
        List<Car> available = inventory.searchByBrand("Tesla");
        assertEquals(1, available.size());
        assertEquals("E002", available.get(0).getId());
    }
    
    @Test
    void testSearchByFuelType_Diesel() {
        inventory.addCar(bmw);
        inventory.addCar(toyota);
        inventory.addCar(new GasCar("G003", "Mercedes", 130.0, "Diesel"));
        
        List<Car> dieselCars = inventory.searchByFuelType("Diesel");
        assertEquals(2, dieselCars.size());
    }
    
    @Test
    void testSearchByFuelType_Gasoline() {
        inventory.addCar(bmw);
        inventory.addCar(toyota);
        
        List<Car> gasolineCars = inventory.searchByFuelType("Gasoline");
        assertEquals(1, gasolineCars.size());
        assertEquals("G002", gasolineCars.get(0).getId());
    }
    
    @Test
    void testSearchByFuelType_ExcludesElectricCars() {
        inventory.addCar(tesla);
        inventory.addCar(bmw);
        
        List<Car> dieselCars = inventory.searchByFuelType("Diesel");
        assertEquals(1, dieselCars.size());
        assertEquals("G001", dieselCars.get(0).getId());
    }
    
    @Test
    void testSearchByFuelType_OnlyAvailableCars() {
        inventory.addCar(bmw);
        inventory.addCar(new GasCar("G003", "Mercedes", 130.0, "Diesel"));
        
        inventory.rentCar("G001", customer, 5);
        
        List<Car> available = inventory.searchByFuelType("Diesel");
        assertEquals(1, available.size());
        assertEquals("G003", available.get(0).getId());
    }
    
    @Test
    void testGetAllRentals_Empty() {
        List<Rental> rentals = inventory.getAllRentals();
        assertNotNull(rentals);
        assertTrue(rentals.isEmpty());
    }
    
    @Test
    void testGetAllRentals_WithRentals() {
        inventory.addCar(tesla);
        inventory.addCar(bmw);
        
        Customer c1 = customer;
        Customer c2 = new Customer("CUST002", "Jane", "555-5678");
        
        inventory.rentCar("E001", c1, 5);
        inventory.rentCar("G001", c2, 7);
        
        List<Rental> rentals = inventory.getAllRentals();
        assertEquals(2, rentals.size());
    }
    
    @Test
    void testGetAllRentals_DefensiveCopy() {
        inventory.addCar(tesla);
        inventory.rentCar("E001", customer, 5);
        
        List<Rental> rentals = inventory.getAllRentals();
        rentals.clear();
        
        List<Rental> rentalsAgain = inventory.getAllRentals();
        assertEquals(1, rentalsAgain.size());
    }
    
    // ============== INTEGRATION TESTS ==============
    
    @Test
    void testMultipleRentalsAndReturns() {
        inventory.addCar(tesla);
        inventory.addCar(bmw);
        inventory.addCar(toyota);
        
        Customer c1 = new Customer("C001", "Alice", "111");
        Customer c2 = new Customer("C002", "Bob", "222");
        Customer c3 = new Customer("C003", "Charlie", "333");
        
        Rental r1 = inventory.rentCar("E001", c1, 5);
        Rental r2 = inventory.rentCar("G001", c2, 7);
        Rental r3 = inventory.rentCar("G002", c3, 3);
        
        assertFalse(tesla.isAvailable());
        assertFalse(bmw.isAvailable());
        assertFalse(toyota.isAvailable());
        
        inventory.returnCar("R1");
        inventory.returnCar("R3");
        
        assertTrue(tesla.isAvailable());
        assertFalse(bmw.isAvailable());
        assertTrue(toyota.isAvailable());
        
        assertTrue(r1.isReturned());
        assertFalse(r2.isReturned());
        assertTrue(r3.isReturned());
    }
    
    @Test
    void testPolymorphicRentalFees() {
        inventory.addCar(tesla);
        inventory.addCar(bmw);
        inventory.addCar(toyota);
        
        Customer c = customer;
        
        Rental r1 = inventory.rentCar("E001", c, 5);
        Rental r2 = inventory.rentCar("G001", c, 5);
        Rental r3 = inventory.rentCar("G002", c, 5);
        
        assertEquals(450.0, r1.getTotalFee(), 0.01);
        assertEquals(862.50, r2.getTotalFee(), 0.01);
        assertEquals(400.0, r3.getTotalFee(), 0.01);
    }
    
    @Test
    void testSystemStateConsistency() {
        inventory.addCar(tesla);
        assertTrue(tesla.isAvailable());
        assertEquals(0, inventory.getAllRentals().size());
        
        Rental rental = inventory.rentCar("E001", customer, 5);
        assertFalse(tesla.isAvailable());
        assertEquals(1, inventory.getAllRentals().size());
        assertFalse(rental.isReturned());
        
        inventory.returnCar("R1");
        assertTrue(tesla.isAvailable());
        assertEquals(1, inventory.getAllRentals().size());
        assertTrue(rental.isReturned());
    }
}