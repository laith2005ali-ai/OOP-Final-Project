package model;

public class Customer {
    // Fields
    private String customerId;
    private String name;
    private String phone;
    
    // Constructor
    public Customer(String customerId, String name, String phone) {
        this.customerId = customerId;
        this.name = name;
        this.phone = phone;
    }
    
    // Getters
    public String getCustomerId() {
        return customerId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPhone() {
        return phone;
    }
}