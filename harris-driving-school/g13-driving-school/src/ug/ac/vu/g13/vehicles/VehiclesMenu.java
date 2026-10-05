package ug.ac.vu.g13.vehicles;

import java.util.List;

import ug.ac.vu.g13.core.AbstractModuleMenu;
import ug.ac.vu.g13.core.InputHelper;
import ug.ac.vu.g13.core.Menu;
import ug.ac.vu.g13.core.VehicleClass;

/**
 * Submenu for the Vehicles module.
 *
 * @author Member 3 (Vehicles module owner)
 */
public class VehiclesMenu extends AbstractModuleMenu {
    private final VehicleService vehicles;

    public VehiclesMenu(VehicleService vehicles) {
        this.vehicles = vehicles;
    }

    @Override
    public String getName() {
        return "Vehicles";
    }

    @Override
    protected String[] options() {
        return new String[] {
            "Add a vehicle", "List all vehicles", "Find a vehicle by ID or plate", "Update a vehicle",
            "Remove a vehicle", "Report: vehicles (sorted / filtered)", "Fleet overview by type"
        };
    }

    @Override
    protected void handle(int choice) {
        switch (choice) {
            case 1: add(); break;
            case 2: Menu.printList("All vehicles", vehicles.sorted(VehicleService.BY_PLATE)); break;
            case 3: find(); break;
            case 4: update(); break;
            case 5: remove(); break;
            case 6: report(); break;
            case 7: fleetOverview(); break;
            default: break;
        }
    }

    private void add() {
        VehicleClass vc = InputHelper.readEnum("Type of vehicle: ", VehicleClass.values());
        String plate = InputHelper.readRequired("Number plate (e.g. UBA 123A): ");
        String model = InputHelper.readRequired("Make and model (e.g. Toyota Premio): ");
        int year = InputHelper.readInt("Year of manufacture: ", 1990, 2100);
        int odo = InputHelper.readInt("Odometer reading in km: ", 0, 2_000_000);
        Vehicle v;
        if (vc == VehicleClass.CAR) {
            v = vehicles.addCar(plate, model, year, odo, InputHelper.readEnum("Transmission: ", Car.Transmission.values()));
        } else if (vc == VehicleClass.MOTORCYCLE) {
            v = vehicles.addMotorcycle(plate, model, year, odo, InputHelper.readInt("Engine size in cc: ", 50, 1500));
        } else {
            v = vehicles.addTruck(plate, model, year, odo, InputHelper.readDouble("Load capacity in tons: ", 1, 40));
        }
        Menu.ok("Added " + v.typeLabel() + " " + v.getPlate() + " with ID " + v.getId());
    }

    private Vehicle pick() {
        String text = InputHelper.readRequired("Vehicle ID or plate: ");
        Vehicle v = vehicles.findByIdOrPlate(text);
        if (v == null) {
            throw new ug.ac.vu.g13.core.RecordNotFoundException("Vehicle", text);
        }
        return v;
    }

    private void find() {
        System.out.println("  " + pick().describe());
    }

    private void update() {
        Vehicle v = pick();
        int f = InputHelper.readChoice("Change: ", "Status", "Odometer reading", "Make and model");
        if (f == 0) {
            Vehicle.Status st = InputHelper.readEnum("New status: ", Vehicle.Status.values());
            vehicles.update(v.getId(), x -> x.setStatus(st));
        } else if (f == 1) {
            int odo = InputHelper.readInt("New odometer in km: ", 0, 2_000_000);
            vehicles.update(v.getId(), x -> x.setOdometerKm(odo));
        } else {
            String m = InputHelper.readRequired("New make and model: ");
            vehicles.update(v.getId(), x -> x.setMakeModel(m));
        }
        Menu.ok("Vehicle updated.");
    }

    private void remove() {
        Vehicle v = pick();
        if (InputHelper.readYesNo("Remove " + v.getPlate() + "?")) {
            vehicles.remove(v.getId());
            Menu.ok("Vehicle removed.");
        }
    }

    private void report() {
        int how = InputHelper.readChoice("Report: ", "Newest first", "Highest mileage first", "Only one vehicle class",
                "Only one status");
        if (how == 0) {
            Menu.printList("Vehicles, newest first", vehicles.sorted(VehicleService.BY_YEAR_NEWEST));
        } else if (how == 1) {
            Menu.printList("Vehicles by mileage", vehicles.sorted(VehicleService.BY_ODOMETER_DESC));
        } else if (how == 2) {
            VehicleClass vc = InputHelper.readEnum("Class: ", VehicleClass.values());
            List<Vehicle> list = vehicles.filter(v -> v.getVehicleClass() == vc);
            list.sort(VehicleService.BY_PLATE);
            Menu.printList("Vehicles: " + vc, list);
        } else {
            Vehicle.Status st = InputHelper.readEnum("Status: ", Vehicle.Status.values());
            List<Vehicle> list = vehicles.filter(v -> v.getStatus() == st);
            list.sort(VehicleService.BY_PLATE);
            Menu.printList("Vehicles: " + st, list);
        }
    }

    /** Polymorphism: one loop over Car, Motorcycle and Truck objects through the Vehicle type. */
    private void fleetOverview() {
        int available = 0;
        System.out.println("--- Fleet overview (" + vehicles.size() + ") ---");
        for (Vehicle v : vehicles.sorted(VehicleService.BY_PLATE)) {
            System.out.println("  [" + v.typeLabel() + "] " + v.reportLine());
            if (v.getStatus() == Vehicle.Status.AVAILABLE) {
                available++;
            }
        }
        System.out.println("  Available for lessons: " + available + " of " + vehicles.size());
    }
}
