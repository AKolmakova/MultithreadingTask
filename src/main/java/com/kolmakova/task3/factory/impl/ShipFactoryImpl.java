package com.kolmakova.task3.factory.impl;

import com.kolmakova.task3.entity.Ship;
import com.kolmakova.task3.factory.ShipFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ShipFactoryImpl implements ShipFactory {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int MIN_CONTAINERS_QUANTITY = 0;
    private static final int MIN_SHIP_CAPACITY = 15;
    private static final int MAX_SHIP_CAPACITY = 500;

    @Override
    public Ship createShip() {
        int shipCapacity = getRandomNumberFromRange(MIN_SHIP_CAPACITY, MAX_SHIP_CAPACITY);
        int containerQuantity = getRandomNumberFromRange(MIN_CONTAINERS_QUANTITY, shipCapacity);

        return new Ship(shipCapacity, containerQuantity);
    }

    @Override
    public List<Ship> createShipList(int shipQuantity) {
        List<Ship> ships = new ArrayList<>();

        for (int i = 0; i < shipQuantity; i++) {
            Ship currentShip = createShip();
            ships.add(currentShip);
            LOGGER.info("Ship {}", currentShip);
        }

        return ships;
    }

    private int getRandomNumberFromRange(int min, int max) {
        return min + (int) (Math.random() * max);
    }
}
