package ug.ac.vu.g13.core;

import java.util.List;

/**
 * Prints menus and lists in one consistent style.
 *
 * @author Kefas (Integration lead)
 */
public final class Menu {
    private Menu() {
    }

    /** Shows a numbered menu with 0 = back and returns the choice. */
    public static int choose(String title, String... options) {
        System.out.println();
        System.out.println("=== " + title + " ===");
        for (int i = 0; i < options.length; i++) {
            System.out.println(" " + (i + 1) + ". " + options[i]);
        }
        System.out.println(" 0. Back / Exit");
        return InputHelper.readInt("Choose: ", 0, options.length);
    }

    public static void printList(String title, List<? extends Reportable> items) {
        System.out.println();
        System.out.println("--- " + title + " (" + items.size() + ") ---");
        if (items.isEmpty()) {
            System.out.println("  (nothing to show)");
            return;
        }
        for (Reportable r : items) {
            System.out.println("  " + r.reportLine());
        }
    }

    public static void ok(String message) {
        System.out.println("  OK: " + message);
    }
}
