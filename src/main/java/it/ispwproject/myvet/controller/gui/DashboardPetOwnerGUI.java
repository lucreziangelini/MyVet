package it.ispwproject.myvet.controller.gui;

import it.ispwproject.myvet.bean.BookingResponseBean;
import it.ispwproject.myvet.controller.applicativo.BookingController;
import it.ispwproject.myvet.controller.applicativo.UserController;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.User;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.view.gui.DashboardPetOwnerGUIView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class DashboardPetOwnerGUI {

    private final Stage stage;
    private final BookingController bookingController =
            new BookingController();
    private final UserController userController =
            new UserController();
    private final DashboardPetOwnerGUIView view =
            new DashboardPetOwnerGUIView();

    private int weekOffset = 0;
    private final int[] weekOffRef = {0};

    public DashboardPetOwnerGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        User user = SessionManager.getInstance().getLoggedUser();

        HBox navbar = view.buildNavbar(
                "Pet Owner",
                this::handleLogout
        );

        VBox calendarSection = view.buildCalendarSection(
                () -> {
                    weekOffset--;
                    weekOffRef[0] = weekOffset;
                    view.refreshCalendar(
                            loadBookings(),
                            weekOffset
                    );
                },
                () -> {
                    weekOffset++;
                    weekOffRef[0] = weekOffset;
                    view.refreshCalendar(
                            loadBookings(),
                            weekOffset
                    );
                },
                () -> {
                    weekOffset = 0;
                    weekOffRef[0] = 0;
                    view.refreshCalendar(
                            loadBookings(),
                            weekOffset
                    );
                }
        );

        List<BookingResponseBean> bookings = loadBookings();

        view.bindCalendarWidth(bookings, weekOffRef);
        view.refreshCalendar(bookings, weekOffset);

        VBox actionButtons = view.buildActionButtons(
                event -> new BookAppointmentGUI(stage).show(),
                event -> new ViewAppointmentGUI(stage).show(),
                event -> new ViewActivitiesGUI(stage).show(),
                event -> new MedicalDocumentsGUI(stage).show()
        );

        VBox accordion = view.buildUserInfoAccordion(
                user,
                this::handleSaveEmail
        );

        VBox rightSection = view.buildRightSection(
                actionButtons,
                accordion
        );

        HBox body = new HBox(20);
        body.getStyleClass().add("myvet-background");
        body.setPadding(new Insets(20, 24, 20, 24));
        body.setAlignment(Pos.CENTER);

        HBox.setHgrow(calendarSection, Priority.ALWAYS);

        body.getChildren().addAll(
                calendarSection,
                rightSection
        );

        BorderPane root = new BorderPane();
        root.getStyleClass().add("myvet-background");
        root.setTop(navbar);
        root.setCenter(body);

        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }

    private List<BookingResponseBean> loadBookings() {
        try {
            int petOwnerId = SessionManager
                    .getInstance()
                    .getLoggedUser()
                    .getId();

            return bookingController.getPetOwnerBookings(
                    petOwnerId
            );
        } catch (DAOException e) {
            return List.of();
        }
    }

    private void handleLogout() {
        try {
            it.ispwproject.myvet.dao.ConnectionFactory.clearRole();
        } catch (java.sql.SQLException ex) {
            // ignora
        }

        SessionManager.getInstance().clearSession();
        MainGUI.showLogin();
    }

    private void handleSaveEmail(String newEmail) {
        try {
            userController.updateEmail(newEmail);
        } catch (DAOException ex) {
            // L'errore viene mostrato dalla view.
        }
    }
}
