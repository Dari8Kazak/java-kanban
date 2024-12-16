package service;

import model.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> taskSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));
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
            return 1; // Если у первой задачи нет startTime, она будет ниже в порядке
        }
        if (task2.getStartTime() == null) {
            return -1; // Если у второй задачи нет startTime, она будет ниже в порядке
        }
        return task1.getStartTime().compareTo(task2.getStartTime());
    });

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    public HashMap<Integer, Epic> getEpicTasks() {
        return epics;
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

    // Метод для проверки пересечения временных интервалов
    private boolean isOverlapping(Task existingTask, Task newTask) {
        return (newTask.getStartTime().isBefore(existingTask.getEndTime()) &&
                newTask.getEndTime().isAfter(existingTask.getStartTime()));
    }


    @Override
    public int createEpic(Epic epic) {
        if (epic != null) {
            int newId = generateNewId();
            epic.setId(newId);
            epics.put(newId, epic);
            return newId;
        } else {
            System.out.println("Эпик не добавлен");
            return -1;
        }
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    @Override
    public int createSubTask(SubTask subtask) {
        if (!isTaskTimeIntersect(subtask)) {
            if (subtask == null || !epics.containsKey(subtask.getEpicId())) {
                return 0;
            }
            Epic epic = epics.get(subtask.getEpicId());
            subtask.setId(generateNewId());

//            prioritizedTasks.remove(subtask); // Удаляем старую задачу
//            prioritizedTasks.add(subtask); // Добавляем обновленную задачу
            epic.addSubTaskId(subtask);

            subTasks.put(subtask.getId(), subtask);
            epic.updateStatus();
            epics.put(epic.getId(), epic);
        }
        return 0;
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
        for (SubTask subTaskId : subTasksId) {
            if (subTasks.get(subTaskId).getStatus() == TaskStatus.NEW) {
                isNew++;
            }
            if (subTasks.get(subTaskId).getStatus() == TaskStatus.DONE) {
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
        epics.remove(epicId);
        Epic tmpEpic = epics.get(epicId);
        if (tmpEpic != null) {
            ArrayList<SubTask> tmpList = tmpEpic.getSubtasks();
            for (SubTask subTaskId : tmpList) {
                subTasks.remove(subTaskId);

            }
        }
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        if (subTasks.containsKey(subTaskId)) {
            Epic epic = epics.get(subTasks.get(subTaskId).getEpicId());
            if (epic != null) {
                epic.deleteSubTaskById(subTaskId);
                subTasks.remove(subTaskId);
                updateEpicStatus(epic);
            }
        }
    }

    @Override
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.getSubtasks().clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public List<Epic> getAllEpics() {
        List<Epic> epicLists = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epic.setSubtask(new ArrayList<>());
            epicLists.add(epic);
        }
        return epicLists;
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

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(taskSet);
    }


    @Override
    public List<SubTask> getAllEpicSubTasks(Epic epicId) {
        if (epicId == null) {
            System.out.println("подзадачи не получены");
            return new ArrayList<>();
        }
        List<SubTask> allSubTasksEpic = new ArrayList<>();
        Epic tmpEpic = epics.get(epicId.getId());
        for (SubTask subTaskId : tmpEpic.getSubtasks()) {
            allSubTasksEpic.add(subTasks.get(subTaskId));
        }
        return new ArrayList<>();
    }


}