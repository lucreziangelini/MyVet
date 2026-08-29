package it.ispwproject.myvet.controller.gui;

import it.ispwproject.myvet.bean.ActivityBean;
import it.ispwproject.myvet.controller.applicativo.ActivityController;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.view.gui.ViewActivitiesGUIView;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;

public class ViewActivitiesGUI {

    private final Stage stage;

    private final ActivityController activityController =
            new ActivityController();

    private final ViewActivitiesGUIView view =
            new ViewActivitiesGUIView();

    public ViewActivitiesGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = view.buildRoot(
                MainGUI::showDashboardPetOwner
        );

        view.clearError();

        try {
            List<ActivityBean> activities =
                    activityController.getMyActivities();

            List<ActivityBean> pending = activities.stream()
                    .filter(activity -> !activity.isCompleted())
                    .toList();

            List<ActivityBean> completed = activities.stream()
                    .filter(ActivityBean::isCompleted)
                    .toList();

            view.buildContent(
                    root,
                    pending,
                    completed,
                    this::handleMarkDone
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

    private void handleMarkDone(ActivityBean activity) {
        try {
            activityController.markActivityCompleted(
                    activity.getId()
            );

            show();

        } catch (DAOException e) {
            view.setError(
                    "Errore: " + e.getMessage()
            );
        }
    }
}