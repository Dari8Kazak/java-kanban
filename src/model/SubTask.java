package model;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, TaskStatus status, int epicId, int id) {
        super(name, description, status);
        this.id = id;
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "подзадача{" +
                "Эпик=" + epicId +
                ", название задачи='" + name + '\'' +
                ", описание='" + description + '\'' +
                ", идентификатор=" + id +
                ", статус=" + status +
                '}';
    }
}