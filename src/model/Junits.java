package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {
    
    private Customer customer;
    
    @BeforeEach
    void setUp() {
        customer = new Customer("CUST001", "John Smith", "555-1234");
    }
    
    @Test
    void testConstructor() {
        assertEquals("CUST001", customer.getCustomerId());
        assertEquals("John Smith", customer.getName());
        assertEquals("555-1234", customer.getPhone());
    }
    
    @Test
    void testGetCustomerId() {
        assertEquals("CUST001", customer.getCustomerId());
    }
    
    @Test
    void testGetName() {
        assertEquals("John Smith", customer.getName());
    }
    
    @Test
    void testGetPhone() {
        assertEquals("555-1234", customer.getPhone());
    }
    
    @Test
    void testImmutability() {
        // Verify values don't change on repeated calls
        String id = customer.getCustomerId();
        String name = customer.getName();
        String phone = customer.getPhone();
        
        // Call getters again
        assertEquals(id, customer.getCustomerId());
        assertEquals(name, customer.getName());
        assertEquals(phone, customer.getPhone());
    }
    
    @Test
    void testMultipleCustomers() {
        Customer customer2 = new Customer("CUST002", "Jane Doe", "555-5678");
        Customer customer3 = new Customer("CUST003", "Bob Wilson", "555-9999");
        
        // Verify each customer is independent
        assertNotEquals(customer.getCustomerId(), customer2.getCustomerId());
        assertNotEquals(customer.getName(), customer2.getName());
        assertNotEquals(customer.getPhone(), customer3.getPhone());
    }
    
    @Test
    void testCustomerWithDifferentPhoneFormats() {
        Customer c1 = new Customer("C001", "Alice", "555-1234");
        Customer c2 = new Customer("C002", "Bob", "(555) 123-4567");
        Customer c3 = new Customer("C003", "Charlie", "+1-555-555-5555");
        
        // All formats should be stored as-is
        assertEquals("555-1234", c1.getPhone());
        assertEquals("(555) 123-4567", c2.getPhone());
        assertEquals("+1-555-555-5555", c3.getPhone());
    }
}