package ug.ac.vu.g13.core;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The one place where the program reads from the keyboard. Every method keeps
 * asking until the user types something valid, so no input can crash the
 * program. If the input stream ends, InputClosedException is thrown and the
 * main program exits cleanly.
 *
 * @author Kefas (Integration lead)
 */
public final class InputHelper {
    private static final Scanner SCANNER = new Scanner(System.in);

    private InputHelper() {
    }

    public static String readLine(String prompt) {
        System.out.print(prompt);
        try {
            if (!SCANNER.hasNextLine()) {
                throw new InputClosedException();
            }
            return SCANNER.nextLine().trim();
        } catch (NoSuchElementException | IllegalStateException e) {
            throw new InputClosedException();
        }
    }

    public static String readText(String prompt, int min, int max) {
        while (true) {
            String s = readLine(prompt);
            if (s.length() >= min && s.length() <= max) {
                return s;
            }
            System.out.println("  ! Please enter " + min + " to " + max + " characters.");
        }
    }

    public static String readRequired(String prompt) {
        return readText(prompt, 1, 120);
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            String s = readLine(prompt);
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
                // fall through to message
            }
            System.out.println("  ! Please enter a whole number from " + min + " to " + max + ".");
        }
    }

    public static double readDouble(String prompt, double min, double max) {
        while (true) {
            String s = readLine(prompt).replace(",", "");
            try {
                double v = Double.parseDouble(s);
                if (!Double.isNaN(v) && v >= min && v <= max) {
                    return v;
                }
            } catch (NumberFormatException ignored) {
                // fall through to message
            }
            System.out.println("  ! Please enter a number from " + min + " to " + max + ".");
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            String s = readLine(prompt + " (yyyy-mm-dd): ");
            try {
                return LocalDate.parse(s);
            } catch (DateTimeParseException e) {
                System.out.println("  ! Please enter a real date like 2026-10-30.");
            }
        }
    }

    public static LocalTime readTime(String prompt) {
        while (true) {
            String s = readLine(prompt + " (HH:mm, 24-hour): ");
            try {
                return LocalTime.parse(s.length() == 4 ? "0" + s : s);
            } catch (DateTimeParseException e) {
                System.out.println("  ! Please enter a time like 08:30 or 14:00.");
            }
        }
    }

    public static boolean readYesNo(String prompt) {
        while (true) {
            String s = readLine(prompt + " (y/n): ").toLowerCase();
            if (s.equals("y") || s.equals("yes")) {
                return true;
            }
            if (s.equals("n") || s.equals("no")) {
                return false;
            }
            System.out.println("  ! Please type y or n.");
        }
    }

    /** Shows numbered options and returns the chosen index (0-based). */
    public static int readChoice(String prompt, Object... options) {
        for (int i = 0; i < options.length; i++) {
            System.out.println("   " + (i + 1) + ". " + options[i]);
        }
        return readInt(prompt, 1, options.length) - 1;
    }

    public static <E extends Enum<E>> E readEnum(String prompt, E[] values) {
        return values[readChoice(prompt, (Object[]) values)];
    }

    public static void pause() {
        readLine("  Press Enter to continue...");
    }
}
