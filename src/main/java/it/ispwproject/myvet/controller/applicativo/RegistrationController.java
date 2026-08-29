package it.ispwproject.myvet.controller.applicativo;

import it.ispwproject.myvet.bean.RegistrationBean;
import it.ispwproject.myvet.dao.DAOFactory;
import it.ispwproject.myvet.dao.RegistrationDAO;
import it.ispwproject.myvet.enumerator.Role;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.exception.LoginException;
import it.ispwproject.myvet.exception.RegistrationException;
import it.ispwproject.myvet.model.PetOwner;
import it.ispwproject.myvet.model.User;
import it.ispwproject.myvet.model.Veterinarian;
import it.ispwproject.myvet.util.PasswordUtils;
import it.ispwproject.myvet.util.ValidationUtils;

public class RegistrationController {

    private final RegistrationDAO registrationDAO;

    public RegistrationController() {
        this.registrationDAO =
                DAOFactory.getRegistrationDAO();
    }

    public void register(RegistrationBean bean)
            throws DAOException, RegistrationException {

        validateBean(bean);

        if (registrationDAO.emailExists(bean.getEmail())) {
            throw new RegistrationException(
                    "Email già registrata. Usa un'altra email."
            );
        }

        String hashedPassword;

        try {
            hashedPassword =
                    PasswordUtils.hash(bean.getPassword());
        } catch (LoginException e) {
            throw new RegistrationException(
                    "Errore durante la codifica della password.",
                    e
            );
        }

        User user;

        if (bean.getRole() == Role.VETERINARIAN) {
            user = new Veterinarian(
                    0,
                    bean.getName(),
                    bean.getSurname(),
                    bean.getEmail(),
                    hashedPassword,
                    bean.getBio(),
                    bean.getSpecialization()
            );
        } else {
            user = new PetOwner(
                    0,
                    bean.getName(),
                    bean.getSurname(),
                    bean.getEmail(),
                    hashedPassword
            );
        }

        registrationDAO.save(user);
    }

    private void validateBean(RegistrationBean bean)
            throws RegistrationException {

        if (bean == null) {
            throw new RegistrationException(
                    "Dati di registrazione non validi."
            );
        }

        validateRequiredField(
                bean.getName(),
                "Il nome è obbligatorio."
        );

        validateRequiredField(
                bean.getSurname(),
                "Il cognome è obbligatorio."
        );

        validateRequiredField(
                bean.getEmail(),
                "L'email è obbligatoria."
        );

        validateEmail(bean.getEmail());
        validatePassword(bean);
        validateRole(bean);
        validateVeterinarianFields(bean);
    }

    private void validateRequiredField(
            String value,
            String message)
            throws RegistrationException {

        if (value == null || value.isBlank()) {
            throw new RegistrationException(message);
        }
    }

    private void validateEmail(String email)
            throws RegistrationException {

        if (!ValidationUtils.isValidEmail(email)) {
            throw new RegistrationException(
                    "Email non valida."
            );
        }
    }

    private void validatePassword(RegistrationBean bean)
            throws RegistrationException {

        if (bean.getPassword() == null
                || bean.getPassword().length() < 8) {

            throw new RegistrationException(
                    "La password deve essere di almeno 8 caratteri."
            );
        }

        if (bean.getPassword()
                .chars()
                .noneMatch(Character::isUpperCase)) {

            throw new RegistrationException(
                    "La password deve contenere almeno "
                            + "una lettera maiuscola."
            );
        }

        if (bean.getPassword()
                .chars()
                .noneMatch(Character::isDigit)) {

            throw new RegistrationException(
                    "La password deve contenere almeno un numero."
            );
        }

        if (!bean.getPassword().equals(
                bean.getConfirmPassword())) {

            throw new RegistrationException(
                    "Le password non coincidono."
            );
        }
    }

    private void validateRole(RegistrationBean bean)
            throws RegistrationException {

        if (bean.getRole() != Role.PET_OWNER
                && bean.getRole() != Role.VETERINARIAN) {

            throw new RegistrationException(
                    "Seleziona un ruolo valido."
            );
        }
    }

    private void validateVeterinarianFields(
            RegistrationBean bean)
            throws RegistrationException {

        if (bean.getRole() != Role.VETERINARIAN) {
            return;
        }

        validateRequiredField(
                bean.getBio(),
                "La bio è obbligatoria per i veterinari."
        );

        validateRequiredField(
                bean.getSpecialization(),
                "La specializzazione è obbligatoria "
                        + "per i veterinari."
        );
    }
}