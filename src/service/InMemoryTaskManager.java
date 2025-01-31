package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {
    private static final HashMap<Integer, Task> tasks = new HashMap<>();
    private static final HashMap<Integer, Epic> epics = new HashMap<>();
    private static final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int id;

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        if (task1.getStartTime() == null && task2.getStartTime() == null) {
            return Integer.compare(task1.getId(), task2.getId()); // Сравниваем по ID, если у обеих задач нет startTime
        }
        if (task1.getStartTime() == null) {
            return 1; // Если у первой задачи нет startTime, она будет ниже в порядке.
        }
        if (task2.getStartTime() == null) {
            return -1; // Если у второй задачи нет startTime, она будет ниже в порядке.
        }
        return task1.getStartTime().compareTo(task2.getStartTime());
    });

    public static HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public static HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public static HashMap<Integer, Epic> getEpicTasks() {
        return epics;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public int createTask(Task task) {
        if (task != null) {
            // Проверка на пересечение по времени
            for (Task existingTask : prioritizedTasks) {
                if (isOverlapping(existingTask, task)) {
                    System.out.println("Задача не добавлена, так как время пересекается с существующей задачей.");
                    return -1; // Возвращаем -1, если есть пересечение
                }
            }

            task.setId(generateNewId());
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        } else {
            System.out.println("Задача не добавлена, так как она равна null.");
        }
        return 0;
    }

    @Override
    public int createEpic(Epic epic) {
        if (epic != null) {
            epic.setId(generateNewId());
            epics.put(epic.getId(), epic);
            prioritizedTasks.add(epic);
        } else {
            System.out.println("Эпик не добавлен");
            return -1;
        }
        return 0;
    }

    @Override
    public int createSubTask(SubTask subTask) {


//            if (subTask == null || (subTask.getStartTime() != null && isCrossedTask(subtask))) {
//                return null;
//            }
//            subtask.setId(generateNewId());
//            subtasks.put(subtask.getId(), subtask);
//            Epic epic = epics.get(subtask.getEpicId());
//            epic.addSubtaskId(subtask.getId());
//            updateEpicState(epic.getId());
//            prioritizedTasks.add(subtask);
//            return subtask.getId();
//        }
        // if (!isTaskTimeIntersect(subTask)) {
        if (subTask == null || !epics.containsKey(subTask.getEpicId())) {
            return 0;
        }
        // Проверка на пересечение по времени
        for (Task existingTask : prioritizedTasks) {
            if (isOverlapping(existingTask, subTask)) {
                System.out.println("Задача не добавлена, так как время пересекается с существующей задачей.");
                return -1; // Возвращаем -1, если есть пересечение
            }
        }

        Epic epic = epics.get(subTask.getEpicId());
        epic.getSubtasks().add(subTask);
        subTasks.put(subTask.getId(), subTask);
        epic.updateStatus();
        epics.put(epic.getId(), epic);
        // epic.createSubTaskId(subTask);
        prioritizedTasks.add(subTask);
        return 1;
    }

    // Метод для проверки пересечения временных интервалов
    private boolean isOverlapping(Task existingTask, Task newTask) {
        return (newTask.getStartTime().isBefore(existingTask.getEndTime()) &&
                newTask.getEndTime().isAfter(existingTask.getStartTime()));
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            Epic updEpic = epics.get(epic.getId());
            updEpic.setName(epic.getName());
            updEpic.setDescription((epic.getDescription()));
        } else {
            System.out.println("Эпика с таким id нет.");
        }
    }

    @Override
    public void updateSubTask(SubTask updSubTask) {
        if (updSubTask != null && subTasks.containsKey(updSubTask.getId())) {
            SubTask subTask = subTasks.get(updSubTask.getId());
            if (subTask.getEpicId() == updSubTask.getEpicId()) {
                prioritizedTasks.remove(subTask);
                subTasks.put(updSubTask.getId(), updSubTask);
                Epic epic = epics.get(updSubTask.getEpicId());
                updateEpicStatus(epic);
                prioritizedTasks.add(updSubTask);
            }
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId()) && task.getTaskType() == TaskType.TASK) {
            // Проверка на пересечение по времени с другими задачами
            for (Task existingTask : prioritizedTasks) {
                if (!existingTask.equals(task) && isOverlapping(existingTask, task)) {
                    System.out.println("Обновление задачи невозможно, так как время пересекается с существующей задачей.");
                    return; // Завершаем метод, если есть пересечение
                }
            }
            prioritizedTasks.remove(task); // Удаляем старую задачу
            prioritizedTasks.add(task); // Добавляем обновлённую задачу
            tasks.put(task.getId(), task);
        } else {
            System.out.println("задачи с таким id нет.");
        }
    }

    private void updateEpicStatus(Epic epic) {
        if (epic == null) {
            System.out.println("Статус эпика не обновлен");
            return;
        }
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        int isNew = 0;
        int isDone = 0;

        ArrayList<SubTask> subTasksId = epic.getSubtasks();
        for (SubTask subTask : subTasksId) {
            if (subTask.getStatus() == TaskStatus.NEW) {
                isNew++;
            }
//            if (subTasks.get(subTask).getStatus() == TaskStatus.DONE) {
            if (subTask.getStatus() == TaskStatus.DONE) {
                isDone++;
            }
        }

        if (isNew == subTasksId.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isDone == subTasksId.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public void deleteEpicById(int epicId) {
        Epic epic = epics.remove(epicId);
        prioritizedTasks.remove(epic);
        historyManager.remove(epicId);
        for (SubTask subtaskId : epic.getSubtasks()) {
            subTasks.remove(subtaskId.getId());
            prioritizedTasks.remove(subtaskId);
        }
        epic.getSubtasks().clear();
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        SubTask existSubtask = subTasks.get(subTaskId);
        SubTask subtask = subTasks.remove(subTaskId);
        historyManager.remove(subTaskId);
        prioritizedTasks.remove(existSubtask);
        if (subtask == null) {
            return;
        }
        Epic epic = epics.get(subtask.getEpicId());
        epic.getSubtasks().remove(subtask);
        updateEpicStatus(epic);
    }

    @Override
    public void deleteTaskById(int taskId) {
        prioritizedTasks.remove(tasks.get(taskId));
        historyManager.remove(taskId);
        tasks.remove(taskId);
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        }
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            prioritizedTasks.remove(epic);
        }
        deleteAllSubTasks();
        epics.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask);
        }
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.setStatus(TaskStatus.NEW);
        }
        subTasks.clear();
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getAllEpicSubTasks(Epic epicId) {
        if (epicId == null) {
            System.out.println("подзадачи не получены");
            return null;
        }

        return epicId.getSubtasks();
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public boolean isSubtaskTimeIntersect(SubTask newSubtask) {
        return isTaskOverlap(newSubtask);
    }

    @Override
    public boolean isTaskTimeIntersect(Task newTask) {
        return isTaskOverlap(newTask);
    }

    @Override
    public void save() {
    }

    public boolean isTaskOverlap(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null)
            return false;

        for (Task existingTask : getPrioritizedTasks()) {
            if ((existingTask.getStartTime() != null && existingTask.getDuration() != null) &&
                    (newTask.getStartTime().isBefore(existingTask.getEndTime()) && newTask.getEndTime().isAfter(existingTask.getStartTime())) ||
                    (newTask.getStartTime().isEqual(existingTask.getStartTime()))) {
                return true;
            }
        }
        return false;
    }

    private int generateNewId() {
        return id++;
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }
}