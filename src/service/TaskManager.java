package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    void removeAllTasks();

    Task getTaskById(int taskId);

    int addTask(Task task);

    void updateTask(Task task);

    void removeTaskById(int taskId);

    List<Epic> getAllEpics();

    void removeAllEpics();

    Epic getEpicById(int epicId);

    int addEpic(Epic epic);

    void updateEpic(Epic epic);

    void removeEpicById(int epicId);

    List<SubTask> getAllEpicSubTasks(Epic epicId);

    List<SubTask> getAllSubTasks();

    void removeAllSubTasks();

    SubTask getSubTaskById(int subTaskId);

    void addSubTask(SubTask subTask);

    void updateSubTask(SubTask updSubTask);

    void removeSubTaskById(int subTaskId);

    List<Task> getHistory();
}