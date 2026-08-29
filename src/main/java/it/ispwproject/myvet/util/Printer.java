package it.ispwproject.myvet.util;

@SuppressWarnings("java:S106")
public final class Printer {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_GREEN = "\u001B[32m";

    private Printer() {
        // Prevents instantiation
    }

    // Stampa un messaggio senza andare a capo
    public static void print(String message) {
        System.out.print(message);
    }

    // Stampa un messaggio andando a capo
    public static void println(String message) {
        System.out.println(message);
    }

    // Stampa messaggi guida e titoli
    public static void printBlue(String message) {
        System.out.print(
                ANSI_BLUE + message + ANSI_RESET
        );
    }

    // Stampa messaggi guida e titoli andando a capo
    public static void printlnBlue(String message) {
        System.out.println(
                ANSI_BLUE + message + ANSI_RESET
        );
    }

    // Stampa messaggi di errore
    public static void printError(String message) {
        System.out.println(
                ANSI_RED + "❌ " + message + ANSI_RESET
        );
    }

    // Stampa messaggi di successo
    public static void printSuccess(String message) {
        System.out.println(
                ANSI_GREEN + "✅ " + message + ANSI_RESET
        );
    }
}