package it.ispwproject.myvet.view.cli;

import it.ispwproject.myvet.bean.ActivityBean;
import it.ispwproject.myvet.bean.PetBean;
import it.ispwproject.myvet.bean.ProgressBean;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManagePetCareCLIView {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy 'alle' HH:mm"
            );

    public void mostraIntestazione() {
        CLIRenderer.intestazione(
                "MyVet – Gestisci la cura degli animali"
        );
    }

    public void mostraAnimali(List<PetBean> pets) {
        if (pets.isEmpty()) {
            CLIRenderer.messaggio(
                    "Nessun animale associato ai tuoi appuntamenti."
            );
            return;
        }

        CLIRenderer.sezione(
                "Animali seguiti"
        );

        for (int i = 0; i < pets.size(); i++) {
            PetBean pet = pets.get(i);

            String description =
                    pet.getName()
                            + " – "
                            + pet.getSpecies();

            if (pet.getBreed() != null
                    && !pet.getBreed().isBlank()) {
                description += " – " + pet.getBreed();
            }

            CLIRenderer.voceMenu(
                    i + 1,
                    description
            );
        }

        CLIRenderer.voceMenuZero("Indietro");
    }

    public void mostraSchedaAnimale(
            PetBean pet,
            ProgressBean progress) {

        CLIRenderer.vuota();
        System.out.println(CLIRenderer.LINE_DECO);

        System.out.printf(
                "  Animale: %s%n",
                pet.getName()
        );

        System.out.printf(
                "  Specie:  %s%n",
                pet.getSpecies()
        );

        if (pet.getBreed() != null
                && !pet.getBreed().isBlank()) {

            System.out.printf(
                    "  Razza:   %s%n",
                    pet.getBreed()
            );
        }

        System.out.println(CLIRenderer.LINE_DECO);

        CLIRenderer.sezione("Progressi");

        if (progress == null) {
            CLIRenderer.messaggio(
                    "Nessun progresso annotato."
            );
            return;
        }

        if (progress.getNotes() != null) {
            for (String line :
                    progress.getNotes().split("\n")) {

                CLIRenderer.messaggio(line);
            }
        }

        if (progress.getUpdatedAt() != null) {
            CLIRenderer.messaggio(
                    "Ultimo aggiornamento: "
                            + progress.getUpdatedAt()
                            .format(DATE_TIME_FORMATTER)
            );
        }
    }

    public void mostraMenuAnimale() {
        CLIRenderer.sezione("Azioni");

        CLIRenderer.voceMenu(
                1,
                "Annota progressi"
        );

        CLIRenderer.voceMenu(
                2,
                "Assegna attività di cura"
        );

        CLIRenderer.voceMenu(
                3,
                "Visualizza attività assegnate"
        );

        CLIRenderer.voceMenuZero(
                "Torna alla lista"
        );
    }

    public void mostraAttivita(
            List<ActivityBean> activities) {

        CLIRenderer.sezione(
                "Attività di cura assegnate"
        );

        if (activities.isEmpty()) {
            CLIRenderer.messaggio(
                    "Nessuna attività assegnata."
            );
            return;
        }

        for (ActivityBean activity : activities) {
            String symbol =
                    activity.isCompleted()
                            ? CLIRenderer.DONE
                            : CLIRenderer.PENDING;

            String status =
                    activity.isCompleted()
                            ? "Completata"
                            : "In sospeso";

            String creationDate =
                    activity.getCreatedAt() != null
                            ? activity.getCreatedAt()
                            .toLocalDate()
                            .toString()
                            : "—";

            System.out.printf(
                    "  %s  %-12s  %s  (%s)%n",
                    symbol,
                    status,
                    activity.getDescription(),
                    creationDate
            );
        }
    }

    public void mostraSuccesso(String message) {
        CLIRenderer.successo(message);
    }

    public void mostraErrore(String message) {
        CLIRenderer.errore(message);
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

    public String chiediTesto(String prompt) {
        return CLIRenderer.chiediCampo(prompt);
    }

    public void attesaInvio() {
        CLIRenderer.chiediCampo(
                "[ INVIO per tornare ]"
        );
    }
}