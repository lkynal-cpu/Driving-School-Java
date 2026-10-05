package ug.ac.vu.g13.vehicles;

import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.core.VehicleClass;

/**
 * A training car.
 *
 * @author Member 3 (Vehicles module owner)
 */
public class Car extends Vehicle {
    public enum Transmission { MANUAL, AUTOMATIC }

    private Transmission transmission;

    public Car(String id, String plate, String makeModel, int year, Status status, int odometerKm, Transmission transmission) {
        super(id, plate, makeModel, year, status, odometerKm);
        setTransmission(transmission);
    }

    public Transmission getTransmission() {
        return transmission;
    }

    public void setTransmission(Transmission transmission) {
        this.transmission = Validate.notNull(transmission, "Transmission");
    }

    @Override
    public VehicleClass getVehicleClass() {
        return VehicleClass.CAR;
    }

    @Override
    public String typeLabel() {
        return "Car";
    }

    @Override
    protected String extraField() {
        return transmission.name();
    }

    @Override
    protected String extraDescription() {
        return transmission.name().toLowerCase();
    }
}
