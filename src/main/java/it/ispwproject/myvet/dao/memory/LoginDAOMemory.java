package it.ispwproject.myvet.dao.memory;

import it.ispwproject.myvet.dao.LoginDAO;
import it.ispwproject.myvet.demo.DemoDataStore;
import it.ispwproject.myvet.exception.LoginException;
import it.ispwproject.myvet.model.Credentials;
import it.ispwproject.myvet.model.User;

public class LoginDAOMemory implements LoginDAO {

    @Override
    public Credentials execute(
            String email,
            String hashedPassword) throws LoginException {

        User user = DemoDataStore.getInstance()
                .getUsers()
                .stream()
                .filter(current ->
                        current.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new LoginException(
                        "Credenziali non valide. Riprova."));

        if (hashedPassword == null || hashedPassword.isBlank()) {
            throw new LoginException(
                    "Credenziali non valide. Riprova.");
        }

        return new Credentials(
                email,
                hashedPassword,
                user.getRole()
        );
    }
}