package com.gui.observer;


import com.gui.events.EventInterface;

public interface Observer<E extends EventInterface> {
    void update(E e);
}