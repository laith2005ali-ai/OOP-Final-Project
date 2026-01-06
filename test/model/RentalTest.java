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
        assertFalse(rental.isReturned());
        
        rental.setReturned(true);
        assertTrue(rental.isReturned());
        
        rental.setReturned(false);
        assertFalse(rental.isReturned());
    }
    
    @Test
    void testGetTotalFee_ElectricCar() {
        double fee = rental.getTotalFee();
        assertEquals(450.0, fee, 0.01);
    }
    
    @Test
    void testGetTotalFee_GasCar_Diesel() {
        Rental bmwRental = new Rental("R002", bmw, customer, 7);
        double fee = bmwRental.getTotalFee();
        assertEquals(1207.50, fee, 0.01);
    }
    
    @Test
    void testGetTotalFee_GasCar_Gasoline() {
        Rental toyotaRental = new Rental("R003", toyota, customer, 3);
        double fee = toyotaRental.getTotalFee();
        assertEquals(240.0, fee, 0.01);
    }
    
    @Test
    void testGetTotalFee_Polymorphism() {
        Rental electricRental = new Rental("R001", tesla, customer, 5);
        Rental dieselRental = new Rental("R002", bmw, customer, 5);
        Rental gasolineRental = new Rental("R003", toyota, customer, 5);
        
        double electricFee = electricRental.getTotalFee();
        double dieselFee = dieselRental.getTotalFee();
        double gasolineFee = gasolineRental.getTotalFee();
        
        assertEquals(450.0, electricFee, 0.01);
        assertEquals(862.50, dieselFee, 0.01);
        assertEquals(400.0, gasolineFee, 0.01);
        
        assertNotEquals(electricFee, dieselFee);
        assertNotEquals(electricFee, gasolineFee);
        assertNotEquals(dieselFee, gasolineFee);
    }
    
    @Test
    void testComposition_HasACar() {
        assertNotNull(rental.getCar());
        assertEquals("E001", rental.getCar().getId());
        assertEquals("Tesla Model 3", rental.getCar().getBrand());
        assertEquals(100.0, rental.getCar().getPricePerDay());
    }
    
    @Test
    void testComposition_HasACustomer() {
        assertNotNull(rental.getCustomer());
        assertEquals("CUST001", rental.getCustomer().getCustomerId());
        assertEquals("John Smith", rental.getCustomer().getName());
        assertEquals("555-1234", rental.getCustomer().getPhone());
    }
    
    @Test
    void testDifferentDurations() {
        Rental shortRental = new Rental("R001", tesla, customer, 1);
        Rental mediumRental = new Rental("R002", tesla, customer, 7);
        Rental longRental = new Rental("R003", tesla, customer, 14);
        
        double shortFee = shortRental.getTotalFee();
        double mediumFee = mediumRental.getTotalFee();
        double longFee = longRental.getTotalFee();
        
        assertEquals(90.0, shortFee, 0.01);
        assertEquals(630.0, mediumFee, 0.01);
        assertEquals(1260.0, longFee, 0.01);
        
        assertTrue(shortFee < mediumFee);
        assertTrue(mediumFee < longFee);
    }
    
    @Test
    void testRentalLifecycle() {
        Rental newRental = new Rental("R001", tesla, customer, 5);
        assertFalse(newRental.isReturned());
        
        newRental.setReturned(true);
        assertTrue(newRental.isReturned());
        
        double fee = newRental.getTotalFee();
        assertEquals(450.0, fee, 0.01);
    }
    
    @Test
    void testMultipleRentalsFromSameCustomer() {
        Rental rental1 = new Rental("R001", tesla, customer, 5);
        Rental rental2 = new Rental("R002", bmw, customer, 3);
        
        assertEquals(customer, rental1.getCustomer());
        assertEquals(customer, rental2.getCustomer());
        
        assertNotEquals(rental1.getCar(), rental2.getCar());
        assertNotEquals(rental1.getTotalFee(), rental2.getTotalFee());
    }
}