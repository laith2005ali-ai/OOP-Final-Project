package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class ElectricCarTest {
    
    private ElectricCar tesla;
    
    @BeforeEach
    void setUp() {
        // Arrange: Create test object before each test
        tesla = new ElectricCar("E001", "Tesla Model 3", 100.0, 75.0);
    }
    
    @Test
    void testConstructor() {
        // Assert: Verify all fields initialized correctly
        assertEquals("E001", tesla.getId());
        assertEquals("Tesla Model 3", tesla.getBrand());
        assertEquals(100.0, tesla.getPricePerDay());
        assertEquals(75.0, tesla.getBatteryCapacity());
        assertTrue(tesla.isAvailable());
    }
    
    @Test
    void testGetBatteryCapacity() {
        assertEquals(75.0, tesla.getBatteryCapacity());
    }
    
    @Test
    void testCalculateRentalFee_ElectricDiscount() {
        // Act: Calculate fee for 5 days
        double fee = tesla.calculateRentalFee(5);
        
        // Assert: 5 days × $100/day × 0.9 (10% discount) = $450
        assertEquals(450.0, fee, 0.01);
    }
    
    @Test
    void testCalculateRentalFee_OneDayRental() {
        double fee = tesla.calculateRentalFee(1);
        assertEquals(90.0, fee, 0.01);
    }
    
    @Test
    void testCalculateRentalFee_MultipleWeeks() {
        // Act: 14 days rental
        double fee = tesla.calculateRentalFee(14);
        
        // Assert: 14 × $100 × 0.9 = $1,260
        assertEquals(1260.0, fee, 0.01);
    }
    
    @Test
    void testSetAvailable() {
        tesla.setAvailable(false);
        assertFalse(tesla.isAvailable());
        
        tesla.setAvailable(true);
        assertTrue(tesla.isAvailable());
    }
    
    @Test
    void testInheritance() {
        assertTrue(tesla instanceof Car);
        assertTrue(tesla instanceof Rentable);
    }
    
    @Test
    void testDifferentBatteryCapacities() {
        ElectricCar nissan = new ElectricCar("E002", "Nissan Leaf", 70.0, 62.0);
        
        assertEquals(75.0, tesla.getBatteryCapacity());
        assertEquals(62.0, nissan.getBatteryCapacity());
    }
}