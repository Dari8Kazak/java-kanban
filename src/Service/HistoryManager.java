package Service;

import Model.Epic;
import Model.SubTask;
import Model.Task;

import java.util.List;

public interface HistoryManager {

    void addTask(Task task);
    void addEpicTask(Epic epic);
    void addSubTask(SubTask subTask);

     List<Task> getHistory();



}