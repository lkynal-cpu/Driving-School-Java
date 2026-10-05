package ug.ac.vu.g13.core;

import java.time.LocalDate;

/**
 * Small collection of validation helpers. Every one throws InvalidDataException
 * with a clear message, so constructors and setters stay short.
 *
 * @author Kefas (Integration lead)
 */
public final class Validate {
    private Validate() {
    }

    public static String recordId(String id) {
        if (id == null || !id.matches("^" + Config.GROUP + "-[A-Z]\\d{3,}$")) {
            throw new InvalidDataException("Record ID must look like " + Config.GROUP + "-S001 but was '" + id + "'.");
        }
        return id;
    }

    public static String text(String value, String field, int min, int max) {
        if (value == null) {
            throw new InvalidDataException(field + " is required.");
        }
        String v = value.trim().replaceAll("\\s+", " ");
        if (v.length() < min || v.length() > max) {
            throw new InvalidDataException(field + " must be " + min + " to " + max + " characters long.");
        }
        return v;
    }

    public static String name(String value, String field) {
        String v = text(value, field, 2, 60);
        if (!v.matches("[\\p{L}][\\p{L} .'-]*")) {
            throw new InvalidDataException(field + " may only contain letters, spaces, . ' and -.");
        }
        return v;
    }

    /** Accepts 0700000000 or +256700000000 style numbers (use invented numbers only). */
    public static String phone(String value) {
        String v = text(value, "Phone number", 10, 13).replace(" ", "");
        if (!v.matches("(\\+256|0)\\d{9}")) {
            throw new InvalidDataException("Phone number must look like 0700000000 or +256700000000.");
        }
        return v;
    }

    public static int intRange(int value, String field, int min, int max) {
        if (value < min || value > max) {
            throw new InvalidDataException(field + " must be between " + min + " and " + max + ".");
        }
        return value;
    }

    public static double doubleRange(double value, String field, double min, double max) {
        if (Double.isNaN(value) || value < min || value > max) {
            throw new InvalidDataException(field + " must be between " + min + " and " + max + ".");
        }
        return value;
    }

    public static <T> T notNull(T value, String field) {
        if (value == null) {
            throw new InvalidDataException(field + " is required.");
        }
        return value;
    }

    public static LocalDate date(LocalDate value, String field, LocalDate earliest, LocalDate latest) {
        notNull(value, field);
        if (value.isBefore(earliest) || value.isAfter(latest)) {
            throw new InvalidDataException(field + " must be between " + earliest + " and " + latest + ".");
        }
        return value;
    }

    public static String money(double amount) {
        return String.format("UGX %,.0f", amount);
    }
}
