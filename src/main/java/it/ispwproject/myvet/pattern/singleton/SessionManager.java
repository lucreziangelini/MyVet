package it.ispwproject.myvet.pattern.singleton;

import it.ispwproject.myvet.bean.SessionBean;
import it.ispwproject.myvet.enumerator.Role;
import it.ispwproject.myvet.model.User;

public class SessionManager {

    private User loggedUser;
    private SessionBean session;

    private SessionManager() {
    }

    private static class Holder {
        private static final SessionManager INSTANCE =
                new SessionManager();
    }

    public static SessionManager getInstance() {
        return Holder.INSTANCE;
    }

    public void setLoggedUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "Logged user cannot be null"
            );
        }

        if (loggedUser != null || session != null) {
            throw new IllegalStateException(
                    "A session is already active"
            );
        }

        loggedUser = user;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    public void setSessionBean(SessionBean sessionBean) {
        if (sessionBean == null) {
            throw new IllegalArgumentException(
                    "Session cannot be null"
            );
        }

        if (session != null) {
            throw new IllegalStateException(
                    "A session is already active"
            );
        }

        if (loggedUser == null) {
            throw new IllegalStateException(
                    "Logged user is not set"
            );
        }

        session = sessionBean;
    }

    public SessionBean getSessionBean() {
        return session;
    }

    public boolean isLoggedIn() {
        return loggedUser != null
                && session != null;
    }

    public boolean isPetOwner() {
        return isLoggedIn()
                && loggedUser.hasRole(Role.PET_OWNER);
    }

    public boolean isVeterinarian() {
        return isLoggedIn()
                && loggedUser.hasRole(Role.VETERINARIAN);
    }

    public boolean isAdmin() {
        return isLoggedIn()
                && loggedUser.hasRole(Role.ADMIN);
    }

    public void updateEmail(String newEmail) {
        if (!isLoggedIn()) {
            throw new IllegalStateException(
                    "No active session"
            );
        }

        loggedUser.setEmail(newEmail);
        session = new SessionBean(
                newEmail,
                session.getRole()
        );
    }

    public void clearSession() {
        loggedUser = null;
        session = null;
    }
}
