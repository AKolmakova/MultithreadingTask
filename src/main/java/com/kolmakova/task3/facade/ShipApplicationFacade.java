package com.kolmakova.task3.facade;

import com.kolmakova.task3.config.ApplicationConfig;
import com.kolmakova.task3.config.Properties;
import com.kolmakova.task3.entity.Ship;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class ShipApplicationFacade {

    public static void run() {
        ApplicationConfig applicationConfig = ApplicationConfig.getInstance();
        Properties properties = Properties.getInstance();
        ExecutorService executorService = applicationConfig.getExecutorService();

        List<Ship> ships = applicationConfig
                .getShipFactory()
                .createShipList(properties.getShipQuantity());

        ships.forEach(executorService::execute);
        executorService.shutdown();
    }
}
