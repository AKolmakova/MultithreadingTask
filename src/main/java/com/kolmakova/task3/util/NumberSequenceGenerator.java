package com.kolmakova.task3.util;

public class NumberSequenceGenerator {

    public static long currentDockNumber = 1;
    public static long currentShipNumber = 1;

    public static long generateDockNumber() {
        return currentDockNumber++;
    }

    public static long generateShipNumber() {
        return currentShipNumber++;
    }
}
