package it.ispwproject.myvet.controller.gui;

import it.ispwproject.myvet.bean.TimeSlotBean;
import it.ispwproject.myvet.controller.applicativo.AvailabilityController;
import it.ispwproject.myvet.controller.applicativo.UserController;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.model.User;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.view.gui.DashboardVeterinarianGUIView;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class DashboardVeterinarianGUI {

    private final Stage stage;

    private final AvailabilityController availabilityController =
            new AvailabilityController();

    private final UserController userController =
            new UserController();

    private final DashboardVeterinarianGUIView view =
            new DashboardVeterinarianGUIView();

    private int weekOffset = 0;
    private final int[] weekOffRef = {0};

    public DashboardVeterinarianGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        User user = SessionManager.getInstance().getLoggedUser();

        HBox navbar = view.buildNavbar(
                "Veterinario",
                this::handleLogout
        );

        VBox calendarSection = view.buildCalendarSection(
                () -> {
                    weekOffset--;
                    weekOffRef[0] = weekOffset;
                    view.refreshCalendar(
                            loadSlots(),
                            weekOffset
                    );
                },
                () -> {
                    weekOffset++;
                    weekOffRef[0] = weekOffset;
                    view.refreshCalendar(
                            loadSlots(),
                            weekOffset
                    );
                },
                () -> {
                    weekOffset = 0;
                    weekOffRef[0] = 0;
                    view.refreshCalendar(
                            loadSlots(),
                            weekOffset
                    );
                }
        );

        List<TimeSlotBean> slots = loadSlots();

        view.bindCalendarWidth(slots, weekOffRef);
        view.refreshCalendar(slots, weekOffset);

        VBox actionGrid = view.buildActionGrid(
                event -> new SetAvailabilityGUI(stage).show(),
                event -> new ViewSlotsGUI(stage).show(),
                event -> new ManagePetCareGUI(stage).show(),
                event -> new MedicalDocumentsGUI(stage).show()
        );

        VBox accordion = view.buildUserInfoAccordion(
                user,
                this::handleSaveEmail
        );

        VBox rightSection = view.buildRightSection(
                actionGrid,
                accordion
        );

        HBox body = new HBox(20);
        body.getStyleClass().add("myvet-background");
        body.setPadding(new Insets(20, 24, 20, 24));
        body.setAlignment(Pos.TOP_CENTER);

        HBox.setHgrow(calendarSection, Priority.ALWAYS);
        calendarSection.setMaxWidth(Double.MAX_VALUE);
        rightSection.setMaxWidth(380);

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

    private List<TimeSlotBean> loadSlots() {
        try {
            return availabilityController.getSlots();
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

    private boolean handleSaveEmail(String newEmail) {
        try {
            userController.updateEmail(newEmail);
            return true;
        } catch (DAOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Email non aggiornata");
            alert.setHeaderText(null);
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            return false;
        }
    }
}
