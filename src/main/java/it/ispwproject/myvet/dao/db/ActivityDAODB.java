package it.ispwproject.myvet.dao.db;

import it.ispwproject.myvet.dao.ActivityDAO;
import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.CareActivity;
import it.ispwproject.myvet.model.Pet;
import it.ispwproject.myvet.model.Veterinarian;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ActivityDAODB implements ActivityDAO {

    private static final String SAVE =
            "INSERT INTO activity " +
                    "(veterinarian_id, pet_id, description) " +
                    "VALUES (?, ?, ?)";

    private static final String GET_BY_PET_AND_VETERINARIAN =
            "SELECT a.id, a.description, a.completed, a.created_at, " +
                    "u_v.id v_id, u_v.name v_name, u_v.surname v_surname, " +
                    "u_v.email v_email, " +
                    "p.id p_id, p.name p_name, p.species p_species, " +
                    "p.breed p_breed, p.birth_date p_birth_date " +
                    "FROM activity a " +
                    "JOIN user u_v ON a.veterinarian_id = u_v.id " +
                    "JOIN pet p ON a.pet_id = p.id " +
                    "WHERE a.veterinarian_id = ? AND a.pet_id = ? " +
                    "ORDER BY a.created_at DESC";

    private static final String GET_BY_PET_OWNER =
            "SELECT a.id, a.description, a.completed, a.created_at, " +
                    "u_v.id v_id, u_v.name v_name, u_v.surname v_surname, " +
                    "u_v.email v_email, " +
                    "p.id p_id, p.name p_name, p.species p_species, " +
                    "p.breed p_breed, p.birth_date p_birth_date " +
                    "FROM activity a " +
                    "JOIN user u_v ON a.veterinarian_id = u_v.id " +
                    "JOIN pet p ON a.pet_id = p.id " +
                    "WHERE p.owner_id = ? " +
                    "ORDER BY a.completed ASC, a.created_at DESC";

    private static final String FIND_BY_ID_FOR_OWNER =
            "SELECT a.id, a.description, a.completed, a.created_at, " +
                    "u_v.id v_id, u_v.name v_name, u_v.surname v_surname, " +
                    "u_v.email v_email, " +
                    "p.id p_id, p.name p_name, p.species p_species, " +
                    "p.breed p_breed, p.birth_date p_birth_date " +
                    "FROM activity a " +
                    "JOIN user u_v ON a.veterinarian_id = u_v.id " +
                    "JOIN pet p ON a.pet_id = p.id " +
                    "WHERE a.id = ? AND p.owner_id = ?";

    private static final String MARK_AS_COMPLETED =
            "UPDATE activity SET completed = TRUE " +
                    "WHERE id = ? " +
                    "AND pet_id IN (" +
                    "SELECT id FROM pet WHERE owner_id = ?" +
                    ")";

    @Override
    public void save(CareActivity activity)
            throws DAOException {

        try (Connection connection =
                     ConnectionFactory.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(
                             SAVE,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setInt(
                    1,
                    activity.getVeterinarian().getId()
            );

            statement.setInt(
                    2,
                    activity.getPet().getId()
            );

            statement.setString(
                    3,
                    activity.getDescription()
            );

            statement.executeUpdate();

            try (ResultSet keys =
                         statement.getGeneratedKeys()) {

                if (keys.next()) {
                    activity.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore durante il salvataggio dell'attività: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public List<CareActivity> getByPetAndVeterinarian(
            int veterinarianId,
            int petId) throws DAOException {

        List<CareActivity> result =
                new ArrayList<>();

        try (Connection connection =
                     ConnectionFactory.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(
                             GET_BY_PET_AND_VETERINARIAN
                     )) {

            statement.setInt(1, veterinarianId);
            statement.setInt(2, petId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    result.add(
                            mapToActivity(resultSet)
                    );
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento delle attività: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    @Override
    public List<CareActivity> getByPetOwner(
            int petOwnerId) throws DAOException {

        List<CareActivity> result =
                new ArrayList<>();

        try (Connection connection =
                     ConnectionFactory.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(
                             GET_BY_PET_OWNER
                     )) {

            statement.setInt(1, petOwnerId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    result.add(
                            mapToActivity(resultSet)
                    );
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento delle attività: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    @Override
    public void markAsCompleted(
            int activityId,
            int petOwnerId) throws DAOException {

        try (Connection connection =
                     ConnectionFactory.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(
                             MARK_AS_COMPLETED
                     )) {

            statement.setInt(1, activityId);
            statement.setInt(2, petOwnerId);

            int rows = statement.executeUpdate();

            if (rows == 0) {
                throw new DAOException(
                        "Attività non trovata o non autorizzata."
                );
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore durante l'aggiornamento dell'attività: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public CareActivity findByIdForOwner(
            int activityId,
            int petOwnerId) throws DAOException {

        try (Connection connection =
                     ConnectionFactory.getConnection();

             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_ID_FOR_OWNER
                     )) {

            statement.setInt(1, activityId);
            statement.setInt(2, petOwnerId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapToActivity(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento dell'attività: "
                            + e.getMessage(),
                    e
            );
        }

        return null;
    }

    private CareActivity mapToActivity(
            ResultSet resultSet) throws SQLException {

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

        Pet pet = new Pet();

        pet.setId(
                resultSet.getInt("p_id")
        );

        pet.setName(
                resultSet.getString("p_name")
        );

        pet.setSpecies(
                resultSet.getString("p_species")
        );

        pet.setBreed(
                resultSet.getString("p_breed")
        );

        java.sql.Date birthDate =
                resultSet.getDate("p_birth_date");

        if (birthDate != null) {
            pet.setBirthDate(
                    birthDate.toLocalDate()
            );
        }

        CareActivity activity =
                new CareActivity();

        activity.setId(
                resultSet.getInt("id")
        );

        activity.setVeterinarian(veterinarian);
        activity.setPet(pet);

        activity.setDescription(
                resultSet.getString("description")
        );

        activity.setCompleted(
                resultSet.getBoolean("completed")
        );

        Timestamp createdAt =
                resultSet.getTimestamp("created_at");

        if (createdAt != null) {
            activity.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        return activity;
    }
}