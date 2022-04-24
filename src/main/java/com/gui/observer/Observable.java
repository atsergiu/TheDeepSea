package com.gui.observer;


import com.gui.events.EventInterface;

public interface Observable<E extends EventInterface> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
