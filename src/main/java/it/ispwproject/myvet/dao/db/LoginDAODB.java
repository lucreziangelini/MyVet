package it.ispwproject.myvet.dao.db;

import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.dao.LoginDAO;
import it.ispwproject.myvet.enumerator.Role;
import it.ispwproject.myvet.exception.LoginException;
import it.ispwproject.myvet.model.Credentials;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class LoginDAODB implements LoginDAO {

    @Override
    public Credentials execute(
            String email,
            String hashedPassword) throws LoginException {

        try (Connection connection =
                     ConnectionFactory.getConnection();

             CallableStatement statement =
                     connection.prepareCall(
                             "{call login(?, ?, ?, ?, ?, ?)}"
                     )) {

            statement.setString(1, email);
            statement.setString(2, hashedPassword);

            statement.registerOutParameter(
                    3,
                    Types.INTEGER
            );

            statement.registerOutParameter(
                    4,
                    Types.VARCHAR
            );

            statement.registerOutParameter(
                    5,
                    Types.VARCHAR
            );

            statement.registerOutParameter(
                    6,
                    Types.VARCHAR
            );

            statement.execute();

            String roleValue =
                    statement.getString(6);

            if (roleValue == null
                    || roleValue.equals("NOT_FOUND")) {

                throw new LoginException(
                        "Credenziali non valide. Riprova."
                );
            }

            Role role = Role.valueOf(
                    roleValue.toUpperCase()
            );

            return new Credentials(
                    email,
                    hashedPassword,
                    role
            );

        } catch (SQLException e) {
            throw new LoginException(
                    "Errore del database durante l'accesso: "
                            + e.getMessage(),
                    e
            );
        }
    }
}
