package ug.ac.vu.g13.maintenance;

import java.time.LocalDate;

import ug.ac.vu.g13.core.BaseRecord;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;
import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.vehicles.Vehicle;

/**
 * A repair or service job on a training vehicle. Links to Vehicle by object reference.
 *
 * @author Member 8 (Maintenance module owner)
 */
public class MaintenanceJob extends BaseRecord {
    public enum Status { OPEN, IN_PROGRESS, CLOSED }

    private final Vehicle vehicle;
    private String description;
    private final LocalDate reportedOn;
    private double cost;
    private Status status;
    private LocalDate closedOn;

    public MaintenanceJob(String id, Vehicle vehicle, String description, LocalDate reportedOn, double cost,
                          Status status, LocalDate closedOn) {
        super(id);
        this.vehicle = Validate.notNull(vehicle, "Vehicle");
        setDescription(description);
        this.reportedOn = Validate.date(reportedOn, "Reported date", LocalDate.of(2000, 1, 1), LocalDate.now().plusDays(1));
        setCost(cost);
        this.status = Validate.notNull(status, "Status");
        if (status == Status.CLOSED) {
            if (closedOn == null || closedOn.isBefore(reportedOn)) {
                throw new InvalidDataException("A closed job needs a closing date on or after the reported date.");
            }
            this.closedOn = closedOn;
        }
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = Validate.text(description, "Description", 3, 120);
    }

    public LocalDate getReportedOn() {
        return reportedOn;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = Validate.doubleRange(cost, "Cost (UGX)", 0, 50_000_000);
    }

    public Status getStatus() {
        return status;
    }

    public LocalDate getClosedOn() {
        return closedOn;
    }

    void markInProgress() {
        this.status = Status.IN_PROGRESS;
    }

    void markClosed(double finalCost, LocalDate date) {
        setCost(finalCost);
        this.status = Status.CLOSED;
        this.closedOn = date;
    }

    @Override
    public String describe() {
        return String.format("%s | Maintenance | %s %s | %s | %s | cost %s%s", getId(), vehicle.typeLabel(),
                vehicle.getPlate(), description, status, Validate.money(cost), closedOn == null ? "" : " | closed " + closedOn);
    }

    @Override
    public String toFileLine() {
        return Fields.join(getId(), vehicle.getId(), description, reportedOn, cost, status.name(), closedOn);
    }
}
