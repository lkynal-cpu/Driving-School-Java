package ug.ac.vu.g13.vehicles;

import java.time.LocalDate;

import ug.ac.vu.g13.core.BaseRecord;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;
import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.core.VehicleClass;

/**
 * Abstract parent of every training vehicle. Car, Motorcycle and Truck extend it.
 *
 * @author Member 3 (Vehicles module owner)
 */
public abstract class Vehicle extends BaseRecord {
    public enum Status { AVAILABLE, IN_MAINTENANCE, RETIRED }

    private String plate;
    private String makeModel;
    private int year;
    private Status status;
    private int odometerKm;

    protected Vehicle(String id, String plate, String makeModel, int year, Status status, int odometerKm) {
        super(id);
        setPlate(plate);
        setMakeModel(makeModel);
        setYear(year);
        setStatus(status);
        setOdometerKm(odometerKm);
    }

    public abstract VehicleClass getVehicleClass();

    /** Short name of the kind of vehicle, used in reports. */
    public abstract String typeLabel();

    /** The one extra detail that only this kind of vehicle has (saved to file). */
    protected abstract String extraField();

    /** Human readable form of the extra detail. */
    protected abstract String extraDescription();

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        String p = Validate.text(plate, "Number plate", 5, 10).toUpperCase().replace(" ", "");
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("([A-Z]{2,3})(\\d{3})([A-Z]?)").matcher(p);
        if (!m.matches()) {
            throw new InvalidDataException("Number plate must look like UBA 123A.");
        }
        this.plate = m.group(1) + " " + m.group(2) + m.group(3);
    }

    public String getMakeModel() {
        return makeModel;
    }

    public void setMakeModel(String makeModel) {
        this.makeModel = Validate.text(makeModel, "Make and model", 2, 40);
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = Validate.intRange(year, "Year of manufacture", 1990, LocalDate.now().getYear() + 1);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = Validate.notNull(status, "Status");
    }

    public int getOdometerKm() {
        return odometerKm;
    }

    public void setOdometerKm(int odometerKm) {
        this.odometerKm = Validate.intRange(odometerKm, "Odometer reading", 0, 2_000_000);
    }

    @Override
    public String describe() {
        return String.format("%s | %-10s | %-9s | %-18s | %d | %,d km | %s | %s",
                getId(), typeLabel(), plate, makeModel, year, odometerKm, extraDescription(), status);
    }

    @Override
    public String toFileLine() {
        return Fields.join(getVehicleClass().name(), getId(), plate, makeModel, year, status.name(), odometerKm, extraField());
    }
}
