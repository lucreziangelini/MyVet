package it.ispwproject.myvet.controller.applicativo;

import it.ispwproject.myvet.bean.SessionBean;
import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.dao.DAOFactory;
import it.ispwproject.myvet.dao.UserDAO;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.exception.LoginException;
import it.ispwproject.myvet.model.Credentials;
import it.ispwproject.myvet.model.User;
import it.ispwproject.myvet.util.PasswordUtils;
import it.ispwproject.myvet.pattern.singleton.SessionManager;

import java.sql.SQLException;

public class LoginController {

    public enum LoginResult {
        SUCCESSO_PET_OWNER,
        SUCCESSO_VETERINARIAN,
        SUCCESSO_ADMIN
    }

    public LoginResult login(String email, String password)
            throws LoginException {

        if (email == null || email.isBlank()
                || password == null || password.isBlank()) {
            throw new LoginException(
                    "Inserisci sia email che password."
            );
        }

        if (SessionManager.getInstance().isLoggedIn()) {
            throw new LoginException(
                    "È già presente una sessione autenticata."
            );
        }

        String hashedPassword =
                PasswordUtils.hash(password);

        Credentials credentials =
                DAOFactory.getLoginDAO()
                        .execute(email, hashedPassword);

        if (!DAOFactory.MEMORY.equalsIgnoreCase(
                DAOFactory.getPersistence())) {

            try {
                ConnectionFactory.changeRole(
                        credentials.getRole()
                );
            } catch (SQLException e) {
                throw new LoginException(
                        "Errore durante il cambio ruolo: "
                                + e.getMessage(),
                        e
                );
            }
        }

        User user;

        try {
            UserDAO userDAO = DAOFactory.getUserDAO();
            user = userDAO.findByEmail(email);
        } catch (DAOException e) {
            throw new LoginException(
                    "Errore nel caricamento utente: "
                            + e.getMessage(),
                    e
            );
        }

        SessionManager.getInstance()
                .setLoggedUser(user);

        SessionManager.getInstance()
                .setSessionBean(
                        new SessionBean(
                                user.getEmail(),
                                credentials.getRole()
                        )
                );

        return switch (credentials.getRole()) {
            case PET_OWNER ->
                    LoginResult.SUCCESSO_PET_OWNER;
            case VETERINARIAN ->
                    LoginResult.SUCCESSO_VETERINARIAN;
            case ADMIN ->
                    LoginResult.SUCCESSO_ADMIN;
        };
    }
}
