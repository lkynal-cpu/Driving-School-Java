package ug.ac.vu.g13.vehicles;

import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.core.VehicleClass;

/**
 * A training motorcycle.
 *
 * @author Member 3 (Vehicles module owner)
 */
public class Motorcycle extends Vehicle {
    private int engineCc;

    public Motorcycle(String id, String plate, String makeModel, int year, Status status, int odometerKm, int engineCc) {
        super(id, plate, makeModel, year, status, odometerKm);
        setEngineCc(engineCc);
    }

    public int getEngineCc() {
        return engineCc;
    }

    public void setEngineCc(int engineCc) {
        this.engineCc = Validate.intRange(engineCc, "Engine size (cc)", 50, 1500);
    }

    @Override
    public VehicleClass getVehicleClass() {
        return VehicleClass.MOTORCYCLE;
    }

    @Override
    public String typeLabel() {
        return "Motorcycle";
    }

    @Override
    protected String extraField() {
        return String.valueOf(engineCc);
    }

    @Override
    protected String extraDescription() {
        return engineCc + " cc";
    }
}
