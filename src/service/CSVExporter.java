package service;

import model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CSVExporter {

    // New format headers (to support persistence)
    private static final String CARS_HEADER =
            "CarID,Brand,Type,PricePerDay,Available,BatteryCapacity,FuelType";

    private static final String RENTALS_HEADER =
            "RentalID,CarID,CustomerID,CustomerName,CustomerPhone,Days,Returned,TotalFee";

    // ===================== PUBLIC API =====================

    // Save (Cars)
    public static void saveCars(CarInventory inventory, String fileName) throws IOException {
        exportCarsToCSV(inventory.getAllCars(), fileName);
    }

    // Save (Rentals)
    public static void saveRentals(CarInventory inventory, String fileName) throws IOException {
        exportRentalsToCSV(inventory.getAllRentals(), fileName);
    }

    // Load (Cars)
    public static int loadCarsIntoInventory(CarInventory inventory, String fileName) throws IOException {
        Path path = Path.of(fileName);
        if (!Files.exists(path)) return 0;

        int loaded = 0;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String header = br.readLine();
            if (header == null) return 0;

            // Accept only the new format header (strict & professional)
            if (!header.trim().equalsIgnoreCase(CARS_HEADER)) {
                throw new IOException("cars.csv header format is invalid or outdated. Expected: " + CARS_HEADER);
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                List<String> cols = parseCSVLine(line);
                if (cols.size() < 7) continue;

                String carId = cols.get(0);
                String brand = cols.get(1);
                String type = cols.get(2);
                double pricePerDay = parseDoubleSafe(cols.get(3), 0.0);
                boolean available = parseBooleanYesNo(cols.get(4), true);
                double batteryCapacity = parseDoubleSafe(cols.get(5), 0.0);
                String fuelType = cols.get(6);

                Car car;
                if ("Electric".equalsIgnoreCase(type)) {
                    car = new ElectricCar(carId, brand, pricePerDay, batteryCapacity);
                } else if ("Gas".equalsIgnoreCase(type)) {
                    car = new GasCar(carId, brand, pricePerDay, fuelType);
                } else {
                    // Unknown type: skip
                    continue;
                }

                // Override availability based on stored value
                car.setAvailable(available);

                inventory.addCarFromStorage(car);
                loaded++;
            }
        }

        return loaded;
    }

    // Load (Rentals)
    public static int loadRentalsIntoInventory(CarInventory inventory, String fileName) throws IOException {
        Path path = Path.of(fileName);
        if (!Files.exists(path)) return 0;

        int loaded = 0;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String header = br.readLine();
            if (header == null) return 0;

            if (!header.trim().equalsIgnoreCase(RENTALS_HEADER)) {
                throw new IOException("rentals.csv header format is invalid or outdated. Expected: " + RENTALS_HEADER);
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                List<String> cols = parseCSVLine(line);
                if (cols.size() < 8) continue;

                String rentalId = cols.get(0);
                String carId = cols.get(1);
                String customerId = cols.get(2);
                String customerName = cols.get(3);
                String customerPhone = cols.get(4);
                int days = parseIntSafe(cols.get(5), 1);
                boolean returned = parseBooleanYesNo(cols.get(6), false);

                // TotalFee is stored for report/readability, but we recompute from Car anyway
                // double storedTotalFee = parseDoubleSafe(cols.get(7), 0.0);

                Car car = inventory.findCarById(carId);
                if (car == null) {
                    // If car not found, skip this rental (data integrity rule)
                    continue;
                }

                Customer customer = new Customer(customerId, customerName, customerPhone);
                Rental rental = new Rental(rentalId, car, customer, days);
                rental.setReturned(returned);

                // Ensure car state matches rental state
                if (!returned) {
                    car.setAvailable(false);
                }

                inventory.addRentalFromStorage(rental);
                loaded++;
            }
        }

        return loaded;
    }

    // ===================== REPORT EXPORT (SAME CLASS) =====================

    public static void exportCarsToCSV(Collection<Car> cars, String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {

            writer.append(CARS_HEADER).append("\n");

            for (Car car : cars) {
                String carId = escapeCSV(car.getId());
                String brand = escapeCSV(car.getBrand());
                String type;
                String batteryCapacity = "";
                String fuelType = "";

                if (car instanceof ElectricCar) {
                    type = "Electric";
                    batteryCapacity = String.valueOf(((ElectricCar) car).getBatteryCapacity());
                } else if (car instanceof GasCar) {
                    type = "Gas";
                    fuelType = ((GasCar) car).getFuelType();
                } else {
                    type = "Unknown";
                }

                writer.append(carId).append(",");
                writer.append(brand).append(",");
                writer.append(escapeCSV(type)).append(",");
                writer.append(String.valueOf(car.getPricePerDay())).append(",");
                writer.append(car.isAvailable() ? "Yes" : "No").append(",");
                writer.append(escapeCSV(batteryCapacity)).append(",");
                writer.append(escapeCSV(fuelType)).append("\n");
            }
        }
    }

    public static void exportRentalsToCSV(List<Rental> rentals, String fileName) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {

            writer.append(RENTALS_HEADER).append("\n");

            for (Rental rental : rentals) {
                writer.append(escapeCSV(rental.getRentalId())).append(",");
                writer.append(escapeCSV(rental.getCar().getId())).append(",");
                writer.append(escapeCSV(rental.getCustomer().getCustomerId())).append(",");
                writer.append(escapeCSV(rental.getCustomer().getName())).append(",");
                writer.append(escapeCSV(rental.getCustomer().getPhone())).append(",");
                writer.append(String.valueOf(rental.getDays())).append(",");
                writer.append(rental.isReturned() ? "Yes" : "No").append(",");
                writer.append(String.valueOf(rental.getTotalFee())).append("\n");
            }
        }
    }

    // ===================== HELPERS =====================

    private static String escapeCSV(String value) {
        if (value == null) return "";
        String v = value;
        boolean mustQuote = v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r");
        v = v.replace("\"", "\"\"");
        return mustQuote ? "\"" + v + "\"" : v;
    }

    // Minimal CSV parser for one line (supports quoted values)
    private static List<String> parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '\"') {
                // if double quote inside quoted field -> escaped quote
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    sb.append('\"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString());
        return result;
    }

    private static boolean parseBooleanYesNo(String s, boolean defaultValue) {
        if (s == null) return defaultValue;
        String v = s.trim().toLowerCase();
        if (v.equals("yes") || v.equals("true")) return true;
        if (v.equals("no") || v.equals("false")) return false;
        return defaultValue;
    }

    private static double parseDoubleSafe(String s, double defaultValue) {
        try {
            return Double.parseDouble(s.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static int parseIntSafe(String s, int defaultValue) {
        try {
            return Integer.parseInt(s.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
