package it.ispwproject.myvet.view.cli;

@SuppressWarnings("java:S106") // System.out e' l'output previsto di questa boundary CLI.
public class InitialCLIView {

    public void mostraBenvenuto() {
        CLIRenderer.vuota();

        System.out.println(
                CLIRenderer.LINE_DECO
        );

        System.out.println(
                CLIRenderer.centra(
                        "M Y V E T"
                )
        );

        System.out.println(
                CLIRenderer.centra(
                        "La salute del tuo animale, sempre con te!"
                )
        );

        System.out.println(
                CLIRenderer.LINE_DECO
        );
    }

    public void mostraMenu() {
        CLIRenderer.vuota();

        CLIRenderer.voceMenu(
                1,
                "Accedi"
        );

        CLIRenderer.voceMenu(
                2,
                "Registrati"
        );

        CLIRenderer.voceMenuZero("Esci");
    }

    public String chiediScelta() {
        return CLIRenderer.chiediSceltaStringa(
                "Scelta"
        );
    }

    public void mostraErrore(String messaggio) {
        CLIRenderer.errore(messaggio);
    }

    public void mostraArrivederci() {
        CLIRenderer.vuota();

        System.out.println(
                CLIRenderer.LINE_DECO
        );

        System.out.println(
                CLIRenderer.centra(
                        "Arrivederci! – MyVet"
                )
        );

        System.out.println(
                CLIRenderer.LINE_DECO
        );

        CLIRenderer.vuota();
    }
}
