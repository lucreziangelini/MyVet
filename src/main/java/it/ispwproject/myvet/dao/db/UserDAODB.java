package it.ispwproject.myvet.dao.db;

import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.dao.UserDAO;
import it.ispwproject.myvet.enumerator.Role;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.Admin;
import it.ispwproject.myvet.model.PetOwner;
import it.ispwproject.myvet.model.User;
import it.ispwproject.myvet.model.Veterinarian;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAODB implements UserDAO {

    private static final String FIND_BY_EMAIL =
            "SELECT u.id, u.name, u.surname, u.email, u.role, " +
                    "vd.bio, vd.specialization " +
                    "FROM user u " +
                    "LEFT JOIN veterinarian_detail vd " +
                    "ON u.id = vd.user_id " +
                    "WHERE u.email = ?";

    private static final String UPDATE_EMAIL =
            "UPDATE user SET email = ? WHERE id = ?";

    private static final String GET_ALL =
            "SELECT u.id, u.name, u.surname, u.email, u.role, " +
                    "vd.bio, vd.specialization " +
                    "FROM user u " +
                    "LEFT JOIN veterinarian_detail vd " +
                    "ON u.id = vd.user_id";

    @Override
    public void updateEmail(
            int id,
            String newEmail) throws DAOException {

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(UPDATE_EMAIL)) {

            ps.setString(1, newEmail);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new DAOException(
                        "Utente non trovato (ID: " + id + ")"
                );
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore aggiornamento email: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public User findByEmail(String email)
            throws DAOException {

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(FIND_BY_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DAOException(
                            "Utente non trovato: " + email
                    );
                }

                int id = rs.getInt("id");
                String name = rs.getString("name");
                String surname = rs.getString("surname");

                Role role = Role.valueOf(
                        rs.getString("role").toUpperCase()
                );

                String bio = rs.getString("bio");
                String specialization =
                        rs.getString("specialization");

                return buildUser(
                        id,
                        name,
                        surname,
                        email,
                        role,
                        bio,
                        specialization
                );
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore caricamento utente: "
                            + e.getMessage(),
                    e
            );
        }
    }

    private User buildUser(
            int id,
            String name,
            String surname,
            String email,
            Role role,
            String bio,
            String specialization) {

        return switch (role) {
            case PET_OWNER ->
                    new PetOwner(
                            id,
                            name,
                            surname,
                            email,
                            null
                    );

            case VETERINARIAN ->
                    new Veterinarian(
                            id,
                            name,
                            surname,
                            email,
                            null,
                            bio,
                            specialization
                    );

            case ADMIN ->
                    new Admin(
                            id,
                            name,
                            surname,
                            email,
                            null
                    );
        };
    }

    @Override
    public List<User> getAll()
            throws DAOException {

        List<User> result = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(GET_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                String email = rs.getString("email");

                Role role = Role.valueOf(
                        rs.getString("role").toUpperCase()
                );

                String bio = rs.getString("bio");
                String specialization =
                        rs.getString("specialization");

                result.add(
                        buildUser(
                                id,
                                name,
                                surname,
                                email,
                                role,
                                bio,
                                specialization
                        )
                );
            }

        } catch (SQLException e) {
            throw new DAOException(
                    "Errore caricamento utenti: "
                            + e.getMessage(),
                    e
            );
        }

        return result;
    }
}