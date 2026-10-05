package ug.ac.vu.g13.core;

/**
 * Anything that carries an amount of money still to be paid, for example a fee
 * invoice. Amounts are in Uganda shillings (UGX).
 *
 * @author Kefas (Integration lead)
 */
public interface Payable {
    double getTotalDue();

    double getAmountPaid();

    default double getBalance() {
        return getTotalDue() - getAmountPaid();
    }

    default boolean isSettled() {
        return getBalance() <= 0.0001;
    }
}
