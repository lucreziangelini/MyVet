package it.ispwproject.myvet.view.cli;

public class DashboardVeterinarianCLIView {

    public void mostraBenvenuto(String nome) {
        CLIRenderer.intestazioneBenvenuto(
                nome,
                "Veterinario"
        );
    }

    public void mostraMenu() {
        CLIRenderer.vuota();

        CLIRenderer.voceMenu(
                1,
                "Aggiungi disponibilità"
        );

        CLIRenderer.voceMenu(
                2,
                "I miei slot"
        );

        CLIRenderer.voceMenu(
                3,
                "Gestisci la cura degli animali"
        );

        CLIRenderer.voceMenu(
                4,
                "Documenti medici"
        );

        CLIRenderer.voceMenu(
                5,
                "Profilo"
        );

        CLIRenderer.voceMenuZero("Logout");
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa(
                "Scelta"
        );
    }

    public void mostraMessaggio(String messaggio) {
        CLIRenderer.messaggio(messaggio);
    }
}