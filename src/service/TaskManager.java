package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int taskId);

    int addTask(Task task);

    void updateTask(Task task);

    void deleteTaskById(int taskId);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int epicId);

    int addEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int epicId);

    List<SubTask> getAllEpicSubTasks(Epic epicId);

    List<SubTask> getAllSubTasks();

    void deleteAllSubTasks();

    SubTask getSubTaskById(int subTaskId);

    int addSubTask(SubTask subTask);

    void updateSubTask(SubTask updSubTask);

    void deleteSubTaskById(int subTaskId);

    List<Task> getHistory();
}