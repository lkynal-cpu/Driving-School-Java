package ug.ac.vu.g13.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Generic service every module extends. It keeps the module's records in an
 * ArrayList and provides add, find, update and remove, plus loading at start-up
 * and saving after every change. Each module's own service adds the business
 * rules on top.
 *
 * @author Kefas (Integration lead)
 * @param <T> the record type managed by the service
 */
public abstract class BaseService<T extends BaseRecord> {
    private final List<T> items = new ArrayList<>();
    private final List<Function<T, String>> removalGuards = new ArrayList<>();
    private final String fileName;
    private final String label;

    protected BaseService(String fileName, String label) {
        this.fileName = fileName;
        this.label = label;
    }

    /** Turns one saved line back into a record. May throw if the line is damaged. */
    protected abstract T fromFileLine(String line);

    /** Subclasses call this at the end of their constructor, once dependencies are set. */
    protected final void load() {
        int damaged = 0;
        for (String line : FileStore.read(fileName)) {
            if (line.trim().isEmpty()) {
                continue;
            }
            try {
                T item = fromFileLine(line);
                if (item == null || find(item.getId()) != null) {
                    damaged++;
                } else {
                    items.add(item);
                }
            } catch (RuntimeException e) {
                damaged++;
            }
        }
        if (damaged > 0) {
            System.out.println("  ! Warning: skipped " + damaged + " damaged line(s) in " + fileName + ".");
        }
    }

    public final void save() {
        List<String> lines = new ArrayList<>();
        for (T item : items) {
            lines.add(item.toFileLine());
        }
        FileStore.write(fileName, lines);
    }

    public void add(T item) {
        Validate.notNull(item, label);
        if (find(item.getId()) != null) {
            throw new InvalidDataException(label + " " + item.getId() + " already exists.");
        }
        items.add(item);
        save();
    }

    public T find(String id) {
        if (id == null) {
            return null;
        }
        String wanted = id.trim();
        for (T item : items) {
            if (item.getId().equalsIgnoreCase(wanted)) {
                return item;
            }
        }
        return null;
    }

    public T require(String id) {
        T item = find(id);
        if (item == null) {
            throw new RecordNotFoundException(label, id == null ? "" : id.trim());
        }
        return item;
    }

    /** Applies a change (normally a setter call that validates) and saves. */
    public T update(String id, Consumer<T> change) {
        T item = require(id);
        change.accept(item);
        save();
        return item;
    }

    public T remove(String id) {
        T item = require(id);
        for (Function<T, String> guard : removalGuards) {
            String reason = guard.apply(item);
            if (reason != null) {
                throw new RecordInUseException("Cannot remove " + item.getId() + ": " + reason);
            }
        }
        items.remove(item);
        save();
        return item;
    }

    /** Lets another module block removal while it still refers to a record. */
    public void addRemovalGuard(Function<T, String> guard) {
        removalGuards.add(guard);
    }

    public List<T> getAll() {
        return Collections.unmodifiableList(items);
    }

    public int size() {
        return items.size();
    }

    public boolean anyMatch(Predicate<T> test) {
        for (T item : items) {
            if (test.test(item)) {
                return true;
            }
        }
        return false;
    }

    public List<T> filter(Predicate<T> test) {
        List<T> out = new ArrayList<>();
        for (T item : items) {
            if (test.test(item)) {
                out.add(item);
            }
        }
        return out;
    }

    public List<T> sorted(Comparator<T> comparator) {
        List<T> copy = new ArrayList<>(items);
        copy.sort(comparator);
        return copy;
    }

    /** Next ID such as G13-S004 for the given type letter. */
    protected String nextId(char letter) {
        String prefix = Config.GROUP + "-" + letter;
        int max = 0;
        for (T item : items) {
            String id = item.getId();
            if (id.startsWith(prefix)) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(prefix.length())));
                } catch (NumberFormatException ignored) {
                    // not one of ours, ignore
                }
            }
        }
        return String.format("%s%03d", prefix, max + 1);
    }
}
