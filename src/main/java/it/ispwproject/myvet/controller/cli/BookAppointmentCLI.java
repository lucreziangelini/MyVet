package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;

import it.ispwproject.myvet.bean.*;
import it.ispwproject.myvet.controller.applicativo.BookingController;
import it.ispwproject.myvet.exception.BookingException;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.PetOwner;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.view.cli.BookAppointmentCLIView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookAppointmentCLI extends AbstractCLIState {

    private final BookingController bookingController = new BookingController();
    private final BookAppointmentCLIView view = new BookAppointmentCLIView();

    @Override
    public void entry(CLIStateMachine context) { view.mostraIntestazione();}

    @Override
    public void action(CLIStateMachine context) {

        PetOwner loggedOwner =
                (PetOwner) SessionManager
                        .getInstance()
                        .getLoggedUser();

        PetOwnerBean ownerBean = new PetOwnerBean(
                loggedOwner.getId(),
                loggedOwner.getName(),
                loggedOwner.getSurname(),
                loggedOwner.getEmail()
        );

        TimeSlotBean slot = null;

        try {
            // Step 1 - animale
            List<PetBean> pets =
                    bookingController.getRegisteredPets();

            if (pets.isEmpty()) {
                view.mostraMessaggio(
                        "Nessun animale registrato."
                );
                goBack(context); return;
            }

            view.mostraAnimali(pets);

            int pc = view.chiediScelta(
                    "Seleziona un animale",
                    0,
                    pets.size()
            );

            if (pc == 0) {
                goBack(context);
                return;
            }

            PetBean pet = pets.get(pc - 1);

            // Step 2 - data
            LocalDate date =
                    view.chiediDataPreferita();

            if (date == null) {
                goBack(context);
                return;
            }

            // Step 3 - veterinario
            List<VeterinarianBean> allVeterinarians =
                    bookingController
                            .getAvailableVeterinarians(
                                    date
                            );

            if (allVeterinarians.isEmpty()) {
                view.mostraMessaggio(
                        "Nessun veterinario disponibile "
                                + "nella data selezionata."
                );
                goBack(context);
                return;
            }

            List<VeterinarianBean> favourites =
                    allVeterinarians.stream()
                            .filter(
                                    VeterinarianBean::isFavourite
                            )
                            .toList();

            List<VeterinarianBean> others =
                    allVeterinarians.stream()
                            .filter(veterinarian ->
                                    !veterinarian.isFavourite())
                            .toList();

            view.mostraVeterinari(
                    favourites,
                    others
            );

            int vc = view.chiediScelta(
                    "Seleziona un veterinario",
                    0,
                    allVeterinarians.size()
            );

            if (vc == 0) {
                goBack(context);
                return;
            }

            List<VeterinarianBean> ordered =
                    new ArrayList<>(favourites);

            ordered.addAll(others);

            VeterinarianBean veterinarian =
                    ordered.get(vc - 1);

            // Step 4 - slot
            List<TimeSlotBean> available =
                    bookingController
                            .getVeterinarianAvailability(
                                    veterinarian,
                                    date
                            )
                            .stream()
                            .filter(
                                    TimeSlotBean::isAvailable
                            )
                            .toList();

            if (available.isEmpty()) {
                view.mostraMessaggio(
                        "Nessuno slot disponibile per "
                                + "questo veterinario."
                );
                goBack(context);
                return;
            }

            view.mostraSlot(available);

            int sc = view.chiediScelta(
                    "Seleziona uno slot",
                    0,
                    available.size()
            );

            if (sc == 0) {
                goBack(context);
                return;
            }

            slot = available.get(sc - 1);

            // Step 5 - riepilogo
            BookingRequestBean request =
                    new BookingRequestBean(
                            ownerBean,
                            pet,
                            veterinarian,
                            slot
                    );

            BookingResponseBean summary =
                    bookingController
                            .prepareBookingSummary(
                                    request
                            );

            view.mostraRiepilogo(summary);

            if (!view.chiediConferma(
                    "Confermare? "
                            + "(hai 5 minuti per decidere)")) {

                bookingController.releaseSlot(
                        slot.getId()
                );

                view.mostraMessaggio(
                        "Prenotazione annullata."
                );

                goBack(context);
                return;
            }

            // Step 6 - creazione
            BookingResponseBean response =
                    bookingController.createBooking(
                            request
                    );

            view.mostraConferma(response);

            if (!veterinarian.isFavourite()
                    && view.chiediConferma(
                    "Vuoi aggiungere "
                            + veterinarian.getFullName()
                            + " ai veterinari preferiti?"
            )) {

                bookingController
                        .addVeterinarianToFavourites(
                                veterinarian.getId()
                        );

                view.mostraMessaggio(
                        "⭐ Veterinario aggiunto ai preferiti."
                );
            }

        } catch (BookingException e) {

            if (slot != null) {
                try {
                    bookingController.releaseSlot(
                            slot.getId()
                    );
                } catch (DAOException ignored) {
                    // Ignora
                }
            }

            view.mostraMessaggio(
                    "❌ Errore: " + e.getMessage()
            );

        } catch (DAOException e) {

            view.mostraMessaggio(
                    "❌ Errore: " + e.getMessage()
            );
        }

        goBack(context);
    }
}
