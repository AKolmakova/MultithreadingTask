package com.kolmakova.task3.factory.impl;

import com.kolmakova.task3.entity.Dock;
import com.kolmakova.task3.factory.DockFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.Queue;

public class DockFactoryImpl implements DockFactory {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public Queue<Dock> createDocks(int dockQuantity) {
        Queue<Dock> availableDocks = new LinkedList<>();

        for (int i = 0; i < dockQuantity; i++) {
            Dock dock = new Dock();
            availableDocks.add(dock);
            LOGGER.info("Created dock {}", dock);
        }

        return availableDocks;
    }

}
