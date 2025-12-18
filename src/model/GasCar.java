package model;

public class GasCar extends Car {
    // Additional field specific to gas cars
    private String fuelType;
    
    // Constructor
    public GasCar(String id, String brand, double pricePerDay, String fuelType) {
        super(id, brand, pricePerDay, true); // true = available initially
        this.fuelType = fuelType;
    }
    
    // Getter for fuel type
    public String getFuelType() {
        return fuelType;
    }
    
    // Override the abstract method from Car
    @Override
    public double calculateRentalFee(int days) {
        // Gas cars use standard pricing
        // Optional: Add 15% surcharge for Diesel
        if (fuelType.equalsIgnoreCase("Diesel")) {
            return days * getPricePerDay() * 1.15;
        }
        return days * getPricePerDay();
    }
}