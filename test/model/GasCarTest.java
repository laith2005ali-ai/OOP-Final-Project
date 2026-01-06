package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class GasCarTest {
    
    private GasCar bmw;
    private GasCar toyota;
    
    @BeforeEach
    void setUp() {
        bmw = new GasCar("G001", "BMW X5", 150.0, "Diesel");
        toyota = new GasCar("G002", "Toyota Camry", 80.0, "Gasoline");
    }
    
    @Test
    void testConstructor_Diesel() {
        assertEquals("G001", bmw.getId());
        assertEquals("BMW X5", bmw.getBrand());
        assertEquals(150.0, bmw.getPricePerDay());
        assertEquals("Diesel", bmw.getFuelType());
        assertTrue(bmw.isAvailable());
    }
    
    @Test
    void testConstructor_Gasoline() {
        assertEquals("G002", toyota.getId());
        assertEquals("Toyota Camry", toyota.getBrand());
        assertEquals(80.0, toyota.getPricePerDay());
        assertEquals("Gasoline", toyota.getFuelType());
        assertTrue(toyota.isAvailable());
    }
    
    @Test
    void testGetFuelType() {
        assertEquals("Diesel", bmw.getFuelType());
        assertEquals("Gasoline", toyota.getFuelType());
    }
    
    @Test
    void testCalculateRentalFee_Diesel_WithSurcharge() {
        double fee = bmw.calculateRentalFee(7);
        assertEquals(1207.50, fee, 0.01);
    }
    
    @Test
    void testCalculateRentalFee_Gasoline_StandardRate() {
        double fee = toyota.calculateRentalFee(3);
        assertEquals(240.0, fee, 0.01);
    }
    
    @Test
    void testCalculateRentalFee_OneDayDiesel() {
        double fee = bmw.calculateRentalFee(1);
        assertEquals(172.50, fee, 0.01);
    }
    
    @Test
    void testCalculateRentalFee_OneDayGasoline() {
        double fee = toyota.calculateRentalFee(1);
        assertEquals(80.0, fee, 0.01);
    }
    
    @Test
    void testFuelTypeCaseInsensitive() {
        GasCar testCar = new GasCar("G003", "Test Car", 100.0, "diesel");
        double fee = testCar.calculateRentalFee(5);
        assertEquals(575.0, fee, 0.01);
    }
    
    @Test
    void testInheritance() {
        assertTrue(bmw instanceof Car);
        assertTrue(bmw instanceof Rentable);
        assertTrue(toyota instanceof Car);
        assertTrue(toyota instanceof Rentable);
    }
    
    @Test
    void testPolymorphicBehavior_DieselVsGasoline() {
        GasCar diesel = new GasCar("G001", "Car A", 100.0, "Diesel");
        GasCar gasoline = new GasCar("G002", "Car B", 100.0, "Gasoline");
        
        double dieselFee = diesel.calculateRentalFee(5);
        double gasolineFee = gasoline.calculateRentalFee(5);
        
        assertEquals(575.0, dieselFee, 0.01);
        assertEquals(500.0, gasolineFee, 0.01);
        assertNotEquals(dieselFee, gasolineFee);
    }
}