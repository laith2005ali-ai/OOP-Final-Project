package model;

public class Rental {

    // ===================== Fields =====================
    private String rentalId;
    private Car car;
    private Customer customer;
    private int days;
    private boolean returned;

    // ===================== Constructor =====================
    public Rental(String rentalId, Car car, Customer customer, int days) {
        this.rentalId = rentalId;
        this.car = car;
        this.customer = customer;
        this.days = days;
        this.returned = false; // Initially not returned
    }

    // ===================== Getters =====================
    public String getRentalId() {
        return rentalId;
    }

    public Car getCar() {
        return car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getDays() {
        return days;
    }

    public boolean isReturned() {
        return returned;
    }

    // ===================== Setter =====================
    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    // ===================== Business Logic =====================
    // Used in: CarInventory, Payment, CSVExporter
    public double getTotalFee() {
        return car.calculateRentalFee(days);
    }
}
