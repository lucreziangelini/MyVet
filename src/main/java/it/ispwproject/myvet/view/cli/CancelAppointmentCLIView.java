package it.ispwproject.myvet.view.cli;

import it.ispwproject.myvet.bean.BookingResponseBean;

import java.util.List;

@SuppressWarnings("java:S106") // System.out e' l'output previsto di questa boundary CLI.
public class CancelAppointmentCLIView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione(
                "MyVet – Annulla un appuntamento"
        );
    }

    public void mostraPrenotazioniAnnullabili(
            List<BookingResponseBean> cancellable) {

        CLIRenderer.sezione(
                "Appuntamenti attivi"
        );

        int petWidth = cancellable.stream()
                .mapToInt(booking ->
                        booking.getPet()
                                .getName()
                                .length())
                .max()
                .orElse(10);

        int veterinarianWidth = cancellable.stream()
                .mapToInt(booking ->
                        booking.getVeterinarian()
                                .getFullName()
                                .length())
                .max()
                .orElse(20);

        int numberWidth = String.valueOf(
                cancellable.size()
        ).length();

        String format =
                "  [%-" + numberWidth + "d] %-"
                        + petWidth + "s  %-"
                        + veterinarianWidth
                        + "s  %s  %s–%s%n";

        for (int i = 0; i < cancellable.size(); i++) {
            BookingResponseBean booking =
                    cancellable.get(i);

            System.out.printf(
                    format,
                    i + 1,
                    booking.getPet().getName(),
                    booking.getVeterinarian()
                            .getFullName(),
                    booking.getTimeSlot().getDate(),
                    booking.getTimeSlot().getStartTime(),
                    booking.getTimeSlot().getEndTime()
            );
        }

        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraRiepilogo(
            BookingResponseBean selected) {

        CLIRenderer.sezione(
                "Conferma annullamento"
        );

        CLIRenderer.campo(
                "Animale",
                selected.getPet().getName()
        );

        CLIRenderer.campo(
                "Veterinario",
                selected.getVeterinarian()
                        .getFullName()
        );

        CLIRenderer.campo(
                "Data",
                selected.getTimeSlot()
                        .getDate()
                        .toString()
        );

        CLIRenderer.campo(
                "Orario",
                selected.getTimeSlot().getStartTime()
                        + " – "
                        + selected.getTimeSlot().getEndTime()
        );
    }

    public void mostraSuccesso() {
        CLIRenderer.successo(
                "Appuntamento annullato con successo."
        );
    }

    public void mostraMessaggio(String message) {
        CLIRenderer.messaggio(message);
    }

    public void mostraErrore(String message) {
        CLIRenderer.errore(message);
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

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }
}
