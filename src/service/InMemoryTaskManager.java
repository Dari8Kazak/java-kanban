package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
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
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epics.get(epicId);
    }

    @Override
    public int addEpic(Epic epic) {
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

    @Override
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

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteAllSubtasksId();
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
    public int addSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("подзадача не добавлена");
            return 0;
        }
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            subTask.setId(generateNewId());
            epic.addSubTaskId(subTask.getId());
            subTasks.put(subTask.getId(), subTask);

            updateEpicStatus(epic);
        }
        return 0;
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

    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}