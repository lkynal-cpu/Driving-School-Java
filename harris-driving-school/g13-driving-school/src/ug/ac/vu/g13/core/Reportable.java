package ug.ac.vu.g13.core;

/**
 * Anything that can print one line in a report. Every model in every module
 * implements this (through BaseRecord) so reports can loop over mixed objects.
 *
 * @author Kefas (Integration lead)
 */
public interface Reportable {
    String reportLine();
}
