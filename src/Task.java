import java.util.Objects;

public class Task {
    protected String name;
    protected int id;
    protected TaskStatus status;
    protected String overview;

    public Task(String name, String overview) {
        this.name = name;
        this.overview = overview;
        this.status = TaskStatus.NEW;
    }

    public Task(String name, String overview, TaskStatus status) {
        this.name = name;
        this.overview = overview;
        this.status = status;
    }

    public Task(String name, String overview, TaskStatus status, int id) {
        this.name = name;
        this.overview = overview;
        this.status = status;
        this.id = id;
    }

    public String getname() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getoverview() {
        return overview;
    }

    public void setoverview(String overview) {
        this.overview = overview;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return getId() == task.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Задача{" +
                "название задачи='" + name + '\'' +
                ", описание='" + overview + '\'' +
                ", идентификатор=" + id +
                ", статус=" + status +
                '}';

    }
}