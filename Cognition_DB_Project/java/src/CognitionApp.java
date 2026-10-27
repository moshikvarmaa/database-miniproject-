import java.sql.*;
import java.util.Scanner;

/**
 * Cognition DB console front end using Oracle JDBC.
 * Set ORACLE_URL, ORACLE_USER and ORACLE_PASSWORD environment variables before running.
 * This demo is for synthetic class-project data only.
 */
public class CognitionApp {
    private static final Scanner IN = new Scanner(System.in);
    private static Connection connection;

    public static void main(String[] args) {
        String url = System.getenv().getOrDefault("ORACLE_URL", "jdbc:oracle:thin:@//localhost:1521/FREEPDB1");
        String user = System.getenv("ORACLE_USER");
        String password = System.getenv("ORACLE_PASSWORD");
        if (user == null || password == null) {
            System.err.println("Set ORACLE_USER and ORACLE_PASSWORD environment variables first.");
            System.err.println("Default URL: " + url + " (change ORACLE_URL if your Oracle service differs).");
            return;
        }
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to Cognition database.");
            boolean running = true;
            while (running) {
                System.out.println("\n=== COGNITION LEARNER DATABASE ===");
                System.out.println("1. Display learners");
                System.out.println("2. Search learner by participant code");
                System.out.println("3. Insert learner");
                System.out.println("4. Update learner display name");
                System.out.println("5. Delete learner (only if no linked sessions)");
                System.out.println("6. Display sessions with topic");
                System.out.println("0. Exit");
                System.out.print("Choose: ");
                String choice = IN.nextLine().trim();
                try {
                    switch (choice) {
                        case "1" -> displayLearners();
                        case "2" -> searchLearner();
                        case "3" -> insertLearner();
                        case "4" -> updateLearner();
                        case "5" -> deleteLearner();
                        case "6" -> displaySessions();
                        case "0" -> running = false;
                        default -> System.out.println("Choose one of the listed options.");
                    }
                } catch (SQLException ex) {
                    System.out.println("Database operation failed: " + ex.getMessage());
                }
            }
            connection.close();
        } catch (SQLException ex) {
            System.err.println("Could not connect to Oracle: " + ex.getMessage());
            System.err.println("Check that Oracle is running, the service name is correct, and credentials are valid.");
        }
    }

    private static void displayLearners() throws SQLException {
        String sql = "SELECT learner_id, participant_code, display_name, active_flag FROM learner ORDER BY learner_id";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.printf("%-10s %-20s %-28s %-8s%n", "ID", "CODE", "NAME", "ACTIVE");
            while (rs.next()) {
                System.out.printf("%-10d %-20s %-28s %-8s%n", rs.getLong(1), rs.getString(2),
                    rs.getString(3), rs.getString(4));
            }
        }
    }

    private static void searchLearner() throws SQLException {
        System.out.print("Participant code: ");
        String code = IN.nextLine().trim();
        String sql = "SELECT learner_id, participant_code, display_name, active_flag FROM learner WHERE participant_code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) System.out.println("No learner found.");
                else System.out.printf("ID=%d | Code=%s | Name=%s | Active=%s%n",
                    rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4));
            }
        }
    }

    private static void insertLearner() throws SQLException {
        System.out.print("Participant code: ");
        String code = IN.nextLine().trim();
        System.out.print("Display name (optional): ");
        String name = IN.nextLine().trim();
        String sql = "INSERT INTO learner (participant_code, display_name) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            if (name.isEmpty()) ps.setNull(2, Types.VARCHAR); else ps.setString(2, name);
            ps.executeUpdate();
            connection.commit();
            System.out.println("Learner inserted.");
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    private static void updateLearner() throws SQLException {
        System.out.print("Participant code: ");
        String code = IN.nextLine().trim();
        System.out.print("New display name: ");
        String name = IN.nextLine().trim();
        String sql = "UPDATE learner SET display_name = ? WHERE participant_code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, code);
            int count = ps.executeUpdate();
            connection.commit();
            System.out.println(count == 0 ? "No learner found." : "Learner updated.");
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    private static void deleteLearner() throws SQLException {
        System.out.print("Participant code to delete: ");
        String code = IN.nextLine().trim();
        System.out.print("Confirm delete? Type YES: ");
        if (!"YES".equals(IN.nextLine().trim())) {
            System.out.println("Cancelled.");
            return;
        }
        String sql = "DELETE FROM learner WHERE participant_code = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            int count = ps.executeUpdate();
            connection.commit();
            System.out.println(count == 0 ? "No learner deleted (not found or linked records exist)." : "Learner deleted.");
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    private static void displaySessions() throws SQLException {
        String sql = "SELECT s.session_id, l.participant_code, t.topic_name, s.started_at, s.status " +
                     "FROM learning_session s JOIN learner l ON l.learner_id=s.learner_id " +
                     "JOIN topic t ON t.topic_id=s.topic_id ORDER BY s.session_id";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            System.out.printf("%-10s %-18s %-25s %-28s %-12s%n", "SESSION", "LEARNER", "TOPIC", "STARTED", "STATUS");
            while (rs.next()) {
                System.out.printf("%-10d %-18s %-25s %-28s %-12s%n", rs.getLong(1),
                    rs.getString(2), rs.getString(3), rs.getTimestamp(4), rs.getString(5));
            }
        }
    }
}
