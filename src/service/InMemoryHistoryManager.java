package service;

import model.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> historyTask;

    public InMemoryHistoryManager() {
    }

    @Override
    public void addTask(Task task) {
    }

    @Override
    public List<Task> getHistory() {
        return historyTask;
    }

    @Override
    public void remove(int id) {
    }
}
