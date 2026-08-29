package it.ispwproject.myvet.controller.gui;

import it.ispwproject.myvet.bean.StatisticsBean;
import it.ispwproject.myvet.controller.applicativo.StatisticsController;
import it.ispwproject.myvet.exception.DAOException;
import it.ispwproject.myvet.view.gui.StatisticsGUIView;

import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StatisticsGUI {

    private final Stage stage;

    private final StatisticsController controller =
            new StatisticsController();

    private final StatisticsGUIView view =
            new StatisticsGUIView();

    public StatisticsGUI(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        BorderPane root = view.buildRoot(
                MainGUI::showDashboardAdmin
        );

        try {
            StatisticsBean statistics =
                    controller.getStatistics();

            root.setCenter(
                    view.buildContent(statistics)
            );

        } catch (DAOException e) {
            root.setCenter(
                    view.buildErrorContent(e.getMessage())
            );
        }

        stage.setScene(
                GUIUtils.createScene(root)
        );

        stage.show();
    }
}
