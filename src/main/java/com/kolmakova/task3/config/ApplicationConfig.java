package com.kolmakova.task3.config;

import com.kolmakova.task3.factory.DockFactory;
import com.kolmakova.task3.factory.ShipFactory;
import com.kolmakova.task3.factory.impl.DockFactoryImpl;
import com.kolmakova.task3.factory.impl.ShipFactoryImpl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationConfig {

    private static Properties properties;
    private static ApplicationConfig instance;

    public static ApplicationConfig getInstance() {
        if (instance == null) {
            instance = new ApplicationConfig();
        }

        return instance;
    }

    private ApplicationConfig() {
        properties = Properties.getInstance();
    }

    public DockFactory getDockFactory() {
        return new DockFactoryImpl();
    }

    public ShipFactory getShipFactory() {
        return new ShipFactoryImpl();
    }

    public ExecutorService getExecutorService() {
        int poolSize = properties.getThreadPoolSize();

        return Executors.newFixedThreadPool(poolSize);
    }

}
