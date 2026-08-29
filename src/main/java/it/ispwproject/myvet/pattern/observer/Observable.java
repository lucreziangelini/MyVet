package it.ispwproject.myvet.pattern.observer;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable {

    private final List<Observer> observers = new ArrayList<>();

    public void attach(Observer observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Observer cannot be null");
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void detach(Observer observer) {
        observers.remove(observer);
    }

    protected void notifyObservers() {
        for (Observer observer : List.copyOf(observers)) {
            observer.update();
        }
    }
}
