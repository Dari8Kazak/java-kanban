package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private int id;

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
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

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epics.get(epicId);
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
    public SubTask getSubTaskById(int subTaskId) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTasks.get(subTaskId);
    }

    @Override
    public int createTask(Task task) {
        if (task != null) {
            task.setId(generateNewId());
            tasks.put(task.getId(), task);
        } else {
            System.out.println("Задача не добавлена, так как она равна null.");
        }
        return 0;
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
    public boolean createSubTask(SubTask subtask) {
        if (!isTaskTimeIntersect(subtask)) {
            if (!(subtask instanceof SubTask) || epics.get(subtask.getEpicId()) == null) {
                return false;
            }
            Epic epic = epics.get(subtask.getEpicId());
            subtask.setId(generateNewId());
            subtask.setEpicId(epic.getId());
            epic.addSubTaskId(subtask);
            subTasks.put(subtask.getId(), subtask);
            epic.updateStatus();
            epics.put(epic.getId(), epic);
            return true;
        }
        return false;
    }
//    @Override
//    public int createSubTask(SubTask subTask) {
//        if (subTask == null) {
//            System.out.println("подзадача не добавлена");
//            return 0;
//        }
//        Epic epic = epics.get(subTask.getEpicId());
//        if (epic != null) {
//            subTask.setId(generateNewId());
//            epic.addSubTaskId(subTask);
//
//            subTasks.put(subTask.getId(), subTask);
//
//            updateEpicStatus(epic);
//        }
//        return 0;
//    }

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
    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId()) && task.getClass() == Task.class) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("задачи с таким id нет.");
        }
    }

    @Override
    public void updateSubTask(SubTask updSubTask) {
        if (updSubTask != null && subTasks.containsKey(updSubTask.getId())) {
            SubTask subTask = subTasks.get(updSubTask.getId());
            if (subTask.getEpicId() == updSubTask.getEpicId()) {
                subTasks.put(updSubTask.getId(), updSubTask);
                Epic epic = epics.get(updSubTask.getEpicId());
                updateEpicStatus(epic);
            }
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
        return allSubTasksEpic;
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
    public boolean isSubtaskTimeIntersect(SubTask newSubtask) {
        return false;
    }

    @Override
    public boolean isTaskTimeIntersect(Task newTask) {
        return isTaskOverlap(newTask);
    }

    public boolean isTaskOverlap(Task newTask) {
        if (newTask.getStartTime() == null ||
                newTask.getDuration() == null)
            return false;

        for (Task existingTask : getPrioritizedTasks()) {
            if ((existingTask.getStartTime() != null || existingTask.getDuration() != null) &&
                    (newTask.getStartTime().isBefore(existingTask.getEndTime()) && newTask.getEndTime().isAfter(existingTask.getStartTime()))) {
                return true;
            }
        }

        return false; // Если пересечений не найдено, возвращаем false
    }

    @Override
    public boolean isTimeOverlap(Task task1, Task task2) {
        return false;
    }

    private int generateNewId() {
        return id++;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getEpicTasks() {
        return epics;
    }

    public TreeSet<Task> getPrioritizedTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll((Collection<? extends Task>) tasks);
        allTasks.addAll((Collection<? extends Task>) subTasks);

        // Сортировка задач по startTime
        Collections.sort(allTasks, Comparator.comparing(Task::getStartTime, Comparator.naturalOrder()));
        return prioritizedTasks;
    }

}