package it.ispwproject.myvet.dao.db;

import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.dao.PetOwnerDAO;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.PetOwner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PetOwnerDAODB implements PetOwnerDAO {

    private static final String FIND_BY_ID =
            "SELECT id, name, surname, email " +
                    "FROM user " +
                    "WHERE id = ? AND role = 'PET_OWNER'";

    private static final String FIND_BY_PET_ID =
            "SELECT u.id, u.name, u.surname, u.email " +
                    "FROM user u " +
                    "JOIN pet p ON u.id = p.owner_id " +
                    "WHERE p.id = ? AND u.role = 'PET_OWNER'";

    private static final String GET_BY_VETERINARIAN =
            "SELECT DISTINCT u.id, u.name, u.surname, u.email " +
                    "FROM user u " +
                    "JOIN booking b ON u.id = b.pet_owner_id " +
                    "WHERE b.veterinarian_id = ? " +
                    "ORDER BY u.name";

    private static final String ADD_FAVOURITE_VETERINARIAN =
            "INSERT IGNORE INTO pet_owner_favourite_veterinarian " +
                    "(pet_owner_id, veterinarian_id) VALUES (?, ?)";

    private static final String REMOVE_FAVOURITE_VETERINARIAN =
            "DELETE FROM pet_owner_favourite_veterinarian " +
                    "WHERE pet_owner_id = ? AND veterinarian_id = ?";

    private static final String IS_FAVOURITE_VETERINARIAN =
            "SELECT COUNT(*) " +
                    "FROM pet_owner_favourite_veterinarian " +
                    "WHERE pet_owner_id = ? AND veterinarian_id = ?";

    @Override
    public PetOwner findById(int id) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(FIND_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapToPetOwner(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento del proprietario: "
                            + e.getMessage(),
                    e
            );
        }

        return null;
    }

    @Override
    public PetOwner findByPetId(int petId)
            throws DAOException {

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(FIND_BY_PET_ID)) {

            ps.setInt(1, petId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapToPetOwner(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento del proprietario: "
                            + e.getMessage(),
                    e
            );
        }

        return null;
    }

    @Override
    public List<PetOwner> getByVeterinarian(
            int veterinarianId) throws DAOException {

        List<PetOwner> result = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(GET_BY_VETERINARIAN)) {

            ps.setInt(1, veterinarianId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapToPetOwner(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento dei proprietari: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    @Override
    public void addFavouriteVeterinarian(
            int petOwnerId,
            int veterinarianId) throws DAOException {

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(
                             ADD_FAVOURITE_VETERINARIAN
                     )) {

            ps.setInt(1, petOwnerId);
            ps.setInt(2, veterinarianId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nell'aggiunta del veterinario preferito: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void removeFavouriteVeterinarian(
            int petOwnerId,
            int veterinarianId) throws DAOException {

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(
                             REMOVE_FAVOURITE_VETERINARIAN
                     )) {

            ps.setInt(1, petOwnerId);
            ps.setInt(2, veterinarianId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nella rimozione del veterinario preferito: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public boolean isFavouriteVeterinarian(
            int petOwnerId,
            int veterinarianId) throws DAOException {

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(
                             IS_FAVOURITE_VETERINARIAN
                     )) {

            ps.setInt(1, petOwnerId);
            ps.setInt(2, veterinarianId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        && rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel controllo del veterinario preferito: "
                            + e.getMessage(),
                    e
            );
        }
    }

    private PetOwner mapToPetOwner(ResultSet rs)
            throws SQLException {

        return new PetOwner(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("email"),
                null
        );
    }
}
