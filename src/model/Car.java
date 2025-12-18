package model;

public abstract class Car implements Rentable {
    // Fields (encapsulated with private)
    private String id;
    private String brand;
    private double pricePerDay;
    private boolean available;
    
    // Constructor
    public Car(String id, String brand, double pricePerDay, boolean available) {
        this.id = id;
        this.brand = brand;
        this.pricePerDay = pricePerDay;
        this.available = available;
    }
    
    // Getters for all fields
    public String getId() {
        return id;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public double getPricePerDay() {
        return pricePerDay;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    // Setters for fields that can change
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }
    
    // Abstract method - must be implemented by subclasses
    @Override
    public abstract double calculateRentalFee(int days);
}