package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, TaskStatus status, String description, int epicId) {
        super(id, name, status, description);
        this.epicId = epicId;
    }

    public SubTask(SubTask subtask) {
        super(subtask.name, subtask.description);
        this.epicId = subtask.epicId;
    }

    public SubTask(int id, String name, String description, TaskStatus taskStatus, Duration duration, LocalDateTime startTime, int epicId) {
        super(id, name, description, taskStatus, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, TaskStatus.NEW, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(Task task, int epicId) {
        super(task);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId == this.id) {
            throw new IllegalArgumentException("Subtask не может быть добавлен как его собственный Epic.");
        }
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", getId(), getType(), getName(), getStatus(), getDescription());

    }
}
