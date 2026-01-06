package model;

public class Payment {
    // Fields
    private String paymentId;
    private Rental rental;
    private double amount;
    private boolean paid;
    
    // Constructor
    public Payment(String paymentId, Rental rental, double amount) {
        this.paymentId = paymentId;
        this.rental = rental;
        this.amount = amount;
        this.paid = false; // Initially unpaid
    }
    
    // Getters
    public String getPaymentId() {
        return paymentId;
    }
    
    public Rental getRental() {
        return rental;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public boolean isPaid() {
        return paid;
    }
    
    // Setter for paid status
    public void setPaid(boolean paid) {
        this.paid = paid;
    }
    
    // Business method - processes the payment
    public void processPayment() {
        this.paid = true;
        System.out.println("Payment processed successfully!");
        System.out.println("Payment ID: " + paymentId);
        System.out.println("Amount: $" + amount);
    }
}