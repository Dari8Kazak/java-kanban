package service;

import model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    void add(Task task);

    void remove(Integer id);

    ArrayList<Task> getHistory();

}