package Service;

import Model.Epic;
import Model.SubTask;
import Model.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Task> tasks;
    private final List<Task> historyTask;
    private Task previousTask;

    public InMemoryHistoryManager() {
        tasks = new LinkedHashMap<>();
        historyTask = new ArrayList<>();
    }

    public void addTask(Task task) {
        if (task != null) {
            historyTask.add(task);
        }

        if (historyTask.size() > 10) historyTask.removeFirst();
    }

    public Task getPreviousTask() {
        return previousTask;
    }
    public void addEpicTask(Epic epic) {
        tasks.put(epic.getId(), epic);
        addToHistory(epic);
    }

    public void addSubTask(SubTask subTask) {
        tasks.put(subTask.getId(), subTask);
        addToHistory(subTask);
    }

    private void addToHistory(Task task) {
       historyTask.remove(task);
       historyTask.add(task);

        if (historyTask.size() > 10) {
            historyTask.removeFirst();
        }
    }

    public List<Task> getHistory() {
        return new ArrayList<>(historyTask);
    }

}



