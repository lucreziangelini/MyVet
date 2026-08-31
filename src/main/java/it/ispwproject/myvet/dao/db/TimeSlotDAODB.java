package it.ispwproject.myvet.dao.db;

import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.dao.TimeSlotDAO;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.TimeSlot;
import it.ispwproject.myvet.model.Veterinarian;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotDAODB implements TimeSlotDAO {

    private static final String GET_AVAILABLE_BY_VETERINARIAN =
            "SELECT id, veterinarian_id, date, start_time, end_time, available, reserved_until " +
                    "FROM time_slot " +
                    "WHERE veterinarian_id = ? AND available = TRUE " +
                    "AND (date > CURDATE() OR " +
                    "(date = CURDATE() AND start_time > CURTIME())) " +
                    "AND (reserved_until IS NULL OR reserved_until < NOW()) " +
                    "ORDER BY date, start_time";

    private static final String GET_AVAILABLE_BY_VETERINARIAN_AND_DATE =
            "SELECT id, veterinarian_id, date, start_time, end_time, available, reserved_until " +
                    "FROM time_slot " +
                    "WHERE veterinarian_id = ? AND date = ? " +
                    "AND available = TRUE " +
                    "AND (date > CURDATE() OR " +
                    "(date = CURDATE() AND start_time > CURTIME())) " +
                    "AND (reserved_until IS NULL OR reserved_until < NOW()) " +
                    "ORDER BY start_time";

    private static final String GET_AVAILABLE_VETERINARIAN_IDS =
            "SELECT DISTINCT veterinarian_id FROM time_slot " +
                    "WHERE date = ? AND available = TRUE " +
                    "AND (date > CURDATE() OR " +
                    "(date = CURDATE() AND start_time > CURTIME())) " +
                    "AND (reserved_until IS NULL OR reserved_until < NOW())";

    private static final String GET_ALL_BY_VETERINARIAN =
            "SELECT id, veterinarian_id, date, start_time, end_time, available, reserved_until " +
                    "FROM time_slot " +
                    "WHERE veterinarian_id = ? " +
                    "AND (date > CURDATE() OR " +
                    "(date = CURDATE() AND start_time > CURTIME())) " +
                    "ORDER BY date, start_time";

    private static final String GET_PAST_BY_VETERINARIAN =
            "SELECT id, veterinarian_id, date, start_time, end_time, available, reserved_until " +
                    "FROM time_slot " +
                    "WHERE veterinarian_id = ? " +
                    "AND (date < CURDATE() OR " +
                    "(date = CURDATE() AND end_time < CURTIME())) " +
                    "ORDER BY date DESC, start_time";

    private static final String FIND_BY_ID =
            "SELECT id, veterinarian_id, date, start_time, end_time, available, reserved_until " +
                    "FROM time_slot WHERE id = ?";

    private static final String SAVE =
            "INSERT INTO time_slot " +
                    "(veterinarian_id, date, start_time, end_time, available) " +
                    "VALUES (?, ?, ?, ?, TRUE)";

    private static final String RESERVE_SLOT =
            "{call reserve_slot(?, ?, ?)}";

    private static final String RELEASE_SLOT =
            "{call release_slot(?)}";

    private static final String DELETE_SLOT =
            "DELETE FROM time_slot " +
                    "WHERE id = ? AND veterinarian_id = ? " +
                    "AND available = TRUE";

    @Override
    public boolean reserveSlot(
            int slotId,
            int minutes) throws DAOException {

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs =
                     conn.prepareCall(RESERVE_SLOT)) {

            cs.setInt(1, slotId);
            cs.setInt(2, minutes);
            cs.registerOutParameter(3, Types.BOOLEAN);
            cs.execute();

            return cs.getBoolean(3);

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore durante la prenotazione temporanea: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void releaseSlot(int slotId)
            throws DAOException {

        try (Connection conn = ConnectionFactory.getConnection();
             CallableStatement cs =
                     conn.prepareCall(RELEASE_SLOT)) {

            cs.setInt(1, slotId);
            cs.execute();

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore durante il rilascio della fascia oraria: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public List<TimeSlot> getAvailableByVeterinarian(
            Veterinarian veterinarian)
            throws DAOException {

        List<TimeSlot> result = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(
                             GET_AVAILABLE_BY_VETERINARIAN
                     )) {

            ps.setInt(1, veterinarian.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TimeSlot slot = mapToTimeSlot(rs);
                    slot.setVeterinarian(veterinarian);
                    result.add(slot);
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento delle fasce orarie: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    @Override
    public List<TimeSlot> getAvailableByVeterinarianAndDate(
            int veterinarianId,
            LocalDate date) throws DAOException {

        List<TimeSlot> result = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(
                             GET_AVAILABLE_BY_VETERINARIAN_AND_DATE
                     )) {

            ps.setInt(1, veterinarianId);
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapToTimeSlot(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento delle fasce orarie: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    @Override
    public List<Integer> getAvailableVeterinarianIds(
            LocalDate date) throws DAOException {

        List<Integer> result = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     GET_AVAILABLE_VETERINARIAN_IDS
             )) {

            ps.setDate(1, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(rs.getInt("veterinarian_id"));
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento dei veterinari disponibili: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    @Override
    public List<TimeSlot> getAllByVeterinarian(
            int veterinarianId) throws DAOException {

        List<TimeSlot> result = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(
                             GET_ALL_BY_VETERINARIAN
                     )) {

            ps.setInt(1, veterinarianId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapToTimeSlot(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento delle fasce orarie: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    @Override
    public List<TimeSlot> getPastByVeterinarian(
            int veterinarianId) throws DAOException {

        List<TimeSlot> result = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(
                             GET_PAST_BY_VETERINARIAN
                     )) {

            ps.setInt(1, veterinarianId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapToTimeSlot(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento delle fasce orarie passate: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    @Override
    public TimeSlot findById(int id)
            throws DAOException {

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(FIND_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapToTimeSlot(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento della fascia oraria: "
                            + e.getMessage(),
                    e
            );
        }

        return null;
    }

    @Override
    public void save(
            TimeSlot slot,
            int veterinarianId) throws DAOException {

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     SAVE,
                     Statement.RETURN_GENERATED_KEYS
             )) {

            ps.setInt(1, veterinarianId);
            ps.setDate(
                    2,
                    Date.valueOf(slot.getDate())
            );
            ps.setTime(
                    3,
                    Time.valueOf(slot.getStartTime())
            );
            ps.setTime(
                    4,
                    Time.valueOf(slot.getEndTime())
            );

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    slot.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore durante il salvataggio della fascia oraria: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void delete(
            int slotId,
            int veterinarianId) throws DAOException {

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(DELETE_SLOT)) {

            ps.setInt(1, slotId);
            ps.setInt(2, veterinarianId);

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new DAOException(
                        "Fascia oraria non trovata o già prenotata."
                );
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore durante l'eliminazione della fascia oraria: "
                            + e.getMessage(),
                    e
            );
        }
    }

    private TimeSlot mapToTimeSlot(ResultSet rs)
            throws SQLException {

        TimeSlot slot = new TimeSlot(
                rs.getInt("id"),
                rs.getDate("date").toLocalDate(),
                rs.getTime("start_time").toLocalTime(),
                rs.getTime("end_time").toLocalTime()
        );

        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setId(rs.getInt("veterinarian_id"));
        slot.setVeterinarian(veterinarian);

        slot.setAvailable(
                rs.getBoolean("available")
        );

        Timestamp reservedUntil =
                rs.getTimestamp("reserved_until");

        if (reservedUntil != null) {
            slot.setReservedUntil(
                    reservedUntil.toLocalDateTime()
            );
        }

        return slot;
    }
}
