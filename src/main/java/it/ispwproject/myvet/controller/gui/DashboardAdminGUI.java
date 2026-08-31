package it.ispwproject.myvet.controller.gui;

import it.ispwproject.myvet.dao.ConnectionFactory;
import it.ispwproject.myvet.pattern.singleton.SessionManager;
import it.ispwproject.myvet.view.gui.DashboardAdminGUIView;

import javafx.stage.Stage;

public class DashboardAdminGUI {

    private final Stage stage;
    private final DashboardAdminGUIView view = new DashboardAdminGUIView();

    public DashboardAdminGUI(Stage stage) { this.stage = stage;}

    public void show() {
        var user = SessionManager.getInstance()
                .getLoggedUser();

        view.reportBtn.setOnAction(
                event -> new StatisticsGUI(stage).show());

        stage.setScene(GUIUtils.createScene(
                view.buildRoot(user, this::handleLogout)
        ));
        stage.show();
    }

    private void handleLogout() {
        try {
            ConnectionFactory.clearRole();
        } catch (java.sql.SQLException ex) {
            // ignora
        }

        SessionManager.getInstance().clearSession();
        MainGUI.showLogin();
    }
}
