package it.ispwproject.myvet.view.gui;

import it.ispwproject.myvet.bean.StatisticsBean;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Map;

public class StatisticsGUIView extends PageGUIView {

    // ────────────────────────────────────────────────────────────────────────
    // Costruzione della pagina delle statistiche
    // ────────────────────────────────────────────────────────────────────────

    public BorderPane buildRoot(Runnable onBack) {
        BorderPane root =
                new BorderPane();

        root.getStyleClass().add(
                "myvet-background"
        );

        root.setTop(
                buildTopBar(
                        "Statistiche e resoconti",
                        onBack
                )
        );

        return root;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Contenuto principale delle statistiche
    // ────────────────────────────────────────────────────────────────────────

    public VBox buildContent(
            StatisticsBean statistics) {

        VBox content = new VBox(24);

        content.getStyleClass().add(
                "myvet-background"
        );

        content.setAlignment(
                Pos.TOP_CENTER
        );

        content.setPadding(
                new Insets(30, 60, 30, 60)
        );

        // Card con i valori numerici principali
        HBox numberCards = new HBox(16);

        numberCards.setAlignment(Pos.CENTER);
        numberCards.setMaxWidth(760);

        numberCards.getChildren().addAll(
                buildNumberCard(
                        "Prenotazioni totali",
                        String.valueOf(
                                statistics.getTotalBookings()
                        ),
                        "green"
                ),
                buildNumberCard(
                        "Prenotazioni annullate",
                        String.valueOf(
                                statistics.getCancelledBookings()
                        ),
                        "red"
                ),
                buildNumberCard(
                        "Tasso di cancellazione",
                        String.format(
                                "%.1f%%",
                                statistics.getCancellationRate()
                        ),
                        "orange"
                )
        );

        // Classifica dei veterinari con più appuntamenti confermati
        VBox rankingCard = buildRankingCard(
                "🏆  Veterinari più prenotati",
                statistics.getTopVeterinarians(),
                "appuntamenti"
        );

        content.getChildren().addAll(
                numberCards,
                rankingCard
        );

        return content;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Contenuto mostrato in caso di errore
    // ────────────────────────────────────────────────────────────────────────

    public VBox buildErrorContent(
            String message) {

        VBox content = new VBox(24);

        content.getStyleClass().add(
                "myvet-background"
        );

        content.setAlignment(
                Pos.TOP_CENTER
        );

        content.setPadding(
                new Insets(30, 60, 30, 60)
        );

        Label errorLabel =
                new Label("Errore: " + message);

        errorLabel.getStyleClass().add(
                "error-label"
        );

        errorLabel.setWrapText(true);

        content.getChildren().add(
                errorLabel
        );

        return content;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Card con un valore numerico
    // ────────────────────────────────────────────────────────────────────────

    private VBox buildNumberCard(
            String labelText,
            String value,
            String color) {

        VBox card = new VBox(6);

        card.getStyleClass().add(
                "stat-card-" + color
        );

        card.setAlignment(
                Pos.CENTER_LEFT
        );

        card.setPrefWidth(220);

        Label valueLabel =
                new Label(value);

        valueLabel.getStyleClass().add(
                "stat-value-" + color
        );

        Label descriptionLabel =
                new Label(labelText);

        descriptionLabel.getStyleClass().add(
                "stat-label"
        );

        descriptionLabel.setWrapText(true);

        card.getChildren().addAll(
                valueLabel,
                descriptionLabel
        );

        return card;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Card con la classifica dei veterinari
    // ────────────────────────────────────────────────────────────────────────

    private VBox buildRankingCard(
            String title,
            Map<String, Integer> data,
            String unit) {

        VBox card = new VBox(12);

        card.getStyleClass().add(
                "ranking-card"
        );

        card.setPrefWidth(500);
        card.setMaxWidth(500);

        Label titleLabel =
                new Label(title);

        titleLabel.getStyleClass().add(
                "ranking-title"
        );

        card.getChildren().addAll(
                titleLabel,
                new Separator()
        );

        if (data == null || data.isEmpty()) {
            Label emptyLabel =
                    new Label(
                            "Nessun dato disponibile"
                    );

            emptyLabel.getStyleClass().add(
                    "register-label"
            );

            card.getChildren().add(
                    emptyLabel
            );

            return card;
        }

        int rank = 1;

        for (Map.Entry<String, Integer> entry
                : data.entrySet()) {

            HBox row = new HBox(10);

            row.setAlignment(
                    Pos.CENTER_LEFT
            );

            // Posizione nella classifica
            Label positionLabel =
                    new Label("#" + rank++);

            positionLabel.getStyleClass().add(
                    "ranking-badge"
            );

            positionLabel.setPrefWidth(40);
            positionLabel.setAlignment(Pos.CENTER);

            // Nome del veterinario
            Label nameLabel =
                    new Label(entry.getKey());

            nameLabel.getStyleClass().add(
                    "ranking-row"
            );

            HBox.setHgrow(
                    nameLabel,
                    Priority.ALWAYS
            );

            // Numero di appuntamenti
            Label valueLabel =
                    new Label(
                            entry.getValue()
                                    + " "
                                    + unit
                    );

            valueLabel.getStyleClass().add(
                    "ranking-badge"
            );

            row.getChildren().addAll(
                    positionLabel,
                    nameLabel,
                    valueLabel
            );

            card.getChildren().add(row);
        }

        return card;
    }
}
