package it.ispwproject.myvet.view.cli;

import it.ispwproject.myvet.bean.BookingResponseBean;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewAppointmentCLIView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione(
                "MyVet – I miei appuntamenti"
        );
    }

    public void mostraTab(
            int confirmedNumber,
            int cancelledNumber,
            int pastNumber) {

        CLIRenderer.vuota();

        CLIRenderer.voceMenu(
                1,
                "Confermati   (" + confirmedNumber + ")"
        );

        CLIRenderer.voceMenu(
                2,
                "Cancellati   (" + cancelledNumber + ")"
        );

        CLIRenderer.voceMenu(
                3,
                "Passati      (" + pastNumber + ")"
        );

        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraConfermati(
            List<BookingResponseBean> bookings) {

        CLIRenderer.sezione(
                "Appuntamenti confermati"
        );

        if (bookings.isEmpty()) {
            CLIRenderer.messaggio(
                    "Non hai appuntamenti confermati."
            );

            CLIRenderer.separatore();
            return;
        }

        for (BookingResponseBean booking : bookings) {
            CLIRenderer.vuota();

            System.out.println(
                    "  " + CLIRenderer.LINE_THIN
            );

            mostraDatiAppuntamento(
                    booking,
                    true
            );
        }

        CLIRenderer.vuota();
        CLIRenderer.separatore();
    }

    public void mostraCancellati(
            List<BookingResponseBean> bookings) {

        CLIRenderer.sezione(
                "Appuntamenti cancellati"
        );

        if (bookings.isEmpty()) {
            CLIRenderer.messaggio(
                    "Non hai appuntamenti cancellati."
            );

            CLIRenderer.separatore();
            return;
        }

        for (BookingResponseBean booking : bookings) {
            CLIRenderer.vuota();

            System.out.println(
                    "  " + CLIRenderer.LINE_THIN
            );

            mostraDatiAppuntamento(
                    booking,
                    false
            );
        }

        CLIRenderer.vuota();
        CLIRenderer.separatore();
    }

    public void mostraPassati(
            List<BookingResponseBean> pastBookings) {

        CLIRenderer.sezione(
                "Appuntamenti passati"
        );

        if (pastBookings.isEmpty()) {
            CLIRenderer.messaggio(
                    "Nessun appuntamento passato."
            );

            CLIRenderer.separatore();
            return;
        }

        Map<String, List<BookingResponseBean>> byPet =
                pastBookings.stream()
                        .collect(
                                Collectors.groupingBy(
                                        booking ->
                                                booking.getPet()
                                                        .getName()
                                )
                        );

        List<String> petNames =
                byPet.keySet()
                        .stream()
                        .sorted()
                        .toList();

        for (String petName : petNames) {
            List<BookingResponseBean> group =
                    byPet.get(petName)
                            .stream()
                            .sorted((first, second) ->
                                    second.getTimeSlot()
                                            .getDate()
                                            .compareTo(
                                                    first.getTimeSlot()
                                                            .getDate()
                                            ))
                            .toList();

            CLIRenderer.vuota();

            System.out.printf(
                    "  %s  (%d %s)%n",
                    petName,
                    group.size(),
                    group.size() == 1
                            ? "appuntamento"
                            : "appuntamenti"
            );

            System.out.println(
                    "  " + CLIRenderer.LINE_THIN
            );

            for (BookingResponseBean booking : group) {
                System.out.printf(
                        "  %s  %s  %s–%s  %s%n",
                        CLIRenderer.BULLET,
                        booking.getTimeSlot().getDate(),
                        booking.getTimeSlot().getStartTime(),
                        booking.getTimeSlot().getEndTime(),
                        booking.getVeterinarian()
                                .getFullName()
                );
            }
        }

        CLIRenderer.vuota();
        CLIRenderer.separatore();
    }

    private void mostraDatiAppuntamento(
            BookingResponseBean booking,
            boolean showVeterinarianEmail) {

        CLIRenderer.campo(
                "Animale",
                booking.getPet().getName()
        );

        CLIRenderer.campo(
                "Veterinario",
                booking.getVeterinarian()
                        .getFullName()
        );

        CLIRenderer.campo(
                "Data",
                booking.getTimeSlot().getDate()
                        + "  "
                        + booking.getTimeSlot().getStartTime()
                        + " – "
                        + booking.getTimeSlot().getEndTime()
        );

        CLIRenderer.campo(
                "Stato",
                booking.getStatus().toString()
        );

        if (showVeterinarianEmail
                && booking.getVeterinarian().getEmail() != null) {

            CLIRenderer.campo(
                    "Email",
                    booking.getVeterinarian().getEmail()
            );
        }
    }

    public int chiediScelta(
            String prompt,
            int min,
            int max) {

        return CLIRenderer.chiediScelta(
                prompt,
                min,
                max
        );
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }
}