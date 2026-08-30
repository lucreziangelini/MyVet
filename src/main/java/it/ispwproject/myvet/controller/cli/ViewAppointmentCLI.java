package it.ispwproject.myvet.controller.cli;

import it.ispwproject.myvet.bean.BookingResponseBean;
import it.ispwproject.myvet.controller.applicativo.BookingController;
import it.ispwproject.myvet.enumerator.BookingStatus;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.pattern.state.AbstractCLIState;
import it.ispwproject.myvet.pattern.state.CLIStateMachine;
import it.ispwproject.myvet.view.cli.ViewAppointmentCLIView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class ViewAppointmentCLI extends AbstractCLIState {

    private final BookingController bookingController =
            new BookingController();

    private final ViewAppointmentCLIView view =
            new ViewAppointmentCLIView();

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

            List<BookingResponseBean> all =
                    bookingController.getPetOwnerBookings(
                            petOwnerId
                    );

            LocalDate today =
                    LocalDate.now(ZoneId.systemDefault());

            LocalTime now =
                    LocalTime.now(ZoneId.systemDefault());

            List<BookingResponseBean> confirmed = all.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                    .filter(b ->
                            b.getTimeSlot().getDate().isAfter(today)
                                    || (b.getTimeSlot().getDate().isEqual(today)
                                    && b.getTimeSlot().getEndTime().isAfter(now))
                    )
                    .sorted((a, b) -> a.getTimeSlot()
                            .getDate()
                            .compareTo(b.getTimeSlot().getDate()))
                    .toList();

            List<BookingResponseBean> cancelled = all.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                    .sorted((a, b) -> a.getTimeSlot()
                            .getDate()
                            .compareTo(b.getTimeSlot().getDate()))
                    .toList();

            List<BookingResponseBean> past = all.stream()
                    .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                    .filter(b ->
                            b.getTimeSlot().getDate().isBefore(today)
                                    || (b.getTimeSlot().getDate().isEqual(today)
                                    && !b.getTimeSlot().getEndTime().isAfter(now))
                    )
                    .sorted((a, b) -> a.getTimeSlot()
                            .getDate()
                            .compareTo(b.getTimeSlot().getDate()))
                    .toList();

            boolean running = true;

            while (running) {
                view.mostraTab(
                        confirmed.size(),
                        cancelled.size(),
                        past.size()
                );

                int scelta =
                        view.chiediScelta("Scelta", 0, 3);

                switch (scelta) {
                    case 1 -> view.mostraConfermati(confirmed);
                    case 2 -> view.mostraCancellati(cancelled);
                    case 3 -> view.mostraPassati(past);
                    case 0 -> running = false;
                    default ->
                            view.mostraErrore("Scelta non valida.");
                }
            }

        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }

        goBack(context);
    }
}
