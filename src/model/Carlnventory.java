// ============== RENTAL MANAGEMENT METHODS ==============
    
    // Rent a car
    public Rental rentCar(String carId, Customer customer, int days) {
        // Find the car
        Car car = findCarById(carId);
        
        // Validation: car exists
        if (car == null) {
            System.out.println("ERROR: Car not found with ID: " + carId);
            return null;
        }
        
        // Validation: car is available
        if (!car.isAvailable()) {
            System.out.println("ERROR: Car is not available (already rented).");
            return null;
        }
        
        // Validation: days is positive
        if (days <= 0) {
            System.out.println("ERROR: Rental days must be positive.");
            return null;
        }
        
        // Create rental
        String rentalId = "R" + (rentals.size() + 1);
        Rental rental = new Rental(rentalId, car, customer, days);
        
        // Mark car as unavailable
        car.setAvailable(false);
        
        // Add to rentals list
        rentals.add(rental);
        
        // Success message
        System.out.println("\n===== RENTAL SUCCESSFUL =====");
        System.out.println("Rental ID: " + rental.getRentalId());
        System.out.println("Customer: " + customer.getName());
        System.out.println("Car: " + car.getBrand() + " (" + car.getId() + ")");
        System.out.println("Days: " + days);
        System.out.println("Total Fee: $" + rental.getTotalFee());
        System.out.println("==============================\n");
        
        return rental;
    }
    
    // Return a car
    public void returnCar(String rentalId) {
        // Find the rental
        Rental rental = null;
        for (Rental r : rentals) {
            if (r.getRentalId().equals(rentalId)) {
                rental = r;
                break;
            }
        }
        
        // Validation: rental exists
        if (rental == null) {
            System.out.println("ERROR: Rental not found with ID: " + rentalId);
            return;
        }
        
        // Validation: not already returned
        if (rental.isReturned()) {
            System.out.println("ERROR: This rental has already been returned.");
            return;
        }
        
        // Mark rental as returned
        rental.setReturned(true);
        
        // Mark car as available again
        rental.getCar().setAvailable(true);
        
        // Success message
        System.out.println("\n===== CAR RETURNED =====");
        System.out.println("Rental ID: " + rental.getRentalId());
        System.out.println("Car: " + rental.getCar().getBrand() + " (" + rental.getCar().getId() + ")");
        System.out.println("Customer: " + rental.getCustomer().getName());
        System.out.println("Total Fee: $" + rental.getTotalFee());
        System.out.println("========================\n");
    }