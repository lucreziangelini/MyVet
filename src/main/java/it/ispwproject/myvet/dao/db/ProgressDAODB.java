package it.ispwproject.myvet.dao.db;

import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.dao.ProgressDAO;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Pet;
import it.ispwproject.myvet.model.Progress;
import it.ispwproject.myvet.model.Veterinarian;

import java.sql.*;

public class ProgressDAODB implements ProgressDAO {

    private static final String SAVE =
            "INSERT INTO progress (veterinarian_id, pet_id, notes) VALUES (?, ?, ?)";

    private static final String UPDATE =
            "UPDATE progress SET notes = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE veterinarian_id = ? AND pet_id = ?";

    private static final String FIND_BY_PET_AND_VETERINARIAN =
            "SELECT pr.id, pr.notes, pr.updated_at, " +
                    "       u_v.id v_id, u_v.name v_name, u_v.surname v_surname, u_v.email v_email, " +
                    "       p.id p_id, p.name p_name, p.species p_species, " +
                    "       p.breed p_breed, p.birth_date p_birth_date " +
                    "FROM progress pr " +
                    "JOIN user u_v ON pr.veterinarian_id = u_v.id " +
                    "JOIN pet p ON pr.pet_id = p.id " +
                    "WHERE pr.veterinarian_id = ? AND pr.pet_id = ?";

    @Override
    public void saveOrUpdate(Progress progress) throws DAOException {
        Progress existing = findByPetAndVeterinarian(
                progress.getVeterinarian().getId(),
                progress.getPet().getId());

        if (existing == null) save(progress);
        else update(progress);
    }

    private void save(Progress progress) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     SAVE, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, progress.getVeterinarian().getId());
            ps.setInt(2, progress.getPet().getId());
            ps.setString(3, progress.getNotes());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    progress.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore salvataggio progresso: " + e.getMessage(),
                    e
            );
        }
    }

    private void update(Progress progress) throws DAOException {
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE)) {

            ps.setString(1, progress.getNotes());
            ps.setInt(2, progress.getVeterinarian().getId());
            ps.setInt(3, progress.getPet().getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore aggiornamento progresso: " + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public Progress findByPetAndVeterinarian(
            int veterinarianId,
            int petId) throws DAOException {

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     FIND_BY_PET_AND_VETERINARIAN)) {

            ps.setInt(1, veterinarianId);
            ps.setInt(2, petId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Veterinarian veterinarian = new Veterinarian();
                    veterinarian.setId(rs.getInt("v_id"));
                    veterinarian.setName(rs.getString("v_name"));
                    veterinarian.setSurname(rs.getString("v_surname"));
                    veterinarian.setEmail(rs.getString("v_email"));

                    Pet pet = new Pet(
                            rs.getInt("p_id"),
                            rs.getString("p_name"),
                            rs.getString("p_species"),
                            rs.getString("p_breed"),
                            rs.getDate("p_birth_date") == null
                                    ? null
                                    : rs.getDate("p_birth_date").toLocalDate()
                    );

                    Progress progress = new Progress(
                            veterinarian,
                            pet,
                            rs.getString("notes")
                    );

                    progress.setId(rs.getInt("id"));

                    Timestamp updatedAt =
                            rs.getTimestamp("updated_at");

                    if (updatedAt != null) {
                        progress.setUpdatedAt(
                                updatedAt.toLocalDateTime()
                        );
                    }

                    return progress;
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore caricamento progresso: " + e.getMessage(),
                    e
            );
        }

        return null;
    }
}