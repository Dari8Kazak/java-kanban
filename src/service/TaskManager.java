package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int id;

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public void addTask(Task task) {
        if (task != null && task.getClass() == Task.class) {
            task.setId(generateNewId());
            tasks.put(task.getId(), task);
        } else {
            System.out.println("задача не добавлена");
        }
    }

    public void updateTask(Task task) {
        if (task != null && tasks.containsKey(task.getId()) && task.getClass() == Task.class) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("задачи с таким id нет.");
        }
    }

    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    public Epic getEpicById(int epicId) {
        return epics.get(epicId);
    }

    public void addEpic(Epic epic) {
        if (epic != null) {
            epic.setId(generateNewId());
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Эпик не добавлен");
        }
    }

    public void updateEpic(Epic epic) {
        if (epic != null && epics.containsKey(epic.getId())) {
            Epic updEpic = epics.get(epic.getId());
            updEpic.setName(epic.getName());
            updEpic.setDescription((epic.getDescription()));
        } else {
            System.out.println("Эпика с таким id нет.");
        }
    }

    public void deleteEpicById(int epicId) {
        epics.remove(epicId);
        Epic tmpEpic = epics.get(epicId);
        if (tmpEpic != null) {
            List<Integer> tmpList = tmpEpic.getEpicSubTasks();
            for (Integer subTaskId : tmpList) {
                subTasks.remove(subTaskId);
            }
        }
    }

    public List<SubTask> getAllEpicSubTasks(Epic epicId) {
        if (epicId == null) {
            System.out.println("подзадачи не получены");
            return new ArrayList<>();
        }
        List<SubTask> AllSubTasksEpic = new ArrayList<>();
        Epic tmpEpic = epics.get(epicId.getId());
        for (Integer subTaskId : tmpEpic.getEpicSubTasks()) {
            AllSubTasksEpic.add(subTasks.get(subTaskId));
        }
        return AllSubTasksEpic;
    }

    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasksId();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    public SubTask getSubTaskById(int subTaskId) {
        return subTasks.get(subTaskId);
    }

    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("подзадача не добавлена");
            return;
        }
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            subTask.setId(generateNewId());
            epic.addSubTaskId(subTask);
            subTasks.put(subTask.getId(), subTask);
            updateEpicStatus(epic);
        }
    }

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
        int isIN_PROGRESS = 0;
        int isDone = 0;

        List<Integer> subTasksId = epic.getEpicSubTasks();
        for (Integer subTaskId : subTasksId) {
            if (subTasks.get(subTaskId).getStatus() == TaskStatus.NEW) {
                isNew++;
            } else if (subTasks.get(subTaskId).getStatus() == TaskStatus.IN_PROGRESS) {
                isIN_PROGRESS++;
            } else if (subTasks.get(subTaskId).getStatus() == TaskStatus.DONE) {
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
}