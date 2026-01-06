package service;

import model.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class CSVExporter {

    // ===================== EXPORT CARS =====================
    public static void exportCarsToCSV(Collection<Car> cars, String fileName) throws IOException {

        try (FileWriter writer = new FileWriter(fileName)) {

            writer.append("Car ID,Brand,Type,Price Per Day,Available,Extra Info\n");

            for (Car car : cars) {
                writer.append(car.getId()).append(",");
                writer.append(car.getBrand()).append(",");

                if (car instanceof ElectricCar) {
                    ElectricCar eCar = (ElectricCar) car;
                    writer.append("Electric").append(",");
                    writer.append(String.valueOf(car.getPricePerDay())).append(",");
                    writer.append(car.isAvailable() ? "Yes" : "No").append(",");
                    writer.append("Battery: ").append(String.valueOf(eCar.getBatteryCapacity())).append(" kWh");
                } else if (car instanceof GasCar) {
                    GasCar gCar = (GasCar) car;
                    writer.append("Gas").append(",");
                    writer.append(String.valueOf(car.getPricePerDay())).append(",");
                    writer.append(car.isAvailable() ? "Yes" : "No").append(",");
                    writer.append("Fuel: ").append(gCar.getFuelType());
                }

                writer.append("\n");
            }
        }
    }

    // ===================== EXPORT RENTALS =====================
    public static void exportRentalsToCSV(List<Rental> rentals, String fileName) throws IOException {

        try (FileWriter writer = new FileWriter(fileName)) {

            writer.append("Rental ID,Customer Name,Car Brand,Days,Total Fee,Returned\n");

            for (Rental rental : rentals) {
                writer.append(rental.getRentalId()).append(",");
                writer.append(rental.getCustomer().getName()).append(",");
                writer.append(rental.getCar().getBrand()).append(",");
                writer.append(String.valueOf(rental.getDays())).append(",");
                writer.append(String.valueOf(rental.getTotalFee())).append(",");
                writer.append(rental.isReturned() ? "Yes" : "No").append("\n");
            }
        }
    }
}
