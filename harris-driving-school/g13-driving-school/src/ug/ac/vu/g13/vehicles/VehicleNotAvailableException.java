package ug.ac.vu.g13.vehicles;

/**
 * Thrown when a vehicle is in maintenance or retired and cannot be used for a lesson.
 *
 * @author Member 3 (Vehicles module owner)
 */
public class VehicleNotAvailableException extends Exception {
    public VehicleNotAvailableException(Vehicle v) {
        super("Vehicle " + v.getId() + " (" + v.getPlate() + ") is not available: " + v.getStatus() + ".");
    }
}
