package com.kolmakova.task3.entity;

import com.kolmakova.task3.util.NumberSequenceGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class Ship extends Thread {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final int CONTAINER_GROUP_SIZE = 30;
    private ShipStatus status;
    private final Port port;
    private final long number;
    private final int capacity;
    private int containerQuantity;

    {
        number = NumberSequenceGenerator.generateShipNumber();
        status = ShipStatus.CREATED;
        port = Port.getInstance();
    }

    public Ship(int capacity, int containerQuantity) {
        this.capacity = capacity;
        this.containerQuantity = containerQuantity;
    }

    public long getNumber() {
        return number;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getContainerQuantity() {
        return containerQuantity;
    }

    public void setStatus(ShipStatus shipStatus) {
        this.status = shipStatus;
    }

    @Override
    public void run() {
        Dock dock = null;

        try {
            dock = port.getFreePier(this);
            defineShipProcess();
        } catch (InterruptedException exception) {
            LOGGER.warn("Ship # {} is interrupted! {}", number , this, exception);
        } finally {
            if (dock != null) {
                port.releaseDock(dock);
            }
        }
    }

    private void defineShipProcess() throws InterruptedException {
        status = ShipStatus.PROCESSING;
        boolean hasContainers = containerQuantity > 0;

        if (hasContainers) {
            LOGGER.info("Ship # {} start UNLOADING containers", number);
            unloadContainers();
        } else {
            LOGGER.info("Ship # {} start LOADING containers", number);
            loadContainers();
        }

        status = ShipStatus.COMPLETED;
        LOGGER.info("Ship {}", this);
    }

    private void unloadContainers() throws InterruptedException {
        while (containerQuantity != 0) {
            int containerQuantityToUnload = Math.min(containerQuantity, CONTAINER_GROUP_SIZE);
            containerQuantity -= containerQuantityToUnload;

            port.getContainersInStock().getAndAdd(containerQuantityToUnload);
            port.getReservationUnloadQty().getAndAdd(-containerQuantityToUnload);

            TimeUnit.MILLISECONDS.sleep(100);
            LOGGER.info("Ship {}, unloaded {} containers", this, containerQuantityToUnload);
        }
    }

    private void loadContainers() throws InterruptedException {
        while (containerQuantity != capacity) {
            int missingContainerQuantity = capacity - containerQuantity;
            int containerQuantityToLoad = Math.min(missingContainerQuantity, CONTAINER_GROUP_SIZE);
            containerQuantity += containerQuantityToLoad;

            port.getContainersInStock().getAndAdd(-containerQuantityToLoad);
            port.getReservationLoadQty().getAndAdd(-containerQuantityToLoad);

            TimeUnit.MILLISECONDS.sleep(100);
            LOGGER.info("Ship {} LOADED {} containers", this, containerQuantityToLoad);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ship ship = (Ship) o;

        if (number != ship.number) return false;
        if (capacity != ship.capacity) return false;
        if (containerQuantity != ship.containerQuantity) return false;
        if (status != ship.status) return false;
        return port.equals(ship.port);
    }

    @Override
    public int hashCode() {
        int result = status.hashCode();
        result = 31 * result + port.hashCode();
        result = 31 * result + (int) (number ^ (number >>> 32));
        result = 31 * result + capacity;
        result = 31 * result + containerQuantity;
        return result;
    }

    @Override
    public String toString() {
        return "{ number = " + number +
                " , capacity = " + capacity +
                " , container quantity = " + containerQuantity +
                " } is " + status;
    }
}
