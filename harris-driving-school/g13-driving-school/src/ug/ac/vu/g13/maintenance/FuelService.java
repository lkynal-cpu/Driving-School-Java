package ug.ac.vu.g13.maintenance;

import java.time.LocalDate;
import java.util.Comparator;

import ug.ac.vu.g13.core.BaseService;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;
import ug.ac.vu.g13.vehicles.Vehicle;
import ug.ac.vu.g13.vehicles.VehicleService;

/**
 * Manages fuel logs and keeps the vehicle odometer up to date.
 *
 * @author Member 8 (Maintenance module owner)
 */
public class FuelService extends BaseService<FuelLog> {
    public static final Comparator<FuelLog> BY_DATE_DESC = Comparator.comparing(FuelLog::getDate).reversed()
            .thenComparing(FuelLog::getId);

    private final VehicleService vehicles;

    public FuelService(VehicleService vehicles) {
        super("fuel_logs.txt", "Fuel log");
        this.vehicles = vehicles;
        load();
    }

    @Override
    protected FuelLog fromFileLine(String line) {
        String[] f = Fields.split(line, 6);
        return new FuelLog(f[0], vehicles.require(f[1]), LocalDate.parse(f[2]), Double.parseDouble(f[3]),
                Double.parseDouble(f[4]), Integer.parseInt(f[5]));
    }

    public FuelLog log(Vehicle vehicle, double litres, double cost, int odometerKm) {
        if (odometerKm < vehicle.getOdometerKm()) {
            throw new InvalidDataException("Odometer " + odometerKm + " km is lower than the recorded "
                    + vehicle.getOdometerKm() + " km for " + vehicle.getPlate() + ".");
        }
        FuelLog log = new FuelLog(nextId('F'), vehicle, LocalDate.now(), litres, cost, odometerKm);
        add(log);
        vehicles.update(vehicle.getId(), v -> v.setOdometerKm(odometerKm));
        return log;
    }

    public double totalCost(Vehicle v) {
        double sum = 0;
        for (FuelLog l : getAll()) {
            if (l.getVehicle().equals(v)) {
                sum += l.getCost();
            }
        }
        return sum;
    }
}
