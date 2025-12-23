// ============== SEARCH & FILTER METHODS (OPTIONAL) ==============
    
    // Search cars by brand
    public List<Car> searchByBrand(String brand) {
        List<Car> results = new ArrayList<>();
        for (Car car : cars.values()) {
            if (car.getBrand().equalsIgnoreCase(brand) && car.isAvailable()) {
                results.add(car);
            }
        }
        return results;
    }
    
    // Search gas cars by fuel type
    public List<Car> searchByFuelType(String fuelType) {
        List<Car> results = new ArrayList<>();
        for (Car car : cars.values()) {
            if (car instanceof GasCar && car.isAvailable()) {
                GasCar gasCar = (GasCar) car;
                if (gasCar.getFuelType().equalsIgnoreCase(fuelType)) {
                    results.add(car);
                }
            }
        }
        return results;
    }
    
    // Get all rentals
    public List<Rental> getAllRentals() {
        return new ArrayList<>(rentals);
    }
}