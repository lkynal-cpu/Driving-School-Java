package ug.ac.vu.g13.core;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Safe text-file reading and writing. A missing file simply means "no data yet"
 * and a failed read or write prints a warning instead of crashing. Writing goes
 * to a temporary file first so a crash can never leave half a file behind.
 *
 * @author Kefas (Integration lead)
 */
public final class FileStore {
    private FileStore() {
    }

    public static List<String> read(String fileName) {
        Path path = Paths.get(Config.DATA_DIR, fileName);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            return new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
        } catch (IOException | RuntimeException e) {
            System.out.println("  ! Warning: could not read " + path + " (" + e.getMessage() + "). Starting empty.");
            return new ArrayList<>();
        }
    }

    public static boolean write(String fileName, List<String> lines) {
        Path path = Paths.get(Config.DATA_DIR, fileName);
        Path temp = Paths.get(Config.DATA_DIR, fileName + ".tmp");
        try {
            Files.createDirectories(path.getParent());
            Files.write(temp, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException | RuntimeException e) {
            System.out.println("  ! Warning: could not save " + path + " (" + e.getMessage() + ").");
            return false;
        }
    }
}
