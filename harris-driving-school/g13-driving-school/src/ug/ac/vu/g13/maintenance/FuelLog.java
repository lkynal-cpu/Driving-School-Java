package ug.ac.vu.g13.maintenance;

import java.time.LocalDate;

import ug.ac.vu.g13.core.BaseRecord;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.vehicles.Vehicle;

/**
 * A fuel top-up for a training vehicle.
 *
 * @author Member 8 (Maintenance module owner)
 */
public class FuelLog extends BaseRecord {
    private final Vehicle vehicle;
    private final LocalDate date;
    private final double litres;
    private final double cost;
    private final int odometerKm;

    public FuelLog(String id, Vehicle vehicle, LocalDate date, double litres, double cost, int odometerKm) {
        super(id);
        this.vehicle = Validate.notNull(vehicle, "Vehicle");
        this.date = Validate.date(date, "Fuel date", LocalDate.of(2000, 1, 1), LocalDate.now().plusDays(1));
        this.litres = Validate.doubleRange(litres, "Litres", 0.5, 200);
        this.cost = Validate.doubleRange(cost, "Fuel cost (UGX)", 500, 2_000_000);
        this.odometerKm = Validate.intRange(odometerKm, "Odometer reading", 0, 2_000_000);
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getLitres() {
        return litres;
    }

    public double getCost() {
        return cost;
    }

    public int getOdometerKm() {
        return odometerKm;
    }

    @Override
    public String describe() {
        return String.format("%s | Fuel | %s %s | %s | %.1f L | %s | odometer %,d km", getId(), vehicle.typeLabel(),
                vehicle.getPlate(), date, litres, Validate.money(cost), odometerKm);
    }

    @Override
    public String toFileLine() {
        return Fields.join(getId(), vehicle.getId(), date, litres, cost, odometerKm);
    }
}
