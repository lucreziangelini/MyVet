package it.ispwproject.myvet.pattern.state;

/**
 * Context interface del pattern GoF State per la navigazione CLI.
 */

public interface CLIStateMachine {

    void start();

    void goNext();

    void goBack();

    void transition(AbstractCLIState nextState);

    /**
     * Cancella la cronologia e ripristina la navigazione
     * a partire dallo stato indicato.
     */
    void reset(AbstractCLIState initialState);

    AbstractCLIState getState();

    void setState(AbstractCLIState state);
}
