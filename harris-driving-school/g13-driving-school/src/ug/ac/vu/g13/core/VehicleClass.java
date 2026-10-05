package ug.ac.vu.g13.core;

/**
 * Category of vehicle a learner is trained on. Shared by vehicles, courses,
 * instructors and tests so every module speaks the same language.
 *
 * @author Kefas (Integration lead)
 */
public enum VehicleClass {
    MOTORCYCLE("Motorcycle"),
    CAR("Car"),
    TRUCK("Truck");

    private final String label;

    VehicleClass(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
