package ug.ac.vu.g13.vehicles;

import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.core.VehicleClass;

/**
 * A training truck.
 *
 * @author Member 3 (Vehicles module owner)
 */
public class Truck extends Vehicle {
    private double loadTons;

    public Truck(String id, String plate, String makeModel, int year, Status status, int odometerKm, double loadTons) {
        super(id, plate, makeModel, year, status, odometerKm);
        setLoadTons(loadTons);
    }

    public double getLoadTons() {
        return loadTons;
    }

    public void setLoadTons(double loadTons) {
        this.loadTons = Validate.doubleRange(loadTons, "Load capacity (tons)", 1, 40);
    }

    @Override
    public VehicleClass getVehicleClass() {
        return VehicleClass.TRUCK;
    }

    @Override
    public String typeLabel() {
        return "Truck";
    }

    @Override
    protected String extraField() {
        return String.valueOf(loadTons);
    }

    @Override
    protected String extraDescription() {
        return loadTons + " t";
    }
}
