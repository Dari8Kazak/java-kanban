package service;

import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;

public class Managers {

    public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}