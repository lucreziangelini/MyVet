package it.ispwproject.myvet.view.cli;

public class ModeSelectorCLIView {

    public void mostraMenu() {
        CLIRenderer.intestazione(
                "MyVet – Seleziona modalità di avvio"
        );

        CLIRenderer.vuota();

        CLIRenderer.voceMenu(
                1,
                "Dimostrativa (in memoria, nessun database richiesto)"
        );

        CLIRenderer.voceMenu(
                2,
                "Database     (persistenza MySQL)"
        );

        CLIRenderer.voceMenu(
                3,
                "Archivio     (prenotazioni e fasce orarie in JSON)"
        );

        CLIRenderer.voceMenuZero("Esci");

        CLIRenderer.vuota();
        CLIRenderer.separatore();
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa(
                "Scelta"
        );
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraModalitaSelezionata(
            String modalita) {

        CLIRenderer.successo(
                "Modalità selezionata: "
                        + modalita
        );
    }
}
