package ug.ac.vu.g13.core;

/**
 * Contract every module menu follows so the main menu can list and run them.
 *
 * @author Kefas (Integration lead)
 */
public interface MenuModule {
    String getName();

    void run();
}
