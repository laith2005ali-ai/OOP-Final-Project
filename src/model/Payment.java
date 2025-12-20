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
}