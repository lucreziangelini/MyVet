package it.ispwproject.myvet.view.cli;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class SetAvailabilityCLIView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione("MyVet  –  Aggiungi disponibilità");
    }

    public LocalDate chiediData() {
        while (true) {
            String input = CLIRenderer.chiediCampo("Data (YYYY-MM-DD)");
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                CLIRenderer.errore("Formato non valido. Usa YYYY-MM-DD.");
            }
        }
    }

    public LocalTime chiediOra(String label) {
        while (true) {
            String input = CLIRenderer.chiediCampo(label + " (HH:MM)");
            try {
                return LocalTime.parse(input);
            } catch (DateTimeParseException e) {
                CLIRenderer.errore("Formato non valido. Usa HH:MM.");
            }
        }
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
    }

    public void mostraSuccesso() {
        CLIRenderer.successo("Slot aggiunto con successo.");
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }
}