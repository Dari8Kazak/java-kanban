package model;

import enums.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> epicSubTasksId = new ArrayList<>();


    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public Epic(String name, String description, int id) {
        super(name, description, TaskStatus.NEW, id);
    }

    public Epic(int id, String name, String description, TaskStatus taskStatus) {
        super(name, description, TaskStatus.NEW, id);
    }

    public List<Integer> getEpicSubTasks() {
        return new ArrayList<>(epicSubTasksId);
    }

    public void addSubTaskId(int subTaskId) {
        if (subTaskId == this.id) {
            throw new IllegalArgumentException("Эпик не может быть добавлен как его собственная подзадача");
        }
        epicSubTasksId.add(subTaskId);
    }

    public void deleteAllSubtasksId() {
        epicSubTasksId.clear();
    }

    public void deleteSubTaskById(int subTaskId) {
        epicSubTasksId.remove(Integer.valueOf(subTaskId));
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", getId(), getType(), getName(), getStatus(), getDescription());
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}