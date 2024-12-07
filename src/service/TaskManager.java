package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getAllTasks();

    int createTask(Task task);

    int createEpic(Epic epic);

    boolean createSubTask(SubTask subTask);

    boolean isSubtaskTimeIntersect(SubTask newSubtask);

    boolean isTaskTimeIntersect(Task newTask);

    boolean isTimeOverlap(Task task1, Task task2);

    void deleteAllTasks();

    Task getTaskById(int taskId);

    void updateTask(Task task);

    void deleteTaskById(int taskId);

    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int epicId);

    void updateEpic(Epic epic);

    void deleteEpicById(int epicId);

    List<SubTask> getAllEpicSubTasks(Epic epicId);

    List<SubTask> getAllSubTasks();

    void deleteAllSubTasks();

    SubTask getSubTaskById(int subTaskId);

    void updateSubTask(SubTask updSubTask);

    void deleteSubTaskById(int subTaskId);

    List<Task> getHistory();
}