package ug.ac.vu.g13.courses;

import java.util.Comparator;

import ug.ac.vu.g13.core.BaseService;
import ug.ac.vu.g13.core.Fields;
import ug.ac.vu.g13.core.InvalidDataException;
import ug.ac.vu.g13.core.VehicleClass;

/**
 * Manages the list of courses.
 *
 * @author Member 4 (Courses module owner)
 */
public class CourseService extends BaseService<Course> {
    public static final Comparator<Course> BY_FEE = Comparator.comparingDouble(Course::getFee).thenComparing(Course::getName);
    public static final Comparator<Course> BY_NAME = Comparator.comparing(c -> c.getName().toLowerCase());

    public CourseService() {
        super("courses.txt", "Course");
        load();
    }

    @Override
    protected Course fromFileLine(String line) {
        String[] f = Fields.split(line, 7);
        return new Course(f[0], f[1], VehicleClass.valueOf(f[2]), Integer.parseInt(f[3]), Integer.parseInt(f[4]),
                Double.parseDouble(f[5]), Integer.parseInt(f[6]));
    }

    public Course create(String name, VehicleClass vc, int weeks, int hours, double fee, int capacity) {
        Course c = new Course(nextId('C'), name, vc, weeks, hours, fee, capacity);
        if (anyMatch(x -> x.getName().equalsIgnoreCase(c.getName()))) {
            throw new InvalidDataException("A course named '" + c.getName() + "' already exists.");
        }
        add(c);
        return c;
    }
}
