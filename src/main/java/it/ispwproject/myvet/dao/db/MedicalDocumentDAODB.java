package it.ispwproject.myvet.dao.db;

import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.dao.MedicalDocumentDAO;
import it.ispwproject.myvet.enumerator.DocumentType;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.MedicalDocument;
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

public class MedicalDocumentDAODB
        implements MedicalDocumentDAO {

    private static final String SAVE =
            "INSERT INTO medical_document " +
                    "(pet_id, veterinarian_id, title, type, " +
                    "storage_reference, uploaded_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String FIND_BY_PET =
            "SELECT md.id, md.title, md.type, " +
                    "md.storage_reference, md.uploaded_at, " +

                    "p.id p_id, p.name p_name, " +
                    "p.species p_species, p.breed p_breed, " +
                    "p.birth_date p_birth_date, " +

                    "u_v.id v_id, u_v.name v_name, " +
                    "u_v.surname v_surname, u_v.email v_email " +

                    "FROM medical_document md " +
                    "JOIN pet p ON md.pet_id = p.id " +
                    "JOIN user u_v " +
                    "ON md.veterinarian_id = u_v.id " +
                    "WHERE md.pet_id = ? " +
                    "ORDER BY md.uploaded_at DESC";

    @Override
    public void save(MedicalDocument document)
            throws DAOException {

        try (Connection conn =
                     ConnectionFactory.getConnection();

             PreparedStatement ps =
                     conn.prepareStatement(
                             SAVE,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            ps.setInt(
                    1,
                    document.getPet().getId()
            );

            ps.setInt(
                    2,
                    document.getVeterinarian().getId()
            );

            ps.setString(
                    3,
                    document.getTitle()
            );

            ps.setString(
                    4,
                    document.getType().name()
            );

            ps.setString(
                    5,
                    document.getStorageReference()
            );

            ps.setTimestamp(
                    6,
                    Timestamp.valueOf(
                            document.getUploadedAt()
                    )
            );

            ps.executeUpdate();

            try (ResultSet keys =
                         ps.getGeneratedKeys()) {

                if (keys.next()) {
                    document.setId(
                            keys.getInt(1)
                    );
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel salvataggio del documento: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public List<MedicalDocument> findByPet(
            int petId) throws DAOException {

        List<MedicalDocument> result =
                new ArrayList<>();

        try (Connection conn =
                     ConnectionFactory.getConnection();

             PreparedStatement ps =
                     conn.prepareStatement(FIND_BY_PET)) {

            ps.setInt(1, petId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(
                            mapToMedicalDocument(rs)
                    );
                }
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore nel caricamento dei documenti: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }

    private MedicalDocument mapToMedicalDocument(
            ResultSet rs) throws SQLException {

        Pet pet = new Pet(
                rs.getInt("p_id"),
                rs.getString("p_name"),
                rs.getString("p_species"),
                rs.getString("p_breed"),
                rs.getDate("p_birth_date") == null
                        ? null
                        : rs.getDate(
                        "p_birth_date"
                ).toLocalDate()
        );

        Veterinarian veterinarian =
                new Veterinarian();

        veterinarian.setId(
                rs.getInt("v_id")
        );

        veterinarian.setName(
                rs.getString("v_name")
        );

        veterinarian.setSurname(
                rs.getString("v_surname")
        );

        veterinarian.setEmail(
                rs.getString("v_email")
        );

        Timestamp uploadedAt =
                rs.getTimestamp("uploaded_at");

        return new MedicalDocument(
                rs.getInt("id"),
                pet,
                veterinarian,
                rs.getString("title"),
                DocumentType.valueOf(
                        rs.getString("type")
                ),
                rs.getString("storage_reference"),
                uploadedAt == null
                        ? null
                        : uploadedAt.toLocalDateTime()
        );
    }
}