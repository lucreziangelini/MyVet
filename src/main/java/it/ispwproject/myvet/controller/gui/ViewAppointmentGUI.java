package it.ispwproject.myvet.controller.gui;

import it.ispwproject.myvet.bean.BookingResponseBean;
import it.ispwproject.myvet.controller.applicativo.BookingController;
import it.ispwproject.myvet.enumerator.BookingStatus;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.view.gui.ViewAppointmentGUIView;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ViewAppointmentGUI {

    private final Stage stage;

    private final BookingController bookingController =
            new BookingController();

    private final ViewAppointmentGUIView view =
            new ViewAppointmentGUIView();

    public ViewAppointmentGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = view.buildRoot(
                MainGUI::showDashboardPetOwner
        );

        view.clearError();

        try {
            int petOwnerId = SessionManager
                    .getInstance()
                    .getLoggedUser()
                    .getId();

            List<BookingResponseBean> bookings =
                    bookingController.getPetOwnerBookings(
                            petOwnerId
                    );

            LocalDate today =
                    LocalDate.now(ZoneId.systemDefault());

            LocalTime now =
                    LocalTime.now(ZoneId.systemDefault());

            List<BookingResponseBean> confirmed = bookings.stream()
                    .filter(booking ->
                            booking.getStatus()
                                    == BookingStatus.CONFIRMED
                    )
                    .filter(booking ->
                            booking.getTimeSlot()
                                    .getDate()
                                    .isAfter(today)
                                    || (booking.getTimeSlot()
                                    .getDate()
                                    .isEqual(today)
                                    && booking.getTimeSlot()
                                    .getEndTime()
                                    .isAfter(now))
                    )
                    .sorted((first, second) ->
                            first.getTimeSlot()
                                    .getDate()
                                    .compareTo(
                                            second.getTimeSlot()
                                                    .getDate()
                                    )
                    )
                    .toList();

            List<BookingResponseBean> cancelled = bookings.stream()
                    .filter(booking ->
                            booking.getStatus()
                                    == BookingStatus.CANCELLED
                    )
                    .sorted((first, second) ->
                            first.getTimeSlot()
                                    .getDate()
                                    .compareTo(
                                            second.getTimeSlot()
                                                    .getDate()
                                    )
                    )
                    .toList();

            List<BookingResponseBean> past = bookings.stream()
                    .filter(booking ->
                            booking.getStatus()
                                    == BookingStatus.CONFIRMED
                    )
                    .filter(booking ->
                            booking.getTimeSlot()
                                    .getDate()
                                    .isBefore(today)
                                    || (booking.getTimeSlot()
                                    .getDate()
                                    .isEqual(today)
                                    && !booking.getTimeSlot()
                                    .getEndTime()
                                    .isAfter(now))
                    )
                    .sorted((first, second) ->
                            first.getTimeSlot()
                                    .getDate()
                                    .compareTo(
                                            second.getTimeSlot()
                                                    .getDate()
                                    )
                    )
                    .toList();

            view.buildContent(
                    root,
                    confirmed,
                    cancelled,
                    past,
                    this::confirmCancel
            );

        } catch (DAOException e) {
            view.setError(
                    "Errore: " + e.getMessage()
            );

            root.setCenter(view.errorLabel);
        }

        stage.setScene(
                GUIUtils.createScene(root)
        );

        stage.show();
    }

    private void confirmCancel(BookingResponseBean booking) {
        Alert alert =
                new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Conferma annullamento");
        alert.setHeaderText(null);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy");

        alert.setContentText(
                "Vuoi annullare l'appuntamento?\n\n"
                        + booking.getPet().getName()
                        + " — "
                        + booking.getVeterinarian().getFullName()
                        + "\n"
                        + booking.getTimeSlot()
                        .getDate()
                        .format(formatter)
                        + "  "
                        + booking.getTimeSlot().getStartTime()
                        + " – "
                        + booking.getTimeSlot().getEndTime()
        );

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    bookingController.cancelBooking(
                            booking.getId(),
                            SessionManager
                                    .getInstance()
                                    .getLoggedUser()
                                    .getId()
                    );

                    show();

                } catch (DAOException e) {
                    view.setError(
                            "Errore: " + e.getMessage()
                    );
                }
            }
        });
    }
}
