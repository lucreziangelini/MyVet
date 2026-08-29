package it.ispwproject.myvet.view.gui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public abstract class PageGUIView {

    // ────────────────────────────────────────────────────────────────────────
    // Top bar comune a tutte le pagine secondarie di MyVet
    // ────────────────────────────────────────────────────────────────────────

    public HBox buildTopBar(
            String titleText,
            Runnable onBack) {

        HBox bar = new HBox();

        bar.getStyleClass().add(
                "navbar"
        );

        bar.setAlignment(Pos.CENTER_LEFT);

        // Pulsante per tornare alla schermata precedente
        Button backButton =
                new Button("⟪  Indietro");

        backButton.getStyleClass().add(
                "back-button"
        );

        backButton.setOnAction(
                event -> onBack.run()
        );

        HBox left = new HBox(backButton);

        left.setAlignment(Pos.CENTER_LEFT);
        left.setPrefWidth(150);

        HBox.setHgrow(
                left,
                Priority.ALWAYS
        );

        // Titolo della pagina corrente
        Label title =
                new Label(titleText);

        title.getStyleClass().add(
                "page-title"
        );

        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);

        HBox.setHgrow(
                title,
                Priority.ALWAYS
        );

        // Sezione destra contenente il logo MyVet
        HBox right = new HBox();

        right.setAlignment(Pos.CENTER_RIGHT);
        right.setPrefWidth(150);

        HBox.setHgrow(
                right,
                Priority.ALWAYS
        );

        var logoStream =
                getClass().getResourceAsStream(
                        "/images/logo.png"
                );

        if (logoStream != null) {
            ImageView logo = new ImageView(
                    new Image(
                            logoStream,
                            60,
                            60,
                            true,
                            true
                    )
            );

            logo.setFitHeight(56);
            logo.setPreserveRatio(true);
            logo.setSmooth(true);

            right.getChildren().add(logo);
        }

        bar.getChildren().addAll(
                left,
                title,
                right
        );

        return bar;
    }

    // ────────────────────────────────────────────────────────────────────────
    // ScrollPane trasparente comune
    // ────────────────────────────────────────────────────────────────────────

    public ScrollPane transparentScroll(
            Node content) {

        ScrollPane scrollPane =
                new ScrollPane(content);

        scrollPane.getStyleClass().add(
                "transparent-scroll"
        );

        scrollPane.setFitToWidth(true);

        return scrollPane;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Shell comune: BorderPane con top bar e sfondo MyVet
    // ────────────────────────────────────────────────────────────────────────

    public BorderPane buildShell(
            String titleText,
            Runnable onBack) {

        BorderPane shell =
                new BorderPane();

        shell.getStyleClass().add(
                "myvet-background"
        );

        shell.setTop(
                buildTopBar(
                        titleText,
                        onBack
                )
        );

        return shell;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Etichetta comune per i messaggi di errore
    // ────────────────────────────────────────────────────────────────────────

    public Label buildErrorLabel() {
        Label errorLabel =
                new Label("");

        errorLabel.getStyleClass().add(
                "error-label"
        );

        errorLabel.setWrapText(true);

        return errorLabel;
    }
}