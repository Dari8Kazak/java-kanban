public class SubTask extends Task {
    private final int epicId;

    public SubTask(String name, String overview, int epicId) {
        super(name, overview);
        this.status = TaskStatus.NEW;
        this.epicId = epicId;
    }

    public SubTask(String name, String overview, TaskStatus status, int epicId, int id) {
        super(name, overview, status);
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
                ", описание='" + overview + '\'' +
                ", идентификатор=" + id +
                ", статус=" + status +
                '}';
    }
}