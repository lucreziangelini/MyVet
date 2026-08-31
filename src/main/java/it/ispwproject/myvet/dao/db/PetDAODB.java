package it.ispwproject.myvet.dao.db;

import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.dao.PetDAO;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Pet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PetDAODB implements PetDAO {

    private static final String INSERT_PET =
            "INSERT INTO pet " +
                    "(owner_id, name, species, breed, birth_date) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_PET =
            "UPDATE pet SET name = ?, species = ?, breed = ?, " +
                    "birth_date = ? WHERE id = ? AND owner_id = ?";

    private static final String FIND_BY_ID =
            "SELECT id, name, species, breed, birth_date " +
                    "FROM pet WHERE id = ?";

    private static final String GET_BY_OWNER =
            "SELECT id, name, species, breed, birth_date " +
                    "FROM pet WHERE owner_id = ? " +
                    "ORDER BY name";

    private static final String GET_BY_VETERINARIAN =
            "SELECT DISTINCT p.id, p.name, p.species, " +
                    "p.breed, p.birth_date " +
                    "FROM pet p " +
                    "JOIN booking b ON p.id = b.pet_id " +
                    "WHERE b.veterinarian_id = ? " +
                    "AND b.status = 'CONFIRMED' " +
                    "ORDER BY p.name";

    @Override
    public void save(Pet pet, int petOwnerId)
            throws DAOException {

        try (Connection conn =
                     ConnectionFactory.getConnection();

             PreparedStatement ps =
                     conn.prepareStatement(
                             INSERT_PET,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            ps.setInt(1, petOwnerId);
            ps.setString(2, pet.getName());
            ps.setString(3, pet.getSpecies());
            ps.setString(4, pet.getBreed());

            if (pet.getBirthDate() == null) {
                ps.setNull(5, java.sql.Types.DATE);
            } else {
                ps.setDate(
                        5,
                        java.sql.Date.valueOf(
                                pet.getBirthDate()
                        )
                );
            }

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    pet.setId(keys.getInt(1));
                    return;
                }
            }

            throw new DAOException(
                    "ID dell'animale non generato."
            );

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel salvataggio dell'animale: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void update(Pet pet, int petOwnerId)
            throws DAOException {

        try (Connection conn =
                     ConnectionFactory.getConnection();

             PreparedStatement ps =
                     conn.prepareStatement(UPDATE_PET)) {

            ps.setString(1, pet.getName());
            ps.setString(2, pet.getSpecies());
            ps.setString(3, pet.getBreed());

            if (pet.getBirthDate() == null) {
                ps.setNull(4, java.sql.Types.DATE);
            } else {
                ps.setDate(
                        4,
                        java.sql.Date.valueOf(
                                pet.getBirthDate()
                        )
                );
            }

            ps.setInt(5, pet.getId());
            ps.setInt(6, petOwnerId);

            if (ps.executeUpdate() == 0) {
                throw new DAOException(
                        "Animale non trovato o non appartenente "
                                + "all'utente autenticato."
                );
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nell'aggiornamento dell'animale: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public Pet findById(int petId)
            throws DAOException {

        try (Connection conn =
                     ConnectionFactory.getConnection();

             PreparedStatement ps =
                     conn.prepareStatement(FIND_BY_ID)) {

            ps.setInt(1, petId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapToPet(rs);
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento dell'animale: "
                            + e.getMessage(),
                    e
            );
        }

        return null;
    }

    @Override
    public List<Pet> getByOwner(
            int petOwnerId) throws DAOException {

        List<Pet> result = new ArrayList<>();

        try (Connection conn =
                     ConnectionFactory.getConnection();

             PreparedStatement ps =
                     conn.prepareStatement(GET_BY_OWNER)) {

            ps.setInt(1, petOwnerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapToPet(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento degli animali: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    @Override
    public List<Pet> getByVeterinarian(
            int veterinarianId) throws DAOException {

        List<Pet> result = new ArrayList<>();

        try (Connection conn =
                     ConnectionFactory.getConnection();

             PreparedStatement ps =
                     conn.prepareStatement(
                             GET_BY_VETERINARIAN
                     )) {

            ps.setInt(1, veterinarianId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapToPet(rs));
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento degli animali: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    private Pet mapToPet(ResultSet rs)
            throws SQLException {

        return new Pet(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("species"),
                rs.getString("breed"),
                rs.getDate("birth_date") == null
                        ? null
                        : rs.getDate(
                        "birth_date"
                ).toLocalDate()
        );
    }
}
