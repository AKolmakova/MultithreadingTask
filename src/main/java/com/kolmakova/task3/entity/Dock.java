package com.kolmakova.task3.entity;

import com.kolmakova.task3.util.NumberSequenceGenerator;

public class Dock {

    private final long dockNumber;

    public Dock() {
        dockNumber = NumberSequenceGenerator.generateDockNumber();
    }

    public long getDockNumber() {
        return dockNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dock dock = (Dock) o;

        return dockNumber == dock.dockNumber;
    }

    @Override
    public int hashCode() {
        return (int) (dockNumber ^ (dockNumber >>> 32));
    }

    @Override
    public String toString() {
        return "{ dock # " + dockNumber + " }";
    }
}
