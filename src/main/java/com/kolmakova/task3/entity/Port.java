package com.kolmakova.task3.entity;

import com.kolmakova.task3.config.ApplicationConfig;
import com.kolmakova.task3.config.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Port {

    private static final Logger LOGGER = LogManager.getLogger();

    private static Port instance;

    private final ReentrantLock lock;

    private final Condition waitForReleaseDockCondition;
    private final Queue<Dock> availableDocks;
    private final Queue<Dock> occupiedDocks;
    private final AtomicInteger containersInStock;
    private final AtomicInteger reservationLoadQty;
    private final AtomicInteger reservationUnloadQty;
    private final int stockCapacity;

    public static Port getInstance() {
        if (instance == null) {
            instance = new Port();
        }

        return instance;
    }

    private Port() {
        ApplicationConfig applicationConfig = ApplicationConfig.getInstance();
        Properties portProperties = Properties.getInstance();

        int pierQuantity = portProperties.getDockQuantity();
        stockCapacity = portProperties.getStockCapacity();

        containersInStock = new AtomicInteger(portProperties.getStockContainerQuantity());
        occupiedDocks = new ArrayDeque<>();
        reservationLoadQty = new AtomicInteger();
        reservationUnloadQty = new AtomicInteger();

        lock = new ReentrantLock(true);
        waitForReleaseDockCondition = lock.newCondition();

        availableDocks = applicationConfig
                .getDockFactory()
                .createDocks(pierQuantity);

        LOGGER.info("Port CREATED {}", this);
    }

    public Dock getFreePier(Ship ship) throws InterruptedException {
        Dock freeDock;
        ship.setStatus(ShipStatus.WAITING);
        LOGGER.info("Ship {} is WAITING", ship.getNumber());
        try {
            lock.lock();
            while (canNotServiceShip(ship)) {
                LOGGER.info("Ship # {} CONTINUE WAITING ...", ship.getNumber());
                waitForReleaseDockCondition.await();
            }
            freeDock = availableDocks.poll();
            occupiedDocks.add(freeDock);
        } finally {
            lock.unlock();
        }

        assert freeDock != null;
        LOGGER.info("Ship # {} MOORED at the dock number {} ", ship.getNumber(), freeDock.getDockNumber());
        return freeDock;
    }

    public void releaseDock(Dock dock) {
        try {
            lock.lock();
            occupiedDocks.remove(dock);
            availableDocks.add(dock);
            LOGGER.info("Dock {} is FREE", dock.getDockNumber());
        } finally {
            waitForReleaseDockCondition.signalAll();
            lock.unlock();
        }
    }

    public AtomicInteger getContainersInStock() {
        return containersInStock;
    }

    public AtomicInteger getReservationLoadQty() {
        return reservationLoadQty;
    }

    public AtomicInteger getReservationUnloadQty() {
        return reservationUnloadQty;
    }

    private boolean canNotServiceShip(Ship ship) {
        return availableDocks.isEmpty() || canNotLoadOrUnloadShip(ship);
    }

    private int getNotReservedContainersInStock() {
        return containersInStock.get() - reservationLoadQty.get();
    }

    private int getFreeStockPlaces() {
        return stockCapacity - containersInStock.get() + reservationUnloadQty.get();
    }

    private boolean canNotLoadOrUnloadShip(Ship ship) {
        if (ship.getContainerQuantity() > 0) {
            return shipCanUnload(ship);
        } else {
            return shipCanLoad(ship);
        }
    }

    private boolean shipCanNotLoad(Ship ship) {
        int shipCapacity = ship.getCapacity();
        int availableContainersToLoad = getNotReservedContainersInStock();

        if (shipCapacity <= availableContainersToLoad) {
            reservationLoadQty.getAndAdd(shipCapacity);
            LOGGER.info("Need to LOAD {} containers to the ship. ", shipCapacity);
            return true;
        }
        return false;
    }

    private boolean shipCanLoad(Ship ship) {
        return !shipCanNotLoad(ship);
    }

    private boolean shipCanUnload(Ship ship) {
        return !shipCanNotUnload(ship);
    }

    private boolean shipCanNotUnload(Ship ship) {
        int availableStock = getFreeStockPlaces();
        int shipContainerQuantity = ship.getContainerQuantity();

        if (shipContainerQuantity <= availableStock) {
            reservationUnloadQty.getAndAdd(shipContainerQuantity);
            LOGGER.info("Ship # {} need to RESERVE {} places for containers in stock", ship.getNumber(), shipContainerQuantity);
            return true;
        }

        return false;
    }

    private int getReservationUnLoadQty(int shipContainerQuantity) {
        return reservationUnloadQty.getAndAdd(shipContainerQuantity);
    }

    private int getLoadQty(int shipCapacity) {
        return reservationUnloadQty.getAndAdd(shipCapacity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Port port = (Port) o;

        if (stockCapacity != port.stockCapacity) return false;
        if (!lock.equals(port.lock)) return false;
        if (!waitForReleaseDockCondition.equals(port.waitForReleaseDockCondition)) return false;
        if (!availableDocks.equals(port.availableDocks)) return false;
        if (!occupiedDocks.equals(port.occupiedDocks)) return false;
        if (!containersInStock.equals(port.containersInStock)) return false;
        if (!reservationLoadQty.equals(port.reservationLoadQty)) return false;
        return reservationUnloadQty.equals(port.reservationUnloadQty);
    }

    @Override
    public int hashCode() {
        int result = lock.hashCode();
        result = 31 * result + waitForReleaseDockCondition.hashCode();
        result = 31 * result + availableDocks.hashCode();
        result = 31 * result + occupiedDocks.hashCode();
        result = 31 * result + containersInStock.hashCode();
        result = 31 * result + reservationLoadQty.hashCode();
        result = 31 * result + reservationUnloadQty.hashCode();
        result = 31 * result + stockCapacity;
        return result;
    }

    @Override
    public String toString() {
        return "{ stockCapacity = " + stockCapacity +
                ", available docks = " + availableDocks +
                ", occupied piers = " + occupiedDocks +
                ", stock container quantity = " + containersInStock +
                " }";
    }
}
