package ug.ac.vu.g13.maintenance;

import java.util.ArrayList;
import java.util.List;

import ug.ac.vu.g13.core.AbstractModuleMenu;
import ug.ac.vu.g13.core.InputHelper;
import ug.ac.vu.g13.core.Menu;
import ug.ac.vu.g13.core.RecordNotFoundException;
import ug.ac.vu.g13.core.Reportable;
import ug.ac.vu.g13.core.Validate;
import ug.ac.vu.g13.vehicles.Vehicle;
import ug.ac.vu.g13.vehicles.VehicleService;

/**
 * Submenu for the Maintenance and Fuel module.
 *
 * @author Member 8 (Maintenance module owner)
 */
public class MaintenanceMenu extends AbstractModuleMenu {
    private final MaintenanceService jobs;
    private final FuelService fuel;
    private final VehicleService vehicles;

    public MaintenanceMenu(MaintenanceService jobs, FuelService fuel, VehicleService vehicles) {
        this.jobs = jobs;
        this.fuel = fuel;
        this.vehicles = vehicles;
    }

    @Override
    public String getName() {
        return "Maintenance and Fuel";
    }

    @Override
    protected String[] options() {
        return new String[] {
            "Open a maintenance job", "Mark a job in progress", "Close a job (enter final cost)", "List maintenance jobs",
            "Edit a job description", "Remove a closed job", "Log fuel", "List fuel logs", "Remove a fuel log",
            "Report: costs (sorted / filtered)", "Overview of jobs and fuel logs"
        };
    }

    @Override
    protected void handle(int choice) throws Exception {
        switch (choice) {
            case 1: open(); break;
            case 2: jobs.start(InputHelper.readRequired("Job ID (e.g. G13-M001): ")); Menu.ok("Job is in progress."); break;
            case 3: close(); break;
            case 4: Menu.printList("Maintenance jobs", jobs.sorted(MaintenanceService.BY_REPORTED)); break;
            case 5: describeJob(); break;
            case 6: removeJob(); break;
            case 7: logFuel(); break;
            case 8: Menu.printList("Fuel logs", fuel.sorted(FuelService.BY_DATE_DESC)); break;
            case 9: removeFuel(); break;
            case 10: report(); break;
            case 11: overview(); break;
            default: break;
        }
    }

    private Vehicle pickVehicle() {
        String text = InputHelper.readRequired("Vehicle ID or plate: ");
        Vehicle v = vehicles.findByIdOrPlate(text);
        if (v == null) {
            throw new RecordNotFoundException("Vehicle", text);
        }
        return v;
    }

    private void open() {
        Vehicle v = pickVehicle();
        String what = InputHelper.readRequired("What is wrong / what service is needed? ");
        MaintenanceJob j = jobs.open(v, what);
        Menu.ok("Job " + j.getId() + " opened. " + v.getPlate() + " is now " + v.getStatus() + ".");
    }

    private void close() throws JobAlreadyClosedException {
        String id = InputHelper.readRequired("Job ID: ");
        jobs.require(id);
        double cost = InputHelper.readDouble("Final cost in UGX (0-50,000,000): ", 0, 50_000_000);
        jobs.close(id, cost);
        Menu.ok("Job closed.");
    }

    private void describeJob() {
        String id = InputHelper.readRequired("Job ID: ");
        jobs.require(id);
        String text = InputHelper.readRequired("New description: ");
        jobs.update(id, j -> j.setDescription(text));
        Menu.ok("Description updated.");
    }

    private void removeJob() {
        String id = InputHelper.readRequired("Job ID to remove: ");
        jobs.require(id);
        if (InputHelper.readYesNo("Remove job " + id + "?")) {
            jobs.remove(id);
            Menu.ok("Job removed.");
        }
    }

    private void logFuel() {
        Vehicle v = pickVehicle();
        double litres = InputHelper.readDouble("Litres (0.5-200): ", 0.5, 200);
        double cost = InputHelper.readDouble("Cost in UGX: ", 500, 2_000_000);
        int odo = InputHelper.readInt("Odometer reading in km: ", 0, 2_000_000);
        FuelLog l = fuel.log(v, litres, cost, odo);
        Menu.ok("Fuel log " + l.getId() + " saved.");
    }

    private void removeFuel() {
        String id = InputHelper.readRequired("Fuel log ID (e.g. G13-F001): ");
        fuel.require(id);
        if (InputHelper.readYesNo("Remove fuel log " + id + "?")) {
            fuel.remove(id);
            Menu.ok("Fuel log removed.");
        }
    }

    private void report() {
        int how = InputHelper.readChoice("Report: ", "Jobs by cost (highest first)", "Open and in-progress jobs",
                "Running cost per vehicle (maintenance + fuel)");
        if (how == 0) {
            Menu.printList("Jobs by cost", jobs.sorted(MaintenanceService.BY_COST_DESC));
        } else if (how == 1) {
            List<MaintenanceJob> list = jobs.filter(j -> j.getStatus() != MaintenanceJob.Status.CLOSED);
            list.sort(MaintenanceService.BY_REPORTED);
            Menu.printList("Open jobs", list);
        } else {
            List<Vehicle> list = new ArrayList<>(vehicles.getAll());
            list.sort((a, b) -> Double.compare(jobs.totalCost(b) + fuel.totalCost(b), jobs.totalCost(a) + fuel.totalCost(a)));
            System.out.println("--- Running cost per vehicle ---");
            for (Vehicle v : list) {
                System.out.println("  " + v.getPlate() + " (" + v.typeLabel() + "): maintenance "
                        + Validate.money(jobs.totalCost(v)) + ", fuel " + Validate.money(fuel.totalCost(v)));
            }
        }
    }

    /** Polymorphism: one loop over maintenance jobs and fuel logs through Reportable. */
    private void overview() {
        List<Reportable> mixed = new ArrayList<>(jobs.sorted(MaintenanceService.BY_REPORTED));
        mixed.addAll(fuel.sorted(FuelService.BY_DATE_DESC));
        System.out.println("--- Maintenance jobs and fuel logs (" + mixed.size() + ") ---");
        for (Reportable r : mixed) {
            System.out.println("  " + r.reportLine());
        }
    }
}
