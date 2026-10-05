package ug.ac.vu.g13.core;

/**
 * Abstract parent of every stored record in the system. It owns the record ID
 * (which always starts with the group number, e.g. G13-S001) and forces each
 * record to describe itself and to turn itself into one line of file text.
 *
 * @author Kefas (Integration lead)
 */
public abstract class BaseRecord implements Reportable {
    private final String id;

    protected BaseRecord(String id) {
        this.id = Validate.recordId(id);
    }

    public String getId() {
        return id;
    }

    /** Human readable multi-field description. */
    public abstract String describe();

    /** One line of text used to save this record to its data file. */
    public abstract String toFileLine();

    @Override
    public String reportLine() {
        return describe();
    }

    @Override
    public String toString() {
        return describe();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof BaseRecord && ((BaseRecord) o).id.equalsIgnoreCase(id);
    }

    @Override
    public int hashCode() {
        return id.toLowerCase().hashCode();
    }
}
