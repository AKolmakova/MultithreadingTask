package com.kolmakova.task3.config;

import com.kolmakova.task3.exception.CustomThreadException;
import com.kolmakova.task3.util.ResourcePathUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;

public class Properties {

    private static final Logger LOGGER = LogManager.getLogger();
    private static Properties instance;

    private static final String PROPERTIES_FILE_NAME = "port.properties";
    private static final String DOCK_QUANTITY = "dockQuantity";
    private static final String STOCK_CAPACITY = "stockCapacity";
    private static final String STOCK_CONTAINER_QUANTITY = "stockContainerQuantity";
    private static final String SHIP_QUANTITY = "shipQuantity";
    private static final String THREAD_POOL_SIZE = "threadPoolSize";


    private int stockCapacity;
    private int stockContainerQuantity;
    private int dockQuantity;
    private int shipQuantity;
    private int threadPoolSize;

    public static Properties getInstance() {
        if (instance == null) {
            instance = new Properties();
        }

        return instance;
    }

    private Properties() {
        try {
            loadFromFile();
            LOGGER.info("Properties are LOADED. Info : {}", this);
        } catch (IOException | CustomThreadException exception) {
            LOGGER.warn("Properties NOT LOADED!", exception);
        }
    }

    public int getStockCapacity() {
        return stockCapacity;
    }

    public int getStockContainerQuantity() {
        return stockContainerQuantity;
    }

    public int getDockQuantity() {
        return dockQuantity;
    }

    public int getShipQuantity() {
        return shipQuantity;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    private void loadFromFile() throws CustomThreadException, IOException {
        String filePath = ResourcePathUtil.getResourcePath(PROPERTIES_FILE_NAME);

        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            java.util.Properties properties = new java.util.Properties();
            properties.load(fileInputStream);

            dockQuantity = Integer.parseInt(properties.getProperty(DOCK_QUANTITY));
            stockCapacity = Integer.parseInt(properties.getProperty(STOCK_CAPACITY));
            stockContainerQuantity = Integer.parseInt(properties.getProperty(STOCK_CONTAINER_QUANTITY));
            shipQuantity = Integer.parseInt(properties.getProperty(SHIP_QUANTITY));
            threadPoolSize = Integer.parseInt(properties.getProperty(THREAD_POOL_SIZE));
        }
    }

    @Override
    public String toString() {
        return "{ stock capacity = " + stockCapacity +
                ", stock container quantity = " + stockContainerQuantity +
                ", dock quantity = " + dockQuantity +
                ", ship quantity = " + shipQuantity +
                ", threadPool size = " + threadPoolSize + " }";
    }
}
