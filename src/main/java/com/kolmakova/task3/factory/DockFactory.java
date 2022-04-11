package com.kolmakova.task3.factory;

import com.kolmakova.task3.entity.Dock;

import java.util.Queue;

public interface DockFactory {
    Queue<Dock> createDocks(int dockQuantity);
}
