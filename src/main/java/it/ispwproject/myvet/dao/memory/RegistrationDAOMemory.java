package it.ispwproject.myvet.dao.memory;

import it.ispwproject.myvet.dao.RegistrationDAO;
import it.ispwproject.myvet.demo.DemoDataStore;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.User;

public class RegistrationDAOMemory
        implements RegistrationDAO {

    private final DemoDataStore store =
            DemoDataStore.getInstance();

    @Override
    public boolean emailExists(String email)
            throws DAOException {

        return store.getUsers().stream()
                .anyMatch(user ->
                        user.getEmail()
                                .equalsIgnoreCase(email));
    }

    @Override
    public void save(User user) throws DAOException {
        user.setId(store.nextUserId());
        store.getUsers().add(user);
    }
}