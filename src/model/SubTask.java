package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description, Integer epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public SubTask(Integer id, String name, TaskStatus status, String description, Integer epicId) {
        super(id, name, status, description);
        this.epicId = epicId;
    }

    public SubTask(SubTask subtask) {
        super(subtask.name, subtask.description);
        this.epicId = subtask.epicId;
    }

    public SubTask(Integer id, String name, String description, TaskStatus taskStatus, Duration duration, LocalDateTime startTime, Integer epicId) {
        super(id, name, description, taskStatus, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, TaskStatus taskStatus, Duration duration, LocalDateTime startTime, Integer epicId) {
        super(name, description, taskStatus, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Integer epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, TaskStatus.NEW, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(Task task, Integer epicId) {
        super(task);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        if (epicId.equals(this.id)) {
            throw new IllegalArgumentException("Subtask не может быть добавлен как его собственный Epic.");
        }
        this.epicId = epicId;
    }

    @Override
    public Integer getId() {
        return super.getId();
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s", getId(), getTaskType(), getName(), getStatus(), getDescription(), getDuration(), getStartTime());

    }
}