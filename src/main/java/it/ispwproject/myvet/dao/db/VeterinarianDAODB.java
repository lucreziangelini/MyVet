package it.ispwproject.myvet.dao.db;

import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.dao.VeterinarianDAO;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Veterinarian;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VeterinarianDAODB
        implements VeterinarianDAO {

    private static final String GET_AVAILABLE_BY_DATE =
            "SELECT DISTINCT u.id, u.name, u.surname, u.email, " +
                    "vd.bio, vd.specialization " +
                    "FROM user u " +
                    "JOIN veterinarian_detail vd " +
                    "ON u.id = vd.user_id " +
                    "JOIN time_slot ts " +
                    "ON u.id = ts.veterinarian_id " +
                    "WHERE u.role = 'VETERINARIAN' " +
                    "AND ts.date = ? " +
                    "AND ts.available = TRUE " +
                    "AND (ts.reserved_until IS NULL " +
                    "OR ts.reserved_until < NOW()) " +
                    "ORDER BY u.name, u.surname";

    private static final String FIND_BY_ID =
            "SELECT u.id, u.name, u.surname, u.email, " +
                    "vd.bio, vd.specialization " +
                    "FROM user u " +
                    "JOIN veterinarian_detail vd " +
                    "ON u.id = vd.user_id " +
                    "WHERE u.id = ? " +
                    "AND u.role = 'VETERINARIAN'";

    @Override
    public List<Veterinarian> getAvailableByDate(
            LocalDate date) throws DAOException {

        List<Veterinarian> result =
                new ArrayList<>();

        try (Connection conn =
                     ConnectionFactory.getConnection();

             PreparedStatement ps =
                     conn.prepareStatement(
                             GET_AVAILABLE_BY_DATE
                     )) {

            ps.setDate(1, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(
                            mapToVeterinarian(rs)
                    );
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento dei veterinari: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    @Override
    public Veterinarian findById(int id)
            throws DAOException {

        try (Connection conn =
                     ConnectionFactory.getConnection();

             PreparedStatement ps =
                     conn.prepareStatement(FIND_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapToVeterinarian(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento del veterinario: "
                            + e.getMessage(),
                    e
            );
        }

        return null;
    }

    private Veterinarian mapToVeterinarian(
            ResultSet rs) throws SQLException {

        return new Veterinarian(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("email"),
                null,
                rs.getString("bio"),
                rs.getString("specialization")
        );
    }
}