package it.ispwproject.myvet.view.cli;

import it.ispwproject.myvet.bean.BookingResponseBean;
import it.ispwproject.myvet.bean.PetBean;
import it.ispwproject.myvet.bean.TimeSlotBean;
import it.ispwproject.myvet.bean.VeterinarianBean;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@SuppressWarnings("java:S106") // System.out e' l'output previsto di questa boundary CLI.
public class BookAppointmentCLIView {

    private static final String BACK_LABEL = "Indietro";

    public void mostraIntestazione() {
        CLIRenderer.intestazione(
                "MyVet – Prenota un appuntamento"
        );
    }

    public void mostraAnimali(List<PetBean> pets) {
        CLIRenderer.sezione("Animali registrati");

        for (int i = 0; i < pets.size(); i++) {
            PetBean pet = pets.get(i);

            String description = pet.getName()
                    + " – " + pet.getSpecies();

            if (pet.getBreed() != null
                    && !pet.getBreed().isBlank()) {
                description += " – " + pet.getBreed();
            }

            CLIRenderer.voceMenu(
                    i + 1,
                    description
            );
        }

        CLIRenderer.voceMenuZero(BACK_LABEL);
    }

    public LocalDate chiediDataPreferita() {
        while (true) {
            String input = CLIRenderer.chiediCampo(
                    "Data preferita (YYYY-MM-DD, oppure 0 per tornare indietro)"
            );

            if ("0".equals(input)) {
                return null;
            }

            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                CLIRenderer.errore(
                        "Formato non valido. Usa YYYY-MM-DD."
                );
            }
        }
    }

    public void mostraVeterinari(
            List<VeterinarianBean> favourites,
            List<VeterinarianBean> others) {

        CLIRenderer.sezione("Veterinari disponibili");

        int index = 1;

        if (!favourites.isEmpty()) {
            CLIRenderer.messaggio(
                    CLIRenderer.STAR
                            + " Veterinari preferiti"
            );

            for (VeterinarianBean veterinarian : favourites) {
                mostraVeterinario(index++, veterinarian);
            }

            if (!others.isEmpty()) {
                CLIRenderer.vuota();
            }
        }

        if (!others.isEmpty()) {
            if (!favourites.isEmpty()) {
                CLIRenderer.messaggio(
                        "Altri veterinari"
                );
            }

            for (VeterinarianBean veterinarian : others) {
                mostraVeterinario(index++, veterinarian);
            }
        }

        CLIRenderer.voceMenuZero(BACK_LABEL);
    }

    private void mostraVeterinario(
            int index,
            VeterinarianBean veterinarian) {

        String specialization =
                veterinarian.getSpecialization() != null
                        ? veterinarian.getSpecialization()
                        : "";

        System.out.printf(
                "  [%d] %-25s  %s%n",
                index,
                veterinarian.getFullName(),
                specialization
        );
    }

    public void mostraSlot(List<TimeSlotBean> slots) {
        CLIRenderer.sezione("Orari disponibili");

        for (int i = 0; i < slots.size(); i++) {
            TimeSlotBean slot = slots.get(i);

            System.out.printf(
                    "  [%d] %s   %s – %s%n",
                    i + 1,
                    slot.getDate(),
                    slot.getStartTime(),
                    slot.getEndTime()
            );
        }

        CLIRenderer.voceMenuZero(BACK_LABEL);
    }

    public void mostraRiepilogo(
            BookingResponseBean summary) {

        CLIRenderer.sezione(
                "Riepilogo appuntamento"
        );

        CLIRenderer.campo(
                "Animale",
                summary.getPet().getName()
        );

        CLIRenderer.campo(
                "Veterinario",
                summary.getVeterinarian().getFullName()
        );

        CLIRenderer.campo(
                "Data",
                summary.getTimeSlot()
                        .getDate()
                        .toString()
        );

        CLIRenderer.campo(
                "Orario",
                summary.getTimeSlot().getStartTime()
                        + " – "
                        + summary.getTimeSlot().getEndTime()
        );
    }

    public void mostraConferma(
            BookingResponseBean response) {

        CLIRenderer.sezione(
                "Appuntamento confermato"
        );

        CLIRenderer.campo(
                "Animale",
                response.getPet().getName()
        );

        CLIRenderer.campo(
                "Veterinario",
                response.getVeterinarian().getFullName()
        );

        CLIRenderer.campo(
                "Stato",
                response.getStatus() != null
                        ? response.getStatus().toString()
                        : "—"
        );

        CLIRenderer.campo(
                "Data",
                response.getTimeSlot()
                        .getDate()
                        .toString()
        );

        CLIRenderer.campo(
                "Orario",
                response.getTimeSlot().getStartTime()
                        + " – "
                        + response.getTimeSlot().getEndTime()
        );

        CLIRenderer.separatore();
    }

    public void mostraMessaggio(String message) {
        CLIRenderer.messaggio(message);
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
