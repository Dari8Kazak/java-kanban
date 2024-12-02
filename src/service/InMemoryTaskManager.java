package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    HistoryManager historyManager = Managers.getDefaultHistory();
    private int id = 1;

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        removeHistory(tasks.keySet());
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public int addTask(Task task) {
        if (task != null) {
            task.setId(generateNewId());
            tasks.put(task.getId(), task);
            return task.getId();
        }
        System.out.println("Задача не добавлена, так как она равна null.");
        return -1;
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
    public void removeTaskById(int taskId) {

        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpics() {

        for (Epic epic : epics.values()) {
            List<Integer> subTaskIds = epic.getEpicSubTasks();
            for (Integer subTaskId : subTaskIds) {
                subTasks.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
        }
        removeHistory(epics.keySet());
        epics.clear();
    }


    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public int addEpic(Epic epic) {
        if (epic != null) {
            epic.setId(generateNewId());
            epics.put(epic.getId(), epic);
            return epic.getId();
        } else {
            System.out.println("Эпик не добавлен, так как он равен null");
            return -1;
        }
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
    public void removeEpicById(int epicId) {

        epics.remove(epicId);
        Epic tmpEpic = epics.get(epicId);
        if (tmpEpic != null) {
            List<Integer> tmpList = tmpEpic.getEpicSubTasks();
            for (Integer subTaskId : tmpList) {
                subTasks.remove(subTaskId);
            }
        }
        historyManager.remove(epicId);
    }

    @Override
    public List<SubTask> getAllEpicSubTasks(Epic epicId) {
        if (epicId == null) {
            System.out.println("подзадачи не получены");
            return new ArrayList<>();
        }
        List<SubTask> allSubTasksEpic = new ArrayList<>();
        Epic tmpEpic = epics.get(epicId.getId());
        for (Integer subTaskId : tmpEpic.getEpicSubTasks()) {
            allSubTasksEpic.add(subTasks.get(subTaskId));
        }
        return allSubTasksEpic;
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeAllSubTasks() {
        removeHistory(subTasks.keySet());
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.removeAllSubtasksId();
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
    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("подзадача не добавлена");
            return;
        }
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            subTask.setId(generateNewId());
            epic.addSubTaskId(subTask.getId());
            subTasks.put(subTask.getId(), subTask);
            updateEpicStatus(epic);
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
    public void removeSubTaskById(int subTaskId) {

        if (subTasks.containsKey(subTaskId)) {
            Epic epic = epics.get(subTasks.get(subTaskId).getEpicId());
            if (epic != null) {
                epic.removeSubTaskById(subTaskId);
                subTasks.remove(subTaskId);
                updateEpicStatus(epic);
            }
        }
        historyManager.remove(subTaskId);
    }

    private int generateNewId() {
        return id++;
    }

    private void updateEpicStatus(Epic epic) {
        if (epic == null) {
            System.out.println("Статус эпика не обновлен");
            return;
        }
        if (epic.getEpicSubTasks().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }
        int isNew = 0;
        int isDone = 0;

        List<Integer> subTasksId = epic.getEpicSubTasks();
        for (Integer subTaskId : subTasksId) {
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
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private void removeHistory(Set<Integer> ids) {
        for (int id : ids) {
            historyManager.remove(id);
        }
    }
}