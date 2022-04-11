package com.kolmakova.task3.factory;

import com.kolmakova.task3.entity.Ship;

import java.util.List;

public interface ShipFactory {

    Ship createShip();

    List<Ship> createShipList(int shipQuantity);
}
