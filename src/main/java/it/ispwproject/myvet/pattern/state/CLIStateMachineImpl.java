package it.ispwproject.myvet.pattern.state;

import it.ispwproject.myvet.controller.cli.InitialCLI;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Context del pattern GoF State.
 * Mantiene lo stato corrente e la cronologia degli stati precedenti.
 */
public class CLIStateMachineImpl implements CLIStateMachine {

    private AbstractCLIState currentState;

    private final Deque<AbstractCLIState> stateHistory =
            new ArrayDeque<>();

    public CLIStateMachineImpl() {
        this.currentState = new InitialCLI();
    }

    @Override
    public void start() {
        stateHistory.clear();
        currentState = new InitialCLI();
        currentState.entry(this);
        goNext();
    }

    @Override
    public void goNext() {
        if (currentState != null) {
            currentState.action(this);
        }
    }

    @Override
    public void goBack() {
        if (stateHistory.isEmpty()) {
            return;
        }

        currentState.exit(this);
        currentState = stateHistory.pop();
        currentState.entry(this);
        goNext();
    }

    @Override
    public void transition(AbstractCLIState nextState) {
        Objects.requireNonNull(
                nextState,
                "Next state cannot be null"
        );

        if (currentState != null) {
            currentState.exit(this);
            stateHistory.push(currentState);
        }

        currentState = nextState;
        currentState.entry(this);
        goNext();
    }

    @Override
    public void reset(AbstractCLIState initialState) {
        Objects.requireNonNull(
                initialState,
                "Initial state cannot be null"
        );

        if (currentState != null) {
            currentState.exit(this);
        }

        stateHistory.clear();
        currentState = initialState;
        currentState.entry(this);
        goNext();
    }

    @Override
    public AbstractCLIState getState() {
        return currentState;
    }

    @Override
    public void setState(AbstractCLIState state) {
        currentState = Objects.requireNonNull(
                state,
                "State cannot be null"
        );
    }
}
