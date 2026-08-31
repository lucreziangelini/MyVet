package it.ispwproject.myvet.view.cli;

public class DashboardPetOwnerCLIView {

    public void mostraBenvenuto(String nome, String saluto) {
        CLIRenderer.intestazioneBenvenuto(
                nome,
                "Proprietario",
                saluto
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
                "I miei animali"
        );

        CLIRenderer.voceMenu(
                5,
                "Attività di cura"
        );

        CLIRenderer.voceMenu(
                6,
                "Documenti medici"
        );

        CLIRenderer.voceMenu(
                7,
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
