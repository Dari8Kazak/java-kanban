package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> epicSubTasksId = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
    }

    public Epic(String name, String description, int id) {
        super(name, description, TaskStatus.NEW, id);
        this.id = id;
    }

    public List<Integer> getEpicSubTasks() {
        return new ArrayList<>(epicSubTasksId);
    }

    public void addSubTaskId(SubTask subTask) {
        epicSubTasksId.add(subTask.getId());
    }

    public void deleteAllSubtasksId() {
        epicSubTasksId.clear();
    }

    public void deleteSubTaskById(int subTaskId) {
        epicSubTasksId.remove(Integer.valueOf(subTaskId));
    }

    @Override
    public String toString() {
        return "Эпик{" +
                "подзадачи эпика=" + epicSubTasksId +
                ", название задачи='" + name +
                '\'' + ", описание='" + description +
                '\'' + ", идентификатор=" + id +
                ", статус=" + status + '}';
    }
}