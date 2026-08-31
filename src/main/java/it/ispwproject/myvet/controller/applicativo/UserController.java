package it.ispwproject.myvet.controller.applicativo;

import it.ispwproject.myvet.dao.DAOFactory;
import it.ispwproject.myvet.dao.UserDAO;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.User;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.util.ValidationUtils;

public class UserController {

    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = DAOFactory.getUserDAO();
    }

    public void updateEmail(String newEmail)
            throws DAOException {

        if (newEmail == null || newEmail.isBlank()) {
            throw new DAOException(
                    "L'email non può essere vuota."
            );
        }

        if (!ValidationUtils.isValidEmail(newEmail)) {
            throw new DAOException(
                    "Email non valida."
            );
        }

        SessionManager sessionManager =
                SessionManager.getInstance();

        User loggedUser = sessionManager.getLoggedUser();

        if (loggedUser == null) {
            throw new DAOException(
                    "Nessun utente autenticato."
            );
        }

        userDAO.updateEmail(loggedUser.getId(), newEmail);

        sessionManager.updateEmail(newEmail);
    }
}
