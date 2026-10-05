package ug.ac.vu.g13.maintenance;

/**
 * Thrown when someone tries to change a maintenance job that is already closed.
 *
 * @author Member 8 (Maintenance module owner)
 */
public class JobAlreadyClosedException extends Exception {
    public JobAlreadyClosedException(MaintenanceJob job) {
        super("Job " + job.getId() + " was already closed on " + job.getClosedOn() + ".");
    }
}
