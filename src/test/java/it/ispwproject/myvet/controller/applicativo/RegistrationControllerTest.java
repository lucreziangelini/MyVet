package it.ispwproject.myvet.controller.applicativo;

import it.ispwproject.myvet.bean.RegistrationBean;
import it.ispwproject.myvet.dao.DAOFactory;
import it.ispwproject.myvet.demo.DemoDataStore;
import it.ispwproject.myvet.enumerator.Gender;
import it.ispwproject.myvet.enumerator.Role;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.exception.RegistrationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ------------------------------------------------------------
 * Test Class : RegistrationControllerTest
 * Author     : Lucrezia Angelini
 * Description: Verifica che il sistema impedisca la registrazione
 *              di due account con la stessa email. Dopo una prima
 *              registrazione avvenuta con successo, un secondo
 *              tentativo con la stessa email deve lanciare una
 *              RegistrationException.
 * ------------------------------------------------------------
 */
class RegistrationControllerTest {

    private RegistrationController registrationController;

    @BeforeEach
    void setup() {
        DemoDataStore.reset();
        DAOFactory.setPersistence(DAOFactory.MEMORY);

        registrationController =
                new RegistrationController();
    }

    @Test
    void testRegistrazioneConEmailDuplicata()
            throws DAOException, RegistrationException {

        // Prima registrazione: deve andare a buon fine

        RegistrationBean bean =
                new RegistrationBean();

        bean.setName("Mario");
        bean.setSurname("Rossi");
        bean.setEmail("mario@test.com");
        bean.setPassword("Password123");
        bean.setConfirmPassword("Password123");
        bean.setRole(Role.PET_OWNER);
        bean.setGender(Gender.MALE);

        registrationController.register(bean);

        var registeredUser = DemoDataStore.getInstance()
                .getUsers()
                .stream()
                .filter(user -> user.getEmail().equals("mario@test.com"))
                .findFirst()
                .orElseThrow();

        assertEquals(Gender.MALE, registeredUser.getGender());
        assertEquals("Bentornato", registeredUser.getWelcome());

        var femaleDemoUser = DemoDataStore.getInstance()
                .getUsers()
                .stream()
                .filter(user -> user.getEmail().equals("anna@demo.it"))
                .findFirst()
                .orElseThrow();

        assertEquals(Gender.FEMALE, femaleDemoUser.getGender());
        assertEquals("Bentornata", femaleDemoUser.getWelcome());

        /*
         * Seconda registrazione con la stessa email:
         * deve lanciare RegistrationException.
         */

        RegistrationBean duplicato =
                new RegistrationBean();

        duplicato.setName("Mario");
        duplicato.setSurname("Rossi");
        duplicato.setEmail("mario@test.com");
        duplicato.setPassword("Password123");
        duplicato.setConfirmPassword("Password123");
        duplicato.setRole(Role.PET_OWNER);
        duplicato.setGender(Gender.MALE);

        assertThrows(
                RegistrationException.class,
                () -> registrationController.register(
                        duplicato
                )
        );
    }
}
