package it.ispwproject.myvet.view.gui;

import it.ispwproject.myvet.bean.TimeSlotBean;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

public class DashboardVeterinarianGUIView
        extends DashboardGUIView {

    private static final String SLOT_AVAILABLE_COLOR =
            "#8FBC8F";

    private static final String SLOT_BOOKED_COLOR =
            "#E74C3C";

    // ScrollPane esposto al controller per aggiornare il contenuto
    public final ScrollPane calendarScroll =
            new ScrollPane();

    // ────────────────────────────────────────────────────────────────────────
    // Sezione calendario del veterinario
    // ────────────────────────────────────────────────────────────────────────

    public VBox buildCalendarSection(
            Runnable onPrevious,
            Runnable onNext,
            Runnable onToday) {

        return super.buildCalendarSection(
                onPrevious,
                onNext,
                onToday,
                calendarScroll
        );
    }

    public void refreshCalendar(
            List<TimeSlotBean> slots,
            int weekOffset) {

        double availableWidth =
                calendarScroll.getWidth() > 10
                        ? calendarScroll.getWidth()
                        : 560;

        calendarScroll.setContent(
                buildWeekCalendar(
                        slots,
                        weekOffset,
                        availableWidth
                )
        );
    }

    public void bindCalendarWidth(
            List<TimeSlotBean> slots,
            int[] weekOffsetReference) {

        calendarScroll.widthProperty().addListener(
                (observable, oldWidth, newWidth) -> {

                    if (newWidth.doubleValue() > 10) {
                        calendarScroll.setContent(
                                buildWeekCalendar(
                                        slots,
                                        weekOffsetReference[0],
                                        newWidth.doubleValue()
                                )
                        );
                    }
                }
        );
    }

    // Costruisce il calendario della settimana selezionata
    private Pane buildWeekCalendar(
            List<TimeSlotBean> slots,
            int weekOffset,
            double availableWidth) {

        LocalDate today =
                LocalDate.now(ZoneId.systemDefault());

        LocalDate monday =
                today.with(DayOfWeek.MONDAY)
                        .plusWeeks(weekOffset);

        int totalHours =
                HOUR_END - HOUR_START;

        int gridHeight =
                totalHours * HOUR_HEIGHT;

        int columnWidth = (int) Math.max(
                48,
                (availableWidth - LABEL_WIDTH - 2) / DAYS
        );

        Pane pane =
                buildCalendarPane(columnWidth);

        addMonthRow(
                pane,
                monday,
                columnWidth
        );

        addDayHeaders(
                pane,
                monday,
                today,
                columnWidth
        );

        addHourRows(
                pane,
                totalHours,
                columnWidth,
                gridHeight
        );

        addSlotBlocks(
                pane,
                slots,
                monday,
                totalHours,
                columnWidth
        );

        return pane;
    }

    // Inserisce nel calendario gli slot del veterinario
    private void addSlotBlocks(
            Pane pane,
            List<TimeSlotBean> slots,
            LocalDate firstDay,
            int totalHours,
            int columnWidth) {

        for (TimeSlotBean slot : slots) {
            if (!isDisplayable(slot, firstDay, totalHours)) {
                continue;
            }

            int dayOffset =
                    findDayOffset(
                            firstDay,
                            slot.getDate()
                    );

            LocalTime startTime =
                    slot.getStartTime();

            LocalTime endTime =
                    slot.getEndTime();

            double startFraction =
                    startTime.getHour()
                            + startTime.getMinute() / 60.0
                            - HOUR_START;

            double endFraction =
                    endTime.getHour()
                            + endTime.getMinute() / 60.0
                            - HOUR_START;

            VBox block = new VBox(2);

            block.setLayoutX(
                    LABEL_WIDTH
                            + dayOffset * columnWidth
                            + 2
            );

            block.setLayoutY(
                    HEADER_H
                            + startFraction * HOUR_HEIGHT
            );

            block.setPrefWidth(
                    columnWidth - 4
            );

            block.setPrefHeight(
                    Math.max(
                            (endFraction - startFraction)
                                    * HOUR_HEIGHT - 2,
                            20
                    )
            );

            block.setPadding(
                    new Insets(2, 3, 2, 3)
            );

            String slotColor =
                    slot.isAvailable()
                            ? SLOT_AVAILABLE_COLOR
                            : SLOT_BOOKED_COLOR;

            block.setStyle(
                    "-fx-background-color: "
                            + slotColor
                            + ";"
                            + "-fx-background-radius: 4;"
                            + "-fx-cursor: hand;"
            );

            // Stato dello slot
            Label statusLabel = new Label(
                    slot.isAvailable()
                            ? "Disponibile"
                            : "Prenotato"
            );

            statusLabel.getStyleClass().add(
                    "calendar-block-title"
            );

            statusLabel.setWrapText(true);

            // Orario dello slot
            Label timeLabel = new Label(
                    startTime + " – " + endTime
            );

            timeLabel.getStyleClass().add(
                    "calendar-block-time"
            );

            block.getChildren().addAll(
                    statusLabel,
                    timeLabel
            );

            // Se lo slot è prenotato, mostra il nome dell'animale
            if (!slot.isAvailable()
                    && slot.getBookedPetName() != null
                    && !slot.getBookedPetName().isBlank()) {

                Label petLabel = new Label(
                        slot.getBookedPetName()
                );

                petLabel.getStyleClass().add(
                        "calendar-block-time"
                );

                petLabel.setWrapText(true);

                block.getChildren().add(
                        petLabel
                );
            }

            String details =
                    buildSlotDetails(
                            slot,
                            startTime,
                            endTime
                    );

            // Tooltip con i dettagli dello slot
            Tooltip.install(
                    block,
                    new Tooltip(details)
            );

            // Click sullo slot → dialog con i dettagli
            block.setOnMouseClicked(
                    event ->
                            showSlotDetails(
                                    slot,
                                    details
                            )
            );

            pane.getChildren().add(block);
        }
    }

    private boolean isDisplayable(
            TimeSlotBean slot,
            LocalDate firstDay,
            int totalHours) {

        if (slot == null
                || slot.getDate() == null
                || slot.getStartTime() == null
                || slot.getEndTime() == null) {
            return false;
        }

        LocalTime start = slot.getStartTime();
        LocalTime end = slot.getEndTime();
        double startFraction = start.getHour()
                + start.getMinute() / 60.0 - HOUR_START;
        double endFraction = end.getHour()
                + end.getMinute() / 60.0 - HOUR_START;
        return findDayOffset(firstDay, slot.getDate()) >= 0
                && startFraction >= 0
                && endFraction <= totalHours;
    }

    // Calcola la colonna della settimana in cui mostrare lo slot
    private int findDayOffset(
            LocalDate firstDay,
            LocalDate slotDate) {

        for (int day = 0; day < DAYS; day++) {
            if (firstDay.plusDays(day)
                    .equals(slotDate)) {
                return day;
            }
        }

        return -1;
    }

    // Costruisce il testo con i dettagli dello slot
    private String buildSlotDetails(
            TimeSlotBean slot,
            LocalTime startTime,
            LocalTime endTime) {

        StringBuilder details =
                new StringBuilder();

        details.append(
                slot.isAvailable()
                        ? "Disponibile"
                        : "Prenotato"
        );

        details.append("\nOrario: ")
                .append(startTime)
                .append(" – ")
                .append(endTime);

        if (!slot.isAvailable()
                && slot.getBookedPetName() != null
                && !slot.getBookedPetName().isBlank()) {

            details.append("\nAnimale: ")
                    .append(
                            slot.getBookedPetName()
                    );
        }

        return details.toString();
    }

    // Mostra i dettagli della fascia oraria selezionata
    private void showSlotDetails(
            TimeSlotBean slot,
            String details) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Dettagli fascia oraria");

        alert.setHeaderText(
                slot.isAvailable()
                        ? "Fascia oraria disponibile"
                        : "Fascia oraria prenotata"
        );

        alert.setContentText(details);
        alert.showAndWait();
    }

    // ────────────────────────────────────────────────────────────────────────
    // Sezione destra del veterinario
    // ────────────────────────────────────────────────────────────────────────

    public VBox buildRightSection(
            VBox actionGrid,
            VBox accordion) {

        VBox section =
                new VBox(14);

        section.setAlignment(Pos.TOP_CENTER);
        section.setPrefWidth(340);
        section.setMinWidth(300);
        section.setMaxWidth(380);
        section.setPadding(new Insets(0));

        VBox.setVgrow(
                actionGrid,
                Priority.NEVER
        );

        VBox.setVgrow(
                accordion,
                Priority.NEVER
        );

        section.getChildren().addAll(
                actionGrid,
                accordion
        );

        return section;
    }

    // Costruisce i pulsanti delle funzionalità disponibili al veterinario
    public VBox buildActionGrid(
            EventHandler<ActionEvent> onAvailability,
            EventHandler<ActionEvent> onSlots,
            EventHandler<ActionEvent> onPetCare,
            EventHandler<ActionEvent> onMedicalDocuments) {

        VBox column =
                new VBox(10);

        column.setAlignment(Pos.TOP_CENTER);

        column.getChildren().addAll(
                buildActionTile(
                        "set-availability.png",
                        "Imposta disponibilità",
                        onAvailability
                ),
                buildActionTile(
                        "time-check.png",
                        "I miei orari",
                        onSlots
                ),
                buildActionTile(
                        "pet-care.png",
                        "Gestisci cura animali",
                        onPetCare
                ),
                buildActionTile(
                        "document.png",
                        "Documenti medici",
                        onMedicalDocuments
                )
        );

        return column;
    }
}
