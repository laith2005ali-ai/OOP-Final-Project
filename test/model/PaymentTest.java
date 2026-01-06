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
    
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    @BeforeEach
    void setUp() {
        tesla = new ElectricCar("E001", "Tesla Model 3", 100.0, 75.0);
        customer = new Customer("CUST001", "John Smith", "555-1234");
        rental = new Rental("R001", tesla, customer, 5);
        payment = new Payment("PAY001", rental, rental.getTotalFee());
        
        System.setOut(new PrintStream(outputStream));
    }
    
    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }
    
    @Test
    void testConstructor() {
        assertEquals("PAY001", payment.getPaymentId());
        assertEquals(rental, payment.getRental());
        assertEquals(450.0, payment.getAmount(), 0.01);
        assertFalse(payment.isPaid());
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
        assertFalse(payment.isPaid());
        
        payment.setPaid(true);
        assertTrue(payment.isPaid());
        
        payment.setPaid(false);
        assertFalse(payment.isPaid());
    }
    
    @Test
    void testProcessPayment_ChangesPaidStatus() {
        assertFalse(payment.isPaid());
        payment.processPayment();
        assertTrue(payment.isPaid());
    }
    
    @Test
    void testProcessPayment_PrintsConfirmation() {
        payment.processPayment();
        
        String output = outputStream.toString();
        assertTrue(output.contains("Payment processed successfully!"));
        assertTrue(output.contains("PAY001"));
        assertTrue(output.contains("450.0"));
    }
    
    @Test
    void testPaymentLinkedToRental() {
        assertNotNull(payment.getRental());
        assertEquals("R001", payment.getRental().getRentalId());
        assertEquals(customer, payment.getRental().getCustomer());
        assertEquals(tesla, payment.getRental().getCar());
    }
    
    @Test
    void testPaymentAmountMatchesRentalFee() {
        assertEquals(rental.getTotalFee(), payment.getAmount(), 0.01);
    }
    
    @Test
    void testMultiplePayments() {
        Car bmw = new GasCar("G001", "BMW X5", 150.0, "Diesel");
        Rental rental2 = new Rental("R002", bmw, customer, 7);
        Payment payment2 = new Payment("PAY002", rental2, rental2.getTotalFee());
        
        assertNotEquals(payment.getPaymentId(), payment2.getPaymentId());
        assertNotEquals(payment.getRental(), payment2.getRental());
        assertNotEquals(payment.getAmount(), payment2.getAmount());
        
        assertEquals(450.0, payment.getAmount(), 0.01);
        assertEquals(1207.50, payment2.getAmount(), 0.01);
    }
    
    @Test
    void testPaymentWorkflow() {
        Payment newPayment = new Payment("PAY001", rental, rental.getTotalFee());
        assertFalse(newPayment.isPaid());
        
        assertEquals(450.0, newPayment.getAmount(), 0.01);
        
        newPayment.processPayment();
        assertTrue(newPayment.isPaid());
        
        assertEquals(450.0, newPayment.getAmount(), 0.01);
    }
    
    @Test
    void testPaymentWithDifferentCarTypes() {
        Car electric = new ElectricCar("E001", "Tesla", 100.0, 75.0);
        Rental electricRental = new Rental("R001", electric, customer, 5);
        Payment electricPayment = new Payment("PAY001", electricRental, electricRental.getTotalFee());
        
        Car diesel = new GasCar("G001", "BMW", 150.0, "Diesel");
        Rental dieselRental = new Rental("R002", diesel, customer, 5);
        Payment dieselPayment = new Payment("PAY002", dieselRental, dieselRental.getTotalFee());
        
        Car gasoline = new GasCar("G002", "Toyota", 80.0, "Gasoline");
        Rental gasolineRental = new Rental("R003", gasoline, customer, 5);
        Payment gasolinePayment = new Payment("PAY003", gasolineRental, gasolineRental.getTotalFee());
        
        assertEquals(450.0, electricPayment.getAmount(), 0.01);
        assertEquals(862.50, dieselPayment.getAmount(), 0.01);
        assertEquals(400.0, gasolinePayment.getAmount(), 0.01);
    }
    
    @Test
    void testComposition_TransitiveRelationship() {
        Car carThroughPayment = payment.getRental().getCar();
        assertEquals("E001", carThroughPayment.getId());
        
        Customer customerThroughPayment = payment.getRental().getCustomer();
        assertEquals("John Smith", customerThroughPayment.getName());
    }
    
    @Test
    void testProcessPaymentIdempotence() {
        assertFalse(payment.isPaid());
        
        payment.processPayment();
        assertTrue(payment.isPaid());
        
        payment.processPayment();
        assertTrue(payment.isPaid());
        
        assertEquals(450.0, payment.getAmount(), 0.01);
    }
}