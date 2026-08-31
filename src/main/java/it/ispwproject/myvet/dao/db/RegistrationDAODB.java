package it.ispwproject.myvet.dao.db;

import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.dao.RegistrationDAO;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.User;
import it.ispwproject.myvet.model.Veterinarian;
import it.ispwproject.myvet.util.logger.AppLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RegistrationDAODB implements RegistrationDAO {

    private static final String INSERT_USER =
            "INSERT INTO user " +
                    "(name, surname, email, password, role, gender) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String INSERT_VETERINARIAN_DETAIL =
            "INSERT INTO veterinarian_detail " +
                    "(user_id, bio, specialization) VALUES (?, ?, ?)";

    private static final String CHECK_EMAIL =
            "SELECT COUNT(*) FROM user WHERE email = ?";

    @Override
    public boolean emailExists(String email) throws DAOException {
        try {
            ConnectionFactory.clearRole();
        } catch (SQLException e) {
            AppLogger.logWarning(
                    "clearRole fallito: " + e.getMessage()
            );
        }

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(CHECK_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore verifica email: " + e.getMessage(),
                    e
            );
        }

        return false;
    }

    @Override
    public void save(User user) throws DAOException {
        try {
            ConnectionFactory.clearRole();
        } catch (SQLException e) {
            AppLogger.logWarning(
                    "clearRole fallito: " + e.getMessage()
            );
        }

        try (Connection conn = ConnectionFactory.getConnection()) {
            executeSaveTransaction(conn, user);

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore connessione: " + e.getMessage(),
                    e
            );
        }
    }

    private void executeSaveTransaction(
            Connection conn,
            User user)
            throws SQLException, DAOException {

        conn.setAutoCommit(false);

        try {
            int userId = insertUser(conn, user);
            user.setId(userId);

            if (user instanceof Veterinarian veterinarian) {
                insertVeterinarianDetail(
                        conn,
                        userId,
                        veterinarian.getBio(),
                        veterinarian.getSpecialization()
                );
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();

            throw new DAOException(
                    "Errore registrazione: " + e.getMessage(),
                    e
            );

        } finally {
            conn.setAutoCommit(true);
        }
    }

    private int insertUser(
            Connection conn,
            User user) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(
                INSERT_USER,
                Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole().name());
            ps.setString(6, user.getGender().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }

        throw new SQLException("ID utente non generato.");
    }

    private void insertVeterinarianDetail(
            Connection conn,
            int veterinarianId,
            String bio,
            String specialization)
            throws SQLException {

        try (PreparedStatement ps =
                     conn.prepareStatement(
                             INSERT_VETERINARIAN_DETAIL
                     )) {

            ps.setInt(1, veterinarianId);
            ps.setString(2, bio);
            ps.setString(3, specialization);

            ps.executeUpdate();
        }
    }
}
