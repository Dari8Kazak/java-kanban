package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);

    Task updateTask(Task task);

    Epic updateEpic(Epic epic);

    SubTask updateSubTask(SubTask updSubTask);

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    Task deleteTaskById(Integer taskId);

    Epic deleteEpicById(Integer epicId);

    SubTask deleteSubTaskById(Integer subTaskId);

    Task getTaskById(Integer taskId);

    Epic getEpicById(Integer epicId);

    SubTask getSubTaskById(Integer subTaskId);

    List<Task> getHistory();

    List<Task> getAllTasks();

    List<Epic> getAllEpics();

    List<SubTask> getAllSubTasks();

    List<SubTask> getAllEpicSubTasks(Epic epicId);

    boolean isSubtaskTimeIntersect(SubTask newSubtask);

    boolean isTaskTimeIntersect(Task newTask);

    HistoryManager getHistoryManager();

    void save();

    TreeSet<Task> getPrioritizedTasks();
}