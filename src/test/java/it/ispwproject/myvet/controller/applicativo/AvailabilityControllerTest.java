package it.ispwproject.myvet.controller.applicativo;

import it.ispwproject.myvet.bean.TimeSlotBean;
import it.ispwproject.myvet.dao.DAOFactory;
import it.ispwproject.myvet.demo.DemoDataStore;
import it.ispwproject.myvet.exception.AvailabilityException;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.exception.LoginException;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ------------------------------------------------------------
 * Test Class : AvailabilityControllerTest
 * Author     : Lucrezia Angelini
 * Description: Verifica che un veterinario non possa aggiungere
 *              due slot con orari sovrapposti. Il primo slot
 *              viene aggiunto con successo, mentre il secondo
 *              deve lanciare una AvailabilityException.
 * ------------------------------------------------------------
 */
class AvailabilityControllerTest {

    private AvailabilityController availabilityController;

    @BeforeEach
    void setup() throws LoginException {
        SessionManager.getInstance().clearSession();
        DemoDataStore.reset();

        DAOFactory.setPersistence(
                DAOFactory.MEMORY
        );

        // Simula il veterinario loggato
        new LoginController().login(
                "luca.vet@demo.it",
                "demo"
        );

        availabilityController =
                new AvailabilityController();
    }

    @Test
    void testSlotSovrapposto()
            throws DAOException, AvailabilityException {

        LocalDate futureDate =
                LocalDate.now().plusYears(1);

        // Primo slot: deve essere aggiunto correttamente
        TimeSlotBean firstSlot =
                new TimeSlotBean(
                        0,
                        futureDate,
                        LocalTime.of(9, 0),
                        LocalTime.of(11, 0),
                        true
                );

        availabilityController.addSlot(
                firstSlot
        );

        /*
         * Secondo slot sovrapposto:
         * deve lanciare AvailabilityException.
         */
        TimeSlotBean overlappingSlot =
                new TimeSlotBean(
                        0,
                        futureDate,
                        LocalTime.of(10, 0),
                        LocalTime.of(12, 0),
                        true
                );

        assertThrows(
                AvailabilityException.class,
                () -> availabilityController.addSlot(
                        overlappingSlot
                )
        );
    }
}