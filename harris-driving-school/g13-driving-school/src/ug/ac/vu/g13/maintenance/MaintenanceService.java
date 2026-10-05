package ug.ac.vu.g13.maintenance;

import java.time.LocalDate;
import java.util.Comparator;

import ug.ac.vu.g13.core.BaseService;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;
import ug.ac.vu.g13.vehicles.Vehicle;
import ug.ac.vu.g13.vehicles.VehicleService;

/**
 * Manages maintenance jobs. Opening a job takes the vehicle out of service;
 * closing the last open job puts it back.
 *
 * @author Member 8 (Maintenance module owner)
 */
public class MaintenanceService extends BaseService<MaintenanceJob> {
    public static final Comparator<MaintenanceJob> BY_COST_DESC = Comparator.comparingDouble(MaintenanceJob::getCost)
            .reversed().thenComparing(MaintenanceJob::getId);
    public static final Comparator<MaintenanceJob> BY_REPORTED = Comparator.comparing(MaintenanceJob::getReportedOn)
            .thenComparing(MaintenanceJob::getId);

    private final VehicleService vehicles;

    public MaintenanceService(VehicleService vehicles) {
        super("maintenance.txt", "Maintenance job");
        this.vehicles = vehicles;
        load();
        addRemovalGuard(j -> j.getStatus() == MaintenanceJob.Status.CLOSED ? null : "the job is still " + j.getStatus());
    }

    @Override
    protected MaintenanceJob fromFileLine(String line) {
        String[] f = Fields.split(line, 7);
        LocalDate closed = f[6].isEmpty() ? null : LocalDate.parse(f[6]);
        return new MaintenanceJob(f[0], vehicles.require(f[1]), f[2], LocalDate.parse(f[3]), Double.parseDouble(f[4]),
                MaintenanceJob.Status.valueOf(f[5]), closed);
    }

    public MaintenanceJob open(Vehicle vehicle, String description) {
        if (vehicle.getStatus() == Vehicle.Status.RETIRED) {
            throw new InvalidDataException("Vehicle " + vehicle.getPlate() + " is retired.");
        }
        MaintenanceJob job = new MaintenanceJob(nextId('M'), vehicle, description, LocalDate.now(), 0,
                MaintenanceJob.Status.OPEN, null);
        add(job);
        vehicles.update(vehicle.getId(), v -> v.setStatus(Vehicle.Status.IN_MAINTENANCE));
        return job;
    }

    public void start(String id) throws JobAlreadyClosedException {
        MaintenanceJob job = require(id);
        if (job.getStatus() == MaintenanceJob.Status.CLOSED) {
            throw new JobAlreadyClosedException(job);
        }
        update(id, MaintenanceJob::markInProgress);
    }

    public void close(String id, double finalCost) throws JobAlreadyClosedException {
        MaintenanceJob job = require(id);
        if (job.getStatus() == MaintenanceJob.Status.CLOSED) {
            throw new JobAlreadyClosedException(job);
        }
        update(id, j -> j.markClosed(finalCost, LocalDate.now()));
        Vehicle v = job.getVehicle();
        boolean stillOpen = anyMatch(j -> j.getVehicle().equals(v) && j.getStatus() != MaintenanceJob.Status.CLOSED);
        if (!stillOpen && v.getStatus() == Vehicle.Status.IN_MAINTENANCE) {
            vehicles.update(v.getId(), x -> x.setStatus(Vehicle.Status.AVAILABLE));
        }
    }

    public double totalCost(Vehicle v) {
        double sum = 0;
        for (MaintenanceJob j : getAll()) {
            if (j.getVehicle().equals(v)) {
                sum += j.getCost();
            }
        }
        return sum;
    }
}
