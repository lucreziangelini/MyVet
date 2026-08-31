package it.ispwproject.myvet.view.cli;

public class DashboardVeterinarianCLIView {

    public void mostraBenvenuto(String nome, String saluto) {
        CLIRenderer.intestazioneBenvenuto(
                nome,
                "Veterinario",
                saluto
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
                "Le mie fasce orarie"
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

        CLIRenderer.voceMenuZero("Esci");
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
