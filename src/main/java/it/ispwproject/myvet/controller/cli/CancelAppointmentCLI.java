package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.bean.BookingResponseBean;
import it.ispwproject.myvet.controller.applicativo.BookingController;
import it.ispwproject.myvet.enumerator.BookingStatus;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;
import it.ispwproject.myvet.view.cli.CancelAppointmentCLIView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class CancelAppointmentCLI
        extends AbstractCLIState {

    private final BookingController bookingController =
            new BookingController();

    private final CancelAppointmentCLIView view =
            new CancelAppointmentCLIView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {

        try {
            int petOwnerId = SessionManager
                    .getInstance()
                    .getLoggedUser()
                    .getId();

            List<BookingResponseBean> cancellable =
                    bookingController
                            .getPetOwnerBookings(petOwnerId)
                            .stream()
                            .filter(booking ->
                                    booking.getStatus()
                                            == BookingStatus.CONFIRMED)
                            .filter(booking ->
                                    booking.getTimeSlot()
                                            .getDate()
                                            .isAfter(
                                                    LocalDate.now(
                                                            ZoneId.systemDefault()
                                                    )
                                            )
                                            || (
                                            booking.getTimeSlot()
                                                    .getDate()
                                                    .isEqual(
                                                            LocalDate.now(
                                                                    ZoneId.systemDefault()
                                                            )
                                                    )
                                                    && booking.getTimeSlot()
                                                    .getStartTime()
                                                    .isAfter(
                                                            LocalTime.now(
                                                                    ZoneId.systemDefault()
                                                            )
                                                    )
                                    ))
                            .toList();

            if (cancellable.isEmpty()) {
                view.mostraMessaggio(
                        "Nessun appuntamento attivo "
                                + "da annullare."
                );
                goBack(context);
                return;
            }

            view.mostraPrenotazioniAnnullabili(
                    cancellable
            );

            int choice = view.chiediScelta(
                    "Seleziona l'appuntamento da annullare",
                    0,
                    cancellable.size()
            );

            if (choice == 0) {
                goBack(context);
                return;
            }

            BookingResponseBean selected =
                    cancellable.get(choice - 1);

            view.mostraRiepilogo(selected);

            if (!view.chiediConferma(
                    "Sei sicuro di voler annullare?")) {

                view.mostraMessaggio(
                        "Operazione annullata."
                );

                goBack(context);
                return;
            }

            bookingController.cancelBooking(
                    selected.getId(),
                    petOwnerId
            );

            view.mostraSuccesso();

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        goBack(context);
    }
}
