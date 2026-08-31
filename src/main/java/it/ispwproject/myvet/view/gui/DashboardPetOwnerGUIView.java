package it.ispwproject.myvet.view.gui;

import it.ispwproject.myvet.bean.BookingResponseBean;
import it.ispwproject.myvet.enumerator.BookingStatus;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
    // Sidebar del Pet Owner
    // ────────────────────────────────────────────────────────────────────────

    public VBox buildSidebar(
            VBox actionButtons,
            VBox accordion,
            Runnable onLogout) {

        VBox sidebar = new VBox(13);
        sidebar.setPrefWidth(235);
        sidebar.setMinWidth(235);
        sidebar.setMaxWidth(235);
        sidebar.setPadding(new Insets(18));
        sidebar.getStyleClass().add("dashboard-sidebar");

        Label dashboardLabel = new Label("Riepilogo");
        dashboardLabel.getStyleClass().add("sidebar-section-title");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutButton = new Button("↪  Esci");
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.getStyleClass().add("sidebar-logout");
        logoutButton.setOnAction(event -> onLogout.run());

        HBox brand = new HBox(7);
        brand.setAlignment(Pos.CENTER_LEFT);
        brand.getStyleClass().add("myvet-brand");

        var logoStream = getClass().getResourceAsStream(
                "/images/myvet_logo.png"
        );

        if (logoStream != null) {
            ImageView logo = new ImageView(
                    new Image(logoStream, 54, 54, true, true)
            );
            logo.setFitHeight(46);
            logo.setFitWidth(46);
            logo.setPreserveRatio(true);
            logo.setSmooth(true);
            brand.getChildren().add(logo);
        }

        Label brandName = new Label("MyVet");
        brandName.getStyleClass().add("myvet-brand-name");
        brand.getChildren().add(brandName);

        sidebar.getChildren().addAll(
                brand,
                dashboardLabel,
                actionButtons,
                accordion,
                spacer,
                logoutButton
        );

        return sidebar;
    }

    // Costruisce l'area principale con benvenuto e calendario
    public HBox buildMainContent(
            it.ispwproject.myvet.model.User owner,
            VBox calendarSection,
            List<BookingResponseBean> bookings,
            Runnable onBook) {

        HBox content = new HBox(18);
        content.setAlignment(Pos.TOP_CENTER);
        content.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(content, Priority.ALWAYS);

        VBox leftColumn = new VBox(16);
        leftColumn.setPrefWidth(320);
        leftColumn.setMinWidth(285);
        leftColumn.setMaxWidth(350);

        VBox welcomeCard = new VBox(4);
        welcomeCard.setPadding(new Insets(18, 22, 18, 22));
        welcomeCard.setMaxWidth(Double.MAX_VALUE);
        welcomeCard.getStyleClass().add("welcome-card");

        String displayedName = owner == null
                || owner.getName() == null
                || owner.getName().isBlank()
                ? "Proprietario"
                : owner.getName();

        String welcome = owner == null
                ? "Bentornato"
                : owner.getWelcome();

        Label title = new Label(
                welcome + ", " + displayedName + "!"
        );
        title.getStyleClass().add("dashboard-welcome-title");

        Label subtitle = new Label(
                "Qui trovi i tuoi prossimi appuntamenti e tutti i servizi "
                        + "dedicati ai tuoi animali."
        );
        subtitle.getStyleClass().add("info-text");
        subtitle.setWrapText(true);

        Button quickBook = new Button("Prenota ora  →");
        quickBook.getStyleClass().add("dashboard-quick-action");
        quickBook.setOnAction(event -> onBook.run());

        welcomeCard.getChildren().addAll(
                title,
                subtitle,
                quickBook
        );

        VBox upcomingCard = buildUpcomingAppointmentsCard(bookings);
        leftColumn.getChildren().addAll(welcomeCard, upcomingCard);

        VBox.setVgrow(calendarSection, Priority.ALWAYS);
        HBox.setHgrow(calendarSection, Priority.ALWAYS);
        calendarSection.setMaxWidth(Double.MAX_VALUE);
        content.getChildren().addAll(leftColumn, calendarSection);

        return content;
    }

    private VBox buildUpcomingAppointmentsCard(
            List<BookingResponseBean> bookings) {

        VBox card = new VBox(10);
        card.setPadding(new Insets(18));
        card.setMaxWidth(Double.MAX_VALUE);
        card.getStyleClass().add("upcoming-card");

        Label title = new Label("Prossimi appuntamenti");
        title.getStyleClass().add("section-title");
        card.getChildren().add(title);

        List<BookingResponseBean> upcoming = bookings.stream()
                .filter(booking ->
                        booking.getStatus() != BookingStatus.CANCELLED)
                .filter(booking -> booking.getTimeSlot() != null)
                .filter(booking ->
                        booking.getTimeSlot().getDate() != null)
                .filter(booking ->
                        booking.getTimeSlot().getStartTime() != null)
                .filter(booking ->
                        !booking.getTimeSlot().getDate()
                                .isBefore(LocalDate.now()))
                .sorted(Comparator
                        .comparing((BookingResponseBean booking) ->
                                booking.getTimeSlot().getDate())
                        .thenComparing(booking ->
                                booking.getTimeSlot().getStartTime()))
                .limit(3)
                .toList();

        if (upcoming.isEmpty()) {
            Label empty = new Label(
                    "Non hai ancora appuntamenti in programma."
            );
            empty.getStyleClass().add("info-text");
            empty.setWrapText(true);
            card.getChildren().add(empty);
            return card;
        }

        for (BookingResponseBean booking : upcoming) {
            card.getChildren().add(
                    buildAppointmentPreview(booking)
            );
        }

        return card;
    }

    private HBox buildAppointmentPreview(
            BookingResponseBean booking) {

        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofPattern(
                        "dd MMM",
                        Locale.ITALIAN
                );

        VBox dateBox = new VBox(1);
        dateBox.setAlignment(Pos.CENTER);
        dateBox.setMinWidth(58);
        dateBox.getStyleClass().add("appointment-date-box");

        Label date = new Label(
                booking.getTimeSlot().getDate()
                        .format(dateFormatter)
        );
        date.getStyleClass().add("appointment-date");

        Label time = new Label(
                booking.getTimeSlot().getStartTime().toString()
        );
        time.getStyleClass().add("appointment-time");
        dateBox.getChildren().addAll(date, time);

        String petName = booking.getPet() == null
                ? "Animale"
                : booking.getPet().getName();

        String veterinarianName = booking.getVeterinarian() == null
                ? "Veterinario da definire"
                : booking.getVeterinarian().getFullName();

        Label pet = new Label(petName);
        pet.getStyleClass().add("appointment-pet");

        Label veterinarian = new Label(veterinarianName);
        veterinarian.getStyleClass().add("info-text");
        veterinarian.setWrapText(true);

        VBox details = new VBox(2, pet, veterinarian);
        HBox.setHgrow(details, Priority.ALWAYS);

        HBox row = new HBox(10, dateBox, details);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);
        row.getStyleClass().add("appointment-preview");
        return row;
    }

    // Sezione destra mantenuta per le altre viste che la riutilizzano
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
            EventHandler<ActionEvent> onManagePets,
            EventHandler<ActionEvent> onActivities,
            EventHandler<ActionEvent> onMedicalDocuments) {

        VBox buttons = new VBox(9);
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
                        "pet-care.png",
                        "I miei animali",
                        onManagePets
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
