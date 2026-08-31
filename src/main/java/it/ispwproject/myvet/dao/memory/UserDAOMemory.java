package it.ispwproject.myvet.dao.memory;

import it.ispwproject.myvet.dao.UserDAO;
import it.ispwproject.myvet.demo.DemoDataStore;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.User;

import java.util.List;

public class UserDAOMemory implements UserDAO {

    private final DemoDataStore store =
            DemoDataStore.getInstance();

    @Override
    public User findByEmail(String email)
            throws DAOException {

        return store.getUsers().stream()
                .filter(user ->
                        user.getEmail()
                                .equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new DAOException(
                        "Utente non trovato: " + email));
    }

    @Override
    public void updateEmail(
            int id,
            String newEmail) throws DAOException {

        boolean alreadyUsed = store.getUsers().stream()
                .anyMatch(user -> user.getId() != id
                        && user.getEmail().equalsIgnoreCase(newEmail));

        if (alreadyUsed) {
            throw new DAOException(
                    "Email già utilizzata da un altro account."
            );
        }

        store.getUsers().stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElseThrow(() -> new DAOException(
                        "Utente non trovato (ID: "
                                + id + ")"))
                .setEmail(newEmail);
    }

    @Override
    public List<User> getAll()
            throws DAOException {

        return List.copyOf(store.getUsers());
    }
}
