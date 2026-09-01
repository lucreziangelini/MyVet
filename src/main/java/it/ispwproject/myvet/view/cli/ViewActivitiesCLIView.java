package it.ispwproject.myvet.view.cli;

import it.ispwproject.myvet.bean.ActivityBean;

import java.util.List;

@SuppressWarnings("java:S106") // System.out e' l'output previsto di questa boundary CLI.
public class ViewActivitiesCLIView {

    public void mostraIntestazione() {
        CLIRenderer.intestazione(
                "MyVet – Le mie attività di cura"
        );
    }

    public void mostraAttivita(
            List<ActivityBean> activities) {

        if (activities.isEmpty()) {
            CLIRenderer.messaggio(
                    "Nessuna attività di cura assegnata."
            );
            return;
        }

        mostraAttivitaInSospeso(activities);
        mostraAttivitaCompletate(activities);
    }

    private void mostraAttivitaInSospeso(
            List<ActivityBean> activities) {

        CLIRenderer.sezione("Da completare");

        boolean hasPending = false;

        for (ActivityBean activity : activities) {
            if (!activity.isCompleted()) {
                System.out.printf(
                        "  %s  %s%s%s%n",
                        CLIRenderer.PENDING,
                        activity.getDescription(),
                        getPetInformation(activity),
                        getVeterinarianInformation(activity)
                );

                hasPending = true;
            }
        }

        if (!hasPending) {
            CLIRenderer.messaggio(
                    "Nessuna attività in sospeso."
            );
        }
    }

    private void mostraAttivitaCompletate(
            List<ActivityBean> activities) {

        CLIRenderer.sezione("Completate");

        boolean hasCompleted = false;

        for (ActivityBean activity : activities) {
            if (activity.isCompleted()) {
                System.out.printf(
                        "  %s  %s%s%s%n",
                        CLIRenderer.DONE,
                        activity.getDescription(),
                        getPetInformation(activity),
                        getVeterinarianInformation(activity)
                );

                hasCompleted = true;
            }
        }

        if (!hasCompleted) {
            CLIRenderer.messaggio(
                    "Nessuna attività completata."
            );
        }
    }

    public void mostraPendingPerSelezione(
            List<ActivityBean> pendingActivities) {

        CLIRenderer.sezione(
                "Segna come completata"
        );

        for (int i = 0;
             i < pendingActivities.size();
             i++) {

            ActivityBean activity =
                    pendingActivities.get(i);

            System.out.printf(
                    "  [%d] %s  %s%s%n",
                    i + 1,
                    CLIRenderer.PENDING,
                    activity.getDescription(),
                    getPetInformation(activity)
            );
        }

        CLIRenderer.voceMenuZero("Annulla");
    }

    private String getPetInformation(
            ActivityBean activity) {

        if (activity.getPet() == null) {
            return "";
        }

        return "  (animale: "
                + activity.getPet().getName()
                + ")";
    }

    private String getVeterinarianInformation(
            ActivityBean activity) {

        if (activity.getVeterinarian() == null) {
            return "";
        }

        return "  (assegnata da: "
                + activity.getVeterinarian()
                .getFullName()
                + ")";
    }

    public void mostraSuccesso(String messaggio) {
        CLIRenderer.successo(messaggio);
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }

    public boolean chiediConferma(String prompt) {
        return CLIRenderer.chiediConferma(prompt);
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
}
