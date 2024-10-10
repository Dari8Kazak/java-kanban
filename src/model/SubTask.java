package model;

import enums.TaskType;

public class SubTask extends Task {
    private int epicId;

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, TaskStatus status, int epicId, int id) {
        super(name, description, status);
        this.id = id;
        this.epicId = epicId;
    }

    public SubTask(int epicId, int id, String name, String description, TaskStatus status) {
        super(name, description, status);
        this.id = id;
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
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", getId(), getType(), getName(), getStatus(), getDescription());//    @Override

    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}

