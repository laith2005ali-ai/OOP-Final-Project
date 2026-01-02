package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class RentalTest {
    
    private Car tesla;
    private Car bmw;
    private Car toyota;
    private Customer customer;
    private Rental rental;
    
    @BeforeEach
    void setUp() {
        tesla = new ElectricCar("E001", "Tesla Model 3", 100.0, 75.0);
        bmw = new GasCar("G001", "BMW X5", 150.0, "Diesel");
        toyota = new GasCar("G002", "Toyota Camry", 80.0, "Gasoline");
        customer = new Customer("CUST001", "John Smith", "555-1234");
        rental = new Rental("R001", tesla, customer, 5);
    }
    
    @Test
    void testConstructor() {
        assertEquals("R001", rental.getRentalId());
        assertEquals(tesla, rental.getCar());
        assertEquals(customer, rental.getCustomer());
        assertEquals(5, rental.getDays());
        assertFalse(rental.isReturned());
    }
    
    @Test
    void testGetRentalId() {
        assertEquals("R001", rental.getRentalId());
    }
    
    @Test
    void testGetCar() {
        assertEquals(tesla, rental.getCar());
    }
    
    @Test
    void testGetCustomer() {
        assertEquals(customer, rental.getCustomer());
    }
    
    @Test
    void testGetDays() {
        assertEquals(5, rental.getDays());
    }
    
    @Test
    void testIsReturned_InitiallyFalse() {
        assertFalse(rental.isReturned());
    }
    
    @Test
    void testSetReturned() {
        // Initially false
        assertFalse(rental.isReturned());
        
        // Mark as returned
        rental.setReturned(true);
        assertTrue(rental.isReturned());
        
        // Mark as not returned again
        rental.setReturned(false);
        assertFalse(rental.isReturned());
    }
    
    @Test
    void testGetTotalFee_ElectricCar() {
        // Act: Get total fee (should use ElectricCar calculation)
        double fee = rental.getTotalFee();
        
        // Assert: 5 days × $100 × 0.9 = $450
        assertEquals(450.0, fee, 0.01);
    }
    
    @Test
    void testGetTotalFee_GasCar_Diesel() {
        // Arrange: Create rental with BMW (diesel)
        Rental bmwRental = new Rental("R002", bmw, customer, 7);
        
        // Act
        double fee = bmwRental.getTotalFee();
        
        // Assert: 7 × $150 × 1.15 = $1,207.50
        assertEquals(1207.50, fee, 0.01);
    }
    
    @Test
    void testGetTotalFee_GasCar_Gasoline() {
        // Arrange: Create rental with Toyota (gasoline)
        Rental toyotaRental = new Rental("R003", toyota, customer, 3);
        
        // Act
        double fee = toyotaRental.getTotalFee();
        
        // Assert: 3 × $80 = $240
        assertEquals(240.0, fee, 0.01);
    }
    
    @Test
    void testGetTotalFee_Polymorphism() {
        // CRITICAL TEST: Proves POLYMORPHISM works!
        
        // Arrange: Three rentals with different car types, same duration
        Rental electricRental = new Rental("R001", tesla, customer, 5);
        Rental dieselRental = new Rental("R002", bmw, customer, 5);
        Rental gasolineRental = new Rental("R003", toyota, customer, 5);
        
        // Act: Same method call, different results
        double electricFee = electricRental.getTotalFee();
        double dieselFee = dieselRental.getTotalFee();
        double gasolineFee = gasolineRental.getTotalFee();
        
        // Assert: Three different fees due to polymorphism
        assertEquals(450.0, electricFee, 0.01);    // 5 × 100 × 0.9
        assertEquals(862.50, dieselFee, 0.01);     // 5 × 150 × 1.15
        assertEquals(400.0, gasolineFee, 0.01);    // 5 × 80 × 1.0
        
        // All different - proves polymorphism!
        assertNotEquals(electricFee, dieselFee);
        assertNotEquals(electricFee, gasolineFee);
        assertNotEquals(dieselFee, gasolineFee);
    }
    
    @Test
    void testComposition_HasACar() {
        // Rental HAS-A Car
        assertNotNull(rental.getCar());
        
        // Can access car details through rental
        assertEquals("E001", rental.getCar().getId());
        assertEquals("Tesla Model 3", rental.getCar().getBrand());
        assertEquals(100.0, rental.getCar().getPricePerDay());
    }
    
    @Test
    void testComposition_HasACustomer() {
        // Rental HAS-A Customer
        assertNotNull(rental.getCustomer());
        
        // Can access customer details through rental
        assertEquals("CUST001", rental.getCustomer().getCustomerId());
        assertEquals("John Smith", rental.getCustomer().getName());
        assertEquals("555-1234", rental.getCustomer().getPhone());
    }
    
    @Test
    void testDifferentDurations() {
        // Arrange: Rentals with different durations
        Rental shortRental = new Rental("R001", tesla, customer, 1);
        Rental mediumRental = new Rental("R002", tesla, customer, 7);
        Rental longRental = new Rental("R003", tesla, customer, 14);
        
        // Act
        double shortFee = shortRental.getTotalFee();
        double mediumFee = mediumRental.getTotalFee();
        double longFee = longRental.getTotalFee();
        
        // Assert: Fee scales linearly with duration
        assertEquals(90.0, shortFee, 0.01);       // 1 × 100 × 0.9
        assertEquals(630.0, mediumFee, 0.01);     // 7 × 100 × 0.9
        assertEquals(1260.0, longFee, 0.01);      // 14 × 100 × 0.9
        
        // Longer rentals cost more
        assertTrue(shortFee < mediumFee);
        assertTrue(mediumFee < longFee);
    }
    
    @Test
    void testRentalLifecycle() {
        // Simulate full rental lifecycle
        
        // 1. Rental created (not returned)
        Rental newRental = new Rental("R001", tesla, customer, 5);
        assertFalse(newRental.isReturned());
        
        // 2. Car is rented out (would happen in CarInventory)
        // 3. Time passes...
        
        // 4. Customer returns car
        newRental.setReturned(true);
        assertTrue(newRental.isReturned());
        
        // 5. Fee is calculated
        double fee = newRental.getTotalFee();
        assertEquals(450.0, fee, 0.01);
    }
    
    @Test
    void testMultipleRentalsFromSameCustomer() {
        // Same customer rents multiple cars
        Rental rental1 = new Rental("R001", tesla, customer, 5);
        Rental rental2 = new Rental("R002", bmw, customer, 3);
        
        // Both rentals valid
        assertEquals(customer, rental1.getCustomer());
        assertEquals(customer, rental2.getCustomer());
        
        // Different cars and fees
        assertNotEquals(rental1.getCar(), rental2.getCar());
        assertNotEquals(rental1.getTotalFee(), rental2.getTotalFee());
    }
}