package it.ispwproject.myvet.view.cli;

import it.ispwproject.myvet.bean.TimeSlotBean;

import java.util.List;
import java.util.Map;

@SuppressWarnings("java:S106") // System.out e' l'output previsto di questa boundary CLI.
public class ViewSlotsCLIView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione(
                "MyVet – Le mie fasce orarie"
        );
    }

    public void mostraTab(
            int availableNumber,
            int bookedNumber,
            int pastNumber) {

        CLIRenderer.vuota();

        CLIRenderer.voceMenu(
                1,
                "Disponibili  (" + availableNumber + ")"
        );

        CLIRenderer.voceMenu(
                2,
                "Prenotati    (" + bookedNumber + ")"
        );

        CLIRenderer.voceMenu(
                3,
                "Passati      (" + pastNumber + ")"
        );

        CLIRenderer.voceMenu(
                4,
                "Elimina fascia oraria"
        );

        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraDisponibili(
            List<TimeSlotBean> availableSlots) {

        CLIRenderer.sezione(
                "Fasce orarie disponibili ("
                        + availableSlots.size()
                        + ")"
        );

        if (availableSlots.isEmpty()) {
            CLIRenderer.messaggio(
                    "Nessuna fascia oraria disponibile."
            );
        } else {
            for (TimeSlotBean slot : availableSlots) {
                System.out.printf(
                        "  %s  %s – %s    %s Disponibile%n",
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        CLIRenderer.AVAIL
                );
            }
        }

        CLIRenderer.separatore();
    }

    public void mostraPrenotati(
            List<TimeSlotBean> bookedSlots,
            Map<Integer, String> petBySlot) {

        CLIRenderer.sezione(
                "Fasce orarie prenotate ("
                        + bookedSlots.size()
                        + ")"
        );

        if (bookedSlots.isEmpty()) {
            CLIRenderer.messaggio(
                    "Nessuna fascia oraria prenotata."
            );
        } else {
            for (TimeSlotBean slot : bookedSlots) {
                String petName =
                        getPetName(
                                slot,
                                petBySlot
                        );

                System.out.printf(
                        "  %s  %s – %s    %s Prenotato  |  Animale: %s%n",
                        slot.getDate(),
                        slot.getStartTime(),
                        slot.getEndTime(),
                        CLIRenderer.BOOKED,
                        petName
                );
            }
        }

        CLIRenderer.separatore();
    }

    public void mostraPassati(
            List<TimeSlotBean> pastSlots,
            Map<Integer, String> petBySlot) {

        CLIRenderer.sezione(
                "Fasce orarie passate ("
                        + pastSlots.size()
                        + ")"
        );

        if (pastSlots.isEmpty()) {
            CLIRenderer.messaggio(
                    "Nessuna fascia oraria passata."
            );

            CLIRenderer.separatore();
            return;
        }

        for (TimeSlotBean slot : pastSlots) {
            String symbol =
                    slot.isAvailable()
                            ? CLIRenderer.AVAIL
                            : CLIRenderer.BOOKED;

            String status =
                    slot.isAvailable()
                            ? "Non utilizzato"
                            : "Prenotato";

            String additionalInformation = "";

            if (!slot.isAvailable()) {
                additionalInformation =
                        "  |  Animale: "
                                + getPetName(
                                slot,
                                petBySlot
                        );
            }

            System.out.printf(
                    "  %s  %s – %s    %s %s%s%n",
                    slot.getDate(),
                    slot.getStartTime(),
                    slot.getEndTime(),
                    symbol,
                    status,
                    additionalInformation
            );
        }

        CLIRenderer.separatore();
    }

    private String getPetName(
            TimeSlotBean slot,
            Map<Integer, String> petBySlot) {

        if (slot.getBookedPetName() != null
                && !slot.getBookedPetName().isBlank()) {

            return slot.getBookedPetName();
        }

        return petBySlot.getOrDefault(
                slot.getId(),
                "—"
        );
    }

    public void mostraSlotDisponibili(
            List<TimeSlotBean> availableSlots) {

        CLIRenderer.sezione(
                "Fasce orarie disponibili eliminabili"
        );

        for (int i = 0;
             i < availableSlots.size();
             i++) {

            TimeSlotBean slot =
                    availableSlots.get(i);

            System.out.printf(
                    "  [%d] %s  %s – %s%n",
                    i + 1,
                    slot.getDate(),
                    slot.getStartTime(),
                    slot.getEndTime()
            );
        }

        CLIRenderer.voceMenuZero("Annulla");
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

    public void mostraSuccessoEliminazione() {
        CLIRenderer.successo(
                "Fascia oraria eliminata con successo."
        );
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }
}
