package ug.ac.vu.g13.core;

/**
 * Template for every module submenu. It shows the options, runs the chosen
 * action and catches any error so a wrong action never closes the program.
 *
 * @author Kefas (Integration lead)
 */
public abstract class AbstractModuleMenu implements MenuModule {
    protected abstract String[] options();

    /** Runs option number 1..n. May throw any exception; it is reported to the user. */
    protected abstract void handle(int choice) throws Exception;

    @Override
    public final void run() {
        while (true) {
            int choice = Menu.choose(getName(), options());
            if (choice == 0) {
                return;
            }
            try {
                handle(choice);
            } catch (InputClosedException e) {
                throw e;
            } catch (Exception e) {
                String msg = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
                System.out.println("  ! " + msg);
            }
        }
    }
}
