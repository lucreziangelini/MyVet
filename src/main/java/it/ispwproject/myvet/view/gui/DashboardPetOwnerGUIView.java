package it.ispwproject.myvet.view.gui;

import it.ispwproject.myvet.bean.BookingResponseBean;
import it.ispwproject.myvet.enumerator.BookingStatus;
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

public class DashboardPetOwnerGUIView extends DashboardGUIView {

    private static final String BOOKING_COLOR = "#8FBC8F";

    // ScrollPane esposto al controller per aggiornare il calendario
    public final ScrollPane calendarScroll = new ScrollPane();

    // ────────────────────────────────────────────────────────────────────────
    // Sezione calendario appuntamenti del Pet Owner
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
            List<BookingResponseBean> bookings,
            int weekOffset) {

        double availableWidth =
                calendarScroll.getWidth() > 10
                        ? calendarScroll.getWidth()
                        : 560;

        calendarScroll.setContent(
                buildWeekCalendar(
                        bookings,
                        weekOffset,
                        availableWidth
                )
        );
    }

    public void bindCalendarWidth(
            List<BookingResponseBean> bookings,
            int[] weekOffsetReference) {

        calendarScroll.widthProperty().addListener(
                (observable, oldWidth, newWidth) -> {

                    if (newWidth.doubleValue() > 10) {
                        calendarScroll.setContent(
                                buildWeekCalendar(
                                        bookings,
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
            List<BookingResponseBean> bookings,
            int weekOffset,
            double availableWidth) {

        LocalDate today =
                LocalDate.now(ZoneId.systemDefault());

        LocalDate monday =
                today.with(DayOfWeek.MONDAY)
                        .plusWeeks(weekOffset);

        int totalHours = HOUR_END - HOUR_START;
        int gridHeight = totalHours * HOUR_HEIGHT;

        int columnWidth = (int) Math.max(
                48,
                (availableWidth - LABEL_WIDTH - 2) / DAYS
        );

        Pane pane =
                buildCalendarPane(monday, columnWidth);

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

        addBookingBlocks(
                pane,
                bookings,
                monday,
                totalHours,
                columnWidth
        );

        return pane;
    }

    // Inserisce nel calendario gli appuntamenti non cancellati
    private void addBookingBlocks(
            Pane pane,
            List<BookingResponseBean> bookings,
            LocalDate firstDay,
            int totalHours,
            int columnWidth) {

        for (BookingResponseBean booking : bookings) {

            if (booking.getStatus() == BookingStatus.CANCELLED) {
                continue;
            }

            // Ignora eventuali prenotazioni incomplete
            if (booking.getTimeSlot() == null
                    || booking.getPet() == null
                    || booking.getVeterinarian() == null) {
                continue;
            }

            LocalDate bookingDate =
                    booking.getTimeSlot().getDate();

            LocalTime startTime =
                    booking.getTimeSlot().getStartTime();

            LocalTime endTime =
                    booking.getTimeSlot().getEndTime();

            int dayOffset =
                    findDayOffset(
                            firstDay,
                            bookingDate
                    );

            // L'appuntamento non appartiene alla settimana visualizzata
            if (dayOffset < 0) {
                continue;
            }

            double startFraction =
                    startTime.getHour()
                            + startTime.getMinute() / 60.0
                            - HOUR_START;

            double endFraction =
                    endTime.getHour()
                            + endTime.getMinute() / 60.0
                            - HOUR_START;

            // Ignora appuntamenti esterni alla fascia oraria del calendario
            if (startFraction < 0
                    || endFraction > totalHours) {
                continue;
            }

            VBox block = new VBox(1);

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

            block.setStyle(
                    "-fx-background-color: "
                            + BOOKING_COLOR
                            + ";"
                            + "-fx-background-radius: 4;"
                            + "-fx-cursor: hand;"
            );

            // Titolo visualizzato nel blocco del calendario
            Label title = new Label(
                    booking.getPet().getName()
                            + " – "
                            + booking.getVeterinarian().getSurname()
            );

            title.getStyleClass().add(
                    "calendar-block-title"
            );

            title.setWrapText(true);

            // Orario visualizzato nel blocco
            Label time = new Label(
                    startTime + " – " + endTime
            );

            time.getStyleClass().add(
                    "calendar-block-time"
            );

            block.getChildren().addAll(
                    title,
                    time
            );

            // Tooltip con i dettagli essenziali dell'appuntamento
            Tooltip.install(
                    block,
                    buildTooltip(
                            booking,
                            startTime,
                            endTime
                    )
            );

            // Click sul blocco → dialog con i dettagli completi
            block.setOnMouseClicked(
                    event -> showBookingDetails(booking)
            );

            pane.getChildren().add(block);
        }
    }

    // Calcola la colonna della settimana in cui mostrare l'appuntamento
    private int findDayOffset(
            LocalDate firstDay,
            LocalDate bookingDate) {

        for (int day = 0; day < DAYS; day++) {
            if (firstDay.plusDays(day).equals(bookingDate)) {
                return day;
            }
        }

        return -1;
    }

    // Costruisce il tooltip associato a un appuntamento
    private Tooltip buildTooltip(
            BookingResponseBean booking,
            LocalTime startTime,
            LocalTime endTime) {

        return new Tooltip(
                "Animale: "
                        + booking.getPet().getName()
                        + "\nVeterinario: "
                        + booking.getVeterinarian().getFullName()
                        + "\nOrario: "
                        + startTime
                        + " – "
                        + endTime
        );
    }

    // Mostra i dettagli dell'appuntamento selezionato
    private void showBookingDetails(
            BookingResponseBean booking) {

        Alert alert =
                new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(
                "Dettagli appuntamento"
        );

        alert.setHeaderText(
                booking.getPet().getName()
        );

        alert.setContentText(
                "Veterinario: "
                        + booking.getVeterinarian().getFullName()
                        + "\n"
                        + "Data: "
                        + booking.getTimeSlot().getDate()
                        + "\n"
                        + "Orario: "
                        + booking.getTimeSlot().getStartTime()
                        + " – "
                        + booking.getTimeSlot().getEndTime()
                        + "\n"
                        + "Stato: "
                        + booking.getStatus()
        );

        alert.showAndWait();
    }

    // ────────────────────────────────────────────────────────────────────────
    // Sezione destra del Pet Owner
    // ────────────────────────────────────────────────────────────────────────

    public VBox buildRightSection(
            VBox actionButtons,
            VBox accordion) {

        VBox section = new VBox(14);

        section.setAlignment(Pos.TOP_CENTER);
        section.setPrefWidth(320);
        section.setMinWidth(320);
        section.setPadding(new Insets(0));

        Region spacer = new Region();

        spacer.setPrefHeight(40);
        spacer.setMinHeight(40);
        spacer.setMaxHeight(40);

        VBox.setVgrow(
                actionButtons,
                Priority.NEVER
        );

        VBox.setVgrow(
                accordion,
                Priority.NEVER
        );

        section.getChildren().addAll(
                spacer,
                actionButtons,
                accordion
        );

        return section;
    }

    // Costruisce i pulsanti delle funzionalità disponibili al Pet Owner
    public VBox buildActionButtons(
            EventHandler<ActionEvent> onBook,
            EventHandler<ActionEvent> onViewAppointments,
            EventHandler<ActionEvent> onActivities,
            EventHandler<ActionEvent> onMedicalDocuments) {

        VBox buttons = new VBox(14);
        buttons.setAlignment(Pos.CENTER);

        buttons.getChildren().addAll(
                buildActionTile(
                        "booking.png",
                        "Prenota appuntamento",
                        onBook
                ),
                buildActionTile(
                        "calendar.png",
                        "I miei appuntamenti",
                        onViewAppointments
                ),
                buildActionTile(
                        "task-checklist.png",
                        "Attività di cura",
                        onActivities
                ),
                buildActionTile(
                        "document.png",
                        "Documenti medici",
                        onMedicalDocuments
                )
        );

        return buttons;
    }
}