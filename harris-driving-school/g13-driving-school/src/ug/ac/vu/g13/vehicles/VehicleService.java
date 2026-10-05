package ug.ac.vu.g13.vehicles;

import java.util.Comparator;

import ug.ac.vu.g13.core.BaseService;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;
import ug.ac.vu.g13.core.VehicleClass;

/**
 * Manages the fleet of training vehicles.
 *
 * @author Member 3 (Vehicles module owner)
 */
public class VehicleService extends BaseService<Vehicle> {
    public static final Comparator<Vehicle> BY_YEAR_NEWEST = Comparator.comparingInt(Vehicle::getYear).reversed()
            .thenComparing(Vehicle::getPlate);
    public static final Comparator<Vehicle> BY_ODOMETER_DESC = Comparator.comparingInt(Vehicle::getOdometerKm).reversed();
    public static final Comparator<Vehicle> BY_PLATE = Comparator.comparing(Vehicle::getPlate);

    public VehicleService() {
        super("vehicles.txt", "Vehicle");
        load();
    }

    @Override
    protected Vehicle fromFileLine(String line) {
        String[] f = Fields.split(line, 8);
        VehicleClass vc = VehicleClass.valueOf(f[0]);
        Vehicle.Status st = Vehicle.Status.valueOf(f[5]);
        int year = Integer.parseInt(f[4]);
        int odo = Integer.parseInt(f[6]);
        switch (vc) {
            case CAR:
                return new Car(f[1], f[2], f[3], year, st, odo, Car.Transmission.valueOf(f[7]));
            case MOTORCYCLE:
                return new Motorcycle(f[1], f[2], f[3], year, st, odo, Integer.parseInt(f[7]));
            default:
                return new Truck(f[1], f[2], f[3], year, st, odo, Double.parseDouble(f[7]));
        }
    }

    public Car addCar(String plate, String makeModel, int year, int odometer, Car.Transmission transmission) {
        Car c = new Car(nextId('V'), plate, makeModel, year, Vehicle.Status.AVAILABLE, odometer, transmission);
        checkPlateFree(c);
        add(c);
        return c;
    }

    public Motorcycle addMotorcycle(String plate, String makeModel, int year, int odometer, int engineCc) {
        Motorcycle m = new Motorcycle(nextId('V'), plate, makeModel, year, Vehicle.Status.AVAILABLE, odometer, engineCc);
        checkPlateFree(m);
        add(m);
        return m;
    }

    public Truck addTruck(String plate, String makeModel, int year, int odometer, double loadTons) {
        Truck t = new Truck(nextId('V'), plate, makeModel, year, Vehicle.Status.AVAILABLE, odometer, loadTons);
        checkPlateFree(t);
        add(t);
        return t;
    }

    private void checkPlateFree(Vehicle v) {
        if (anyMatch(x -> x.getPlate().equals(v.getPlate()))) {
            throw new InvalidDataException("A vehicle with plate " + v.getPlate() + " already exists.");
        }
    }

    /** Finds by ID first, then by number plate. Returns null if neither matches. */
    public Vehicle findByIdOrPlate(String text) {
        Vehicle v = find(text);
        if (v != null) {
            return v;
        }
        String wanted = text.toUpperCase().replace(" ", "");
        for (Vehicle x : getAll()) {
            if (x.getPlate().replace(" ", "").equals(wanted)) {
                return x;
            }
        }
        return null;
    }

    /** Only AVAILABLE vehicles may be used for lessons. */
    public void ensureUsable(Vehicle v) throws VehicleNotAvailableException {
        if (v.getStatus() != Vehicle.Status.AVAILABLE) {
            throw new VehicleNotAvailableException(v);
        }
    }
}
