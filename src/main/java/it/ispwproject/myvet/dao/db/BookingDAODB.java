package it.ispwproject.myvet.dao.db;

import it.ispwproject.myvet.dao.AbstractBookingDAO;
import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.enumerator.BookingStatus;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Booking;
import it.ispwproject.myvet.model.Pet;
import it.ispwproject.myvet.model.PetOwner;
import it.ispwproject.myvet.model.TimeSlot;
import it.ispwproject.myvet.model.Veterinarian;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BookingDAODB extends AbstractBookingDAO {

    private static final String INSERT_BOOKING =
            "INSERT INTO booking " +
                    "(pet_owner_id, veterinarian_id, pet_id, slot_id, status) " +
                    "VALUES (?, ?, ?, ?, 'CONFIRMED')";

    private static final String CANCEL_BOOKING =
            "UPDATE booking SET status = 'CANCELLED' " +
                    "WHERE id = ? AND pet_owner_id = ?";

    private static final String FREE_SLOT =
            "UPDATE time_slot SET available = TRUE " +
                    "WHERE id = (" +
                    "SELECT slot_id FROM booking " +
                    "WHERE id = ? AND pet_owner_id = ?" +
                    ")";

    private static final String UPDATE_SLOT_AVAILABILITY =
            "UPDATE time_slot SET available = ? WHERE id = ?";

    private static final String SELECT_BOOKINGS =
            "SELECT b.id, b.status, b.created_at, " +

                    "u_o.id o_id, u_o.name o_name, " +
                    "u_o.surname o_surname, u_o.email o_email, " +

                    "u_v.id v_id, u_v.name v_name, " +
                    "u_v.surname v_surname, u_v.email v_email, " +

                    "p.id p_id, p.name p_name, p.species p_species, " +
                    "p.breed p_breed, p.birth_date p_birth_date, " +

                    "ts.id ts_id, ts.date ts_date, " +
                    "ts.start_time, ts.end_time, ts.available " +

                    "FROM booking b " +
                    "JOIN user u_o ON b.pet_owner_id = u_o.id " +
                    "JOIN user u_v ON b.veterinarian_id = u_v.id " +
                    "JOIN pet p ON b.pet_id = p.id " +
                    "JOIN time_slot ts ON b.slot_id = ts.id ";

    private static final String FIND_BY_PET_OWNER =
            SELECT_BOOKINGS +
                    "WHERE b.pet_owner_id = ? " +
                    "ORDER BY b.created_at DESC";

    private static final String FIND_BY_VETERINARIAN =
            SELECT_BOOKINGS +
                    "WHERE b.veterinarian_id = ? " +
                    "AND b.status = 'CONFIRMED' " +
                    "ORDER BY ts.date ASC, ts.start_time ASC";

    private static final String FIND_PAST_BY_PET_OWNER =
            SELECT_BOOKINGS +
                    "WHERE b.pet_owner_id = ? " +
                    "AND b.status = 'CONFIRMED' " +
                    "AND (" +
                    "ts.date < CURDATE() " +
                    "OR (ts.date = CURDATE() " +
                    "AND ts.end_time < CURTIME())" +
                    ") " +
                    "ORDER BY ts.date DESC, ts.start_time DESC";

    private static final String FIND_ALL =
            SELECT_BOOKINGS +
                    "ORDER BY b.created_at DESC";

    @Override
    public void save(Booking booking) throws DAOException {
        try (Connection connection =
                     ConnectionFactory.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(
                             INSERT_BOOKING,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setInt(
                    1,
                    booking.getPetOwner().getId()
            );

            statement.setInt(
                    2,
                    booking.getVeterinarian().getId()
            );

            statement.setInt(
                    3,
                    booking.getPet().getId()
            );

            statement.setInt(
                    4,
                    booking.getTimeSlot().getId()
            );

            statement.executeUpdate();

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (keys.next()) {
                    booking.setId(keys.getInt(1));
                }
            }

            booking.setStatus(BookingStatus.CONFIRMED);

            updateSlotAvailability(
                    connection,
                    booking.getTimeSlot().getId(),
                    false
            );

            addToCache(booking);

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore durante il salvataggio: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public List<Booking> findByPetOwner(
            int petOwnerId) throws DAOException {

        List<Booking> cached =
                findInCacheByPetOwner(petOwnerId);

        if (!cached.isEmpty()) {
            return cached;
        }

        List<Booking> result = new ArrayList<>();

        try (Connection connection =
                     ConnectionFactory.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_PET_OWNER
                     )) {

            statement.setInt(1, petOwnerId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    Booking booking =
                            mapToBooking(resultSet);

                    addToCache(booking);
                    result.add(booking);
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento degli appuntamenti: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    @Override
    public List<Booking> findByVeterinarian(
            int veterinarianId) throws DAOException {

        List<Booking> result = new ArrayList<>();

        try (Connection connection =
                     ConnectionFactory.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_VETERINARIAN
                     )) {

            statement.setInt(1, veterinarianId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    result.add(
                            mapToBooking(resultSet)
                    );
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento degli appuntamenti "
                            + "del veterinario: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    @Override
    public List<Booking> findPastByPetOwner(
            int petOwnerId) throws DAOException {

        List<Booking> result = new ArrayList<>();

        try (Connection connection =
                     ConnectionFactory.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_PAST_BY_PET_OWNER
                     )) {

            statement.setInt(1, petOwnerId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    result.add(
                            mapToBooking(resultSet)
                    );
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento degli appuntamenti passati: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    @Override
    public List<Booking> findAll()
            throws DAOException {

        List<Booking> result = new ArrayList<>();

        try (Connection connection =
                     ConnectionFactory.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(FIND_ALL);

             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {
                result.add(
                        mapToBooking(resultSet)
                );
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento degli appuntamenti: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    @Override
    public void cancel(
            int bookingId,
            int petOwnerId) throws DAOException {

        try (Connection connection =
                     ConnectionFactory.getConnection()) {

            connection.setAutoCommit(false);

            executeCancel(
                    connection,
                    bookingId,
                    petOwnerId
            );

            connection.commit();
            connection.setAutoCommit(true);

            updateInCache(bookingId);

            identityMap.removeIf(booking ->
                    booking.getPetOwner() != null
                            && booking.getPetOwner().getId()
                            == petOwnerId
            );

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore durante l'annullamento: "
                            + e.getMessage(),
                    e
            );
        }
    }

    private void executeCancel(
            Connection connection,
            int bookingId,
            int petOwnerId)
            throws SQLException, DAOException {

        freeSlot(
                connection,
                bookingId,
                petOwnerId
        );

        cancelBooking(
                connection,
                bookingId,
                petOwnerId
        );
    }

    private void freeSlot(
            Connection connection,
            int bookingId,
            int petOwnerId) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(FREE_SLOT)) {

            statement.setInt(1, bookingId);
            statement.setInt(2, petOwnerId);
            statement.executeUpdate();
        }
    }

    private void cancelBooking(
            Connection connection,
            int bookingId,
            int petOwnerId)
            throws SQLException, DAOException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             CANCEL_BOOKING
                     )) {

            statement.setInt(1, bookingId);
            statement.setInt(2, petOwnerId);

            int rows = statement.executeUpdate();

            if (rows == 0) {
                throw new DAOException(
                        "Appuntamento non trovato o non autorizzato."
                );
            }
        }
    }

    private void updateSlotAvailability(
            Connection connection,
            int slotId,
            boolean available) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             UPDATE_SLOT_AVAILABILITY
                     )) {

            statement.setBoolean(1, available);
            statement.setInt(2, slotId);
            statement.executeUpdate();
        }
    }

    private Booking mapToBooking(
            ResultSet resultSet) throws SQLException {

        PetOwner owner = new PetOwner(
                resultSet.getInt("o_id"),
                resultSet.getString("o_name"),
                resultSet.getString("o_surname"),
                resultSet.getString("o_email"),
                null
        );

        Veterinarian veterinarian =
                new Veterinarian();

        veterinarian.setId(
                resultSet.getInt("v_id")
        );

        veterinarian.setName(
                resultSet.getString("v_name")
        );

        veterinarian.setSurname(
                resultSet.getString("v_surname")
        );

        veterinarian.setEmail(
                resultSet.getString("v_email")
        );

        Pet pet = new Pet(
                resultSet.getInt("p_id"),
                resultSet.getString("p_name"),
                resultSet.getString("p_species"),
                resultSet.getString("p_breed"),
                resultSet.getDate("p_birth_date") == null
                        ? null
                        : resultSet.getDate(
                        "p_birth_date"
                ).toLocalDate()
        );

        TimeSlot slot = new TimeSlot(
                resultSet.getInt("ts_id"),
                veterinarian,
                resultSet.getDate(
                        "ts_date"
                ).toLocalDate(),
                resultSet.getTime(
                        "start_time"
                ).toLocalTime(),
                resultSet.getTime(
                        "end_time"
                ).toLocalTime()
        );

        slot.setAvailable(
                resultSet.getBoolean("available")
        );

        Booking booking = new Booking(
                owner,
                veterinarian,
                pet,
                slot
        );

        booking.setId(
                resultSet.getInt("id")
        );

        booking.setStatus(
                BookingStatus.valueOf(
                        resultSet.getString("status")
                )
        );

        Timestamp createdAt =
                resultSet.getTimestamp("created_at");

        if (createdAt != null) {
            booking.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        return booking;
    }
}