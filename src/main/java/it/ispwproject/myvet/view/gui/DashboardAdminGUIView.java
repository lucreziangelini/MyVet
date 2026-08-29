package it.ispwproject.myvet.view.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DashboardAdminGUIView
        extends DashboardGUIView {

    public final Button reportBtn =
            new Button("Statistiche e report");

    public DashboardAdminGUIView() {
        reportBtn.getStyleClass().add("button");
        reportBtn.setPrefWidth(220);
        reportBtn.setPrefHeight(42);
    }

    public BorderPane buildRoot(
            String nomeUtente,
            Runnable onLogout) {

        HBox navbar =
                buildNavbar("Admin", onLogout);

        Label welcomeLabel =
                new Label("Benvenuto, " + nomeUtente);

        welcomeLabel.getStyleClass().add(
                "page-title"
        );

        VBox body = new VBox(
                24,
                welcomeLabel,
                reportBtn
        );

        body.getStyleClass().add(
                "myvet-background"
        );

        body.setAlignment(Pos.CENTER);
        body.setPadding(new Insets(40));

        BorderPane root = new BorderPane();

        root.getStyleClass().add(
                "myvet-background"
        );

        root.setTop(navbar);
        root.setCenter(body);

        return root;
    }
}