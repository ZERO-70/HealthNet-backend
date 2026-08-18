import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs .sql files against the configured database.
 *
 * Exists because there is no mysql client installed locally; this reuses the
 * JDBC driver Maven already downloaded. Run via scripts/load-schema.sh, which
 * supplies DB_URL / DB_USERNAME / DB_PASSWORD from .env.
 *
 * Statement splitting is a plain semicolon split. That is sufficient for these
 * files (no stored procedures, triggers, or semicolons inside string literals)
 * and deliberately not a general-purpose SQL parser.
 */
public class DbInit {

    public static void main(String[] args) throws Exception {
        String url = env("DB_URL");
        String user = env("DB_USERNAME");
        String pass = env("DB_PASSWORD");

        if (args.length == 0) {
            System.err.println("usage: DbInit <file.sql> [file.sql ...]");
            System.exit(2);
        }

        String display = url.replaceAll("//[^@]*@", "//");
        System.out.println("Connecting to " + display);

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Connected: " + conn.getMetaData().getDatabaseProductVersion());

            for (String file : args) {
                System.out.println("\n--- " + file);
                String sql = Files.readString(Path.of(file));
                int ok = 0;

                for (String stmt : split(sql)) {
                    try (Statement s = conn.createStatement()) {
                        s.execute(stmt);
                        ok++;
                    } catch (Exception e) {
                        System.err.println("  FAILED: " + firstLine(stmt));
                        System.err.println("    " + e.getMessage());
                        throw e;
                    }
                }
                System.out.println("  " + ok + " statements executed");
            }
        }
        System.out.println("\nDone.");
    }

    /** Strips line comments, then splits on semicolons. */
    private static List<String> split(String sql) {
        StringBuilder cleaned = new StringBuilder();
        for (String line : sql.split("\n")) {
            String t = line.strip();
            if (t.startsWith("--") || t.isEmpty()) continue;
            cleaned.append(line).append('\n');
        }
        List<String> out = new ArrayList<>();
        for (String part : cleaned.toString().split(";")) {
            if (!part.isBlank()) out.add(part.strip());
        }
        return out;
    }

    private static String firstLine(String s) {
        int i = s.indexOf('\n');
        return i < 0 ? s : s.substring(0, i) + " ...";
    }

    private static String env(String name) {
        String v = System.getenv(name);
        if (v == null || v.isBlank()) {
            System.err.println("Missing environment variable: " + name + " (set it in .env)");
            System.exit(2);
        }
        return v;
    }
}
