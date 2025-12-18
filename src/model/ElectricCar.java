package model;

public class ElectricCar extends Car {
    // Additional field specific to electric cars
    private double batteryCapacity;
    
    // Constructor
    public ElectricCar(String id, String brand, double pricePerDay, double batteryCapacity) {
        super(id, brand, pricePerDay, true); // true = available initially
        this.batteryCapacity = batteryCapacity;
    }
    
    // Getter for battery capacity
    public double getBatteryCapacity() {
        return batteryCapacity;
    }
    
    // Override the abstract method from Car
    @Override
    public double calculateRentalFee(int days) {
        // Electric cars get 10% discount (eco-friendly pricing)
        return days * getPricePerDay() * 0.9;
    }
}