package it.ispwproject.myvet.view.cli;

public class DashboardPetOwnerCLIView {

    public void mostraBenvenuto(String nome) {
        CLIRenderer.intestazioneBenvenuto(
                nome,
                "Pet Owner"
        );
    }

    public void mostraMenu() {
        CLIRenderer.vuota();

        CLIRenderer.voceMenu(
                1,
                "Prenota un appuntamento"
        );

        CLIRenderer.voceMenu(
                2,
                "I miei appuntamenti"
        );

        CLIRenderer.voceMenu(
                3,
                "Annulla un appuntamento"
        );

        CLIRenderer.voceMenu(
                4,
                "Attività di cura"
        );

        CLIRenderer.voceMenu(
                5,
                "Documenti medici"
        );

        CLIRenderer.voceMenu(
                6,
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