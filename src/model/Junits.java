package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {
    
    private Car tesla;
    private Customer customer;
    private Rental rental;
    private Payment payment;
    
    // For testing console output
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        tesla = new ElectricCar("E001", "Tesla Model 3", 100.0, 75.0);
        customer = new Customer("CUST001", "John Smith", "555-1234");
        rental = new Rental("R001", tesla, customer, 5);
        payment = new Payment("PAY001", rental, rental.getTotalFee());
        
        // Capture console output
        System.setOut(new PrintStream(outputStream));
    }
    
    @AfterEach
    void restoreStreams() {
        // Restore original System.out
        System.setOut(originalOut);
    }
    
    @Test
    void testConstructor() {
        assertEquals("PAY001", payment.getPaymentId());
        assertEquals(rental, payment.getRental());
        assertEquals(450.0, payment.getAmount(), 0.01);
        assertFalse(payment.isPaid()); // Initially unpaid
    }
    
    @Test
    void testGetPaymentId() {
        assertEquals("PAY001", payment.getPaymentId());
    }
    
    @Test
    void testGetRental() {
        assertEquals(rental, payment.getRental());
    }
    
    @Test
    void testGetAmount() {
        assertEquals(450.0, payment.getAmount(), 0.01);
    }
    
    @Test
    void testIsPaid_InitiallyFalse() {
        assertFalse(payment.isPaid());
    }
    
    @Test
    void testSetPaid() {
        // Initially false
        assertFalse(payment.isPaid());
        
        // Mark as paid
        payment.setPaid(true);
        assertTrue(payment.isPaid());
        
        // Mark as unpaid again
        payment.setPaid(false);
        assertFalse(payment.isPaid());
    }
    
    @Test
    void testProcessPayment_ChangesPaidStatus() {
        // Initially unpaid
        assertFalse(payment.isPaid());
        
        // Process payment
        payment.processPayment();
        
        // Now paid
        assertTrue(payment.isPaid());
    }
    
    @Test
    void testProcessPayment_PrintsConfirmation() {
        // Act: Process payment (output captured)
        payment.processPayment();
        
        // Assert: Check console output
        String output = outputStream.toString();
        assertTrue(output.contains("Payment processed successfully!"));
        assertTrue(output.contains("PAY001"));
        assertTrue(output.contains("450.0"));
    }
    
    @Test
    void testPaymentLinkedToRental() {
        // Payment HAS-A Rental
        assertNotNull(payment.getRental());
        
        // Can access rental details through payment
        assertEquals("R001", payment.getRental().getRentalId());
        assertEquals(customer, payment.getRental().getCustomer());
        assertEquals(tesla, payment.getRental().getCar());
    }
    
    @Test
    void testPaymentAmountMatchesRentalFee() {
        // Payment amount should equal rental total fee
        assertEquals(rental.getTotalFee(), payment.getAmount(), 0.01);
    }
    
    @Test
    void testMultiplePayments() {
        // Create different rentals and payments
        Car bmw = new GasCar("G001", "BMW X5", 150.0, "Diesel");
        Rental rental2 = new Rental("R002", bmw, customer, 7);
        Payment payment2 = new Payment("PAY002", rental2, rental2.getTotalFee());
        
        // Each payment is independent
        assertNotEquals(payment.getPaymentId(), payment2.getPaymentId());
        assertNotEquals(payment.getRental(), payment2.getRental());
        assertNotEquals(payment.getAmount(), payment2.getAmount());
        
        // Verify amounts
        assertEquals(450.0, payment.getAmount(), 0.01);      // Tesla electric
        assertEquals(1207.50, payment2.getAmount(), 0.01);   // BMW diesel
    }
    
    @Test
    void testPaymentWorkflow() {
        // Simulate real payment workflow
        
        // 1. Payment created (unpaid)
        Payment newPayment = new Payment("PAY001", rental, rental.getTotalFee());
        assertFalse(newPayment.isPaid());
        
        // 2. Verify amount before processing
        assertEquals(450.0, newPayment.getAmount(), 0.01);
        
        // 3. Process payment
        newPayment.processPayment();
        
        // 4. Verify payment completed
        assertTrue(newPayment.isPaid());
        
        // 5. Amount unchanged after processing
        assertEquals(450.0, newPayment.getAmount(), 0.01);
    }
    
    @Test
    void testPaymentWithDifferentCarTypes() {
        // Test payments for all car types
        
        // Electric car payment
        Car electric = new ElectricCar("E001", "Tesla", 100.0, 75.0);
        Rental electricRental = new Rental("R001", electric, customer, 5);
        Payment electricPayment = new Payment("PAY001", electricRental, electricRental.getTotalFee());
        
        // Diesel car payment
        Car diesel = new GasCar("G001", "BMW", 150.0, "Diesel");
        Rental dieselRental = new Rental("R002", diesel, customer, 5);
        Payment dieselPayment = new Payment("PAY002", dieselRental, dieselRental.getTotalFee());
        
        // Gasoline car payment
        Car gasoline = new GasCar("G002", "Toyota", 80.0, "Gasoline");
        Rental gasolineRental = new Rental("R003", gasoline, customer, 5);
        Payment gasolinePayment = new Payment("PAY003", gasolineRental, gasolineRental.getTotalFee());
        
        // Verify different amounts (polymorphism reflected in payments)
        assertEquals(450.0, electricPayment.getAmount(), 0.01);    // 5 × 100 × 0.9
        assertEquals(862.50, dieselPayment.getAmount(), 0.01);     // 5 × 150 × 1.15
        assertEquals(400.0, gasolinePayment.getAmount(), 0.01);    // 5 × 80 × 1.0
    }
    
    @Test
    void testComposition_TransitiveRelationship() {
        // Payment → Rental → Car (transitive)
        // Payment → Rental → Customer (transitive)
        
        // Access car through payment
        Car carThroughPayment = payment.getRental().getCar();
        assertEquals("E001", carThroughPayment.getId());
        
        // Access customer through payment
        Customer customerThroughPayment = payment.getRental().getCustomer();
        assertEquals("John Smith", customerThroughPayment.getName());
    }
    
    @Test
    void testProcessPaymentIdempotence() {
        // Processing payment multiple times should be safe
        
        assertFalse(payment.isPaid());
        
        // First process
        payment.processPayment();
        assertTrue(payment.isPaid());
        
        // Second process (already paid)
        payment.processPayment();
        assertTrue(payment.isPaid()); // Still paid
        
        // Amount unchanged
        assertEquals(450.0, payment.getAmount(), 0.01);
    }
}