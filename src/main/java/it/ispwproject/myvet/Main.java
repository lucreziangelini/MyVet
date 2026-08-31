package it.ispwproject.myvet;

import it.ispwproject.myvet.controller.cli.MainCLI;
import it.ispwproject.myvet.controller.cli.ModeSelectorCLI;
import it.ispwproject.myvet.controller.gui.MainGUI;
import it.ispwproject.myvet.util.Printer;
import it.ispwproject.myvet.view.cli.CLIRenderer;

public class Main {

    public static void main(String[] args) {

        // ── Step 1 — selezione modalità persistenza (sempre CLI) ─────
        ModeSelectorCLI modeSelector =
                new ModeSelectorCLI();

        boolean proceed =
                modeSelector.start();

        if (!proceed) {
            return;
        }

        // ── Step 2 — selezione interfaccia ───────────────────────────
        String scelta = "";

        while (!scelta.equals("1")
                && !scelta.equals("2")) {

            Printer.println(
                    "\n  ── Seleziona interfaccia"
            );

            Printer.println(
                    "  [1] CLI  — interfaccia testuale"
            );

            Printer.println(
                    "  [2] GUI  — interfaccia grafica"
            );

            Printer.print("\n  Scelta: ");

            scelta =
                    CLIRenderer.SCANNER.nextLine().trim();

            if (!scelta.equals("1")
                    && !scelta.equals("2")) {

                Printer.printError(
                        "Scelta non valida."
                );
            }
        }

        if (scelta.equals("2")) {
            MainGUI.launch(args);
        } else {
            MainCLI.start();
        }
    }
}
