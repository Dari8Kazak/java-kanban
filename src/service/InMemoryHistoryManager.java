package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyTask;
    private static final int MAX_HISTORY_VALUE = 10;

    public InMemoryHistoryManager() {
        historyTask = new ArrayList<>();
    }

    public void add(Task task) {
        if (task != null) {
            historyTask.add(task);
        }

        if (historyTask.size() > MAX_HISTORY_VALUE) {
            historyTask.removeFirst();
        }
    }

    public List<Task> getHistory() {
        return new ArrayList<>(historyTask);
    }
}