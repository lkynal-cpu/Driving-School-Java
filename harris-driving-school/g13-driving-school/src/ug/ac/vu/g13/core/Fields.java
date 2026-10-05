package ug.ac.vu.g13.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Joins and splits the fields of one data-file line. The pipe character is the
 * separator, so pipes, percent signs and new lines inside values are escaped.
 *
 * @author Kefas (Integration lead)
 */
public final class Fields {
    private Fields() {
    }

    public static String join(Object... parts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                sb.append('|');
            }
            sb.append(escape(parts[i] == null ? "" : String.valueOf(parts[i])));
        }
        return sb.toString();
    }

    public static String[] split(String line, int expected) {
        List<String> out = new ArrayList<>();
        int start = 0;
        for (int i = 0; i <= line.length(); i++) {
            if (i == line.length() || line.charAt(i) == '|') {
                out.add(unescape(line.substring(start, i)));
                start = i + 1;
            }
        }
        if (out.size() < expected) {
            throw new InvalidDataException("Damaged line: expected " + expected + " fields but found " + out.size() + ".");
        }
        return out.toArray(new String[0]);
    }

    private static String escape(String s) {
        return s.replace("%", "%25").replace("|", "%7C").replace("\n", "%0A").replace("\r", "%0D");
    }

    private static String unescape(String s) {
        return s.replace("%0D", "\r").replace("%0A", "\n").replace("%7C", "|").replace("%25", "%");
    }
}
