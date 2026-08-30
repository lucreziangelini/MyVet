package it.ispwproject.myvet.controller.applicativo;

import it.ispwproject.myvet.bean.*;
import it.ispwproject.myvet.dao.DAOFactory;
import it.ispwproject.myvet.demo.DemoDataStore;
import it.ispwproject.myvet.enumerator.Role;
import it.ispwproject.myvet.exception.BookingException;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.PetOwner;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ------------------------------------------------------------
 * Test Class : BookingControllerTest
 * Author     : Lucrezia Angelini
 * Description: Verifica il meccanismo di prenotazione temporanea
 *              degli slot.
 *              Tentativo di prenotare uno slot già riservato
 *              da un altro Pet Owner prima che la finestra di
 *              5 minuti sia scaduta.
 * ------------------------------------------------------------
 */
class BookingControllerTest {

    @BeforeEach
    void setup() {
        SessionManager.getInstance().clearSession();
        DemoDataStore.reset();
        DAOFactory.setPersistence(DAOFactory.MEMORY);
    }

    // Test: prenotazione su slot già riservato

    @Test
    void testPrenotazioneSuSlotGiaRiservato()
            throws DAOException, BookingException {

        VeterinarianBean veterinarian = new VeterinarianBean(
                3, "Luca", "Verdi", null,
                "luca.vet@demo.it", null, true
        );

        TimeSlotBean slot = new TimeSlotBean(
                2, null, null, null, true
        ); // slot 2: libero nel DemoDataStore

        // Pet Owner 1 riserva lo slot

        PetOwner owner1 = new PetOwner(
                1, "Anna", "Rossi",
                "anna@demo.it", null
        );

        SessionManager.getInstance().setLoggedUser(owner1);
        SessionManager.getInstance().setSessionBean(
                new SessionBean(
                        owner1.getEmail(),
                        Role.PET_OWNER
                )
        );

        BookingController controller1 =
                new BookingController();

        PetOwnerBean ownerBean1 = new PetOwnerBean(
                1, "Anna", "Rossi", "anna@demo.it"
        );

        PetBean pet1 = new PetBean(
                1, "Milo", "Dog", "Labrador", null
        );

        controller1.prepareBookingSummary(
                new BookingRequestBean(
                        ownerBean1,
                        pet1,
                        veterinarian,
                        slot
                )
        );

        // Pet Owner 2 tenta lo stesso slot

        SessionManager.getInstance().clearSession();

        PetOwner owner2 = new PetOwner(
                2, "Marco", "Bianchi",
                "marco@demo.it", null
        );

        SessionManager.getInstance().setLoggedUser(owner2);
        SessionManager.getInstance().setSessionBean(
                new SessionBean(
                        owner2.getEmail(),
                        Role.PET_OWNER
                )
        );

        BookingController controller2 =
                new BookingController();

        PetOwnerBean ownerBean2 = new PetOwnerBean(
                2, "Marco", "Bianchi", "marco@demo.it"
        );

        PetBean pet2 = new PetBean(
                2, "Luna", "Cat",
                "European Shorthair", null
        );

        assertThrows(
                BookingException.class,
                () -> controller2.prepareBookingSummary(
                        new BookingRequestBean(
                                ownerBean2,
                                pet2,
                                veterinarian,
                                slot
                        )
                )
        );
    }
}