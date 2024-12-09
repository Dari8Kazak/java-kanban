package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subTasks = new ArrayList<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subTasks = new ArrayList<>();
    }

    public Epic(int id, String name, TaskStatus status, String description, ArrayList<SubTask> subTasks) {
        super(id, name, status, description);
        this.subTasks = subTasks;
    }

    public Epic(Epic epic) {
        super(epic.getId(), epic.name, epic.description);
        ArrayList<SubTask> newSubtasks = new ArrayList<>();
        for (SubTask subtask : epic.subTasks) {
            newSubtasks.add(new SubTask(subtask));
        }
        this.subTasks = newSubtasks;
    }

    public Epic(int id, String name, TaskStatus taskStatus, String description, Duration duration, LocalDateTime startTime) {
        super(id, name, description, taskStatus, duration, startTime);
        this.subTasks = new ArrayList<>();
    }

    public Epic(String name, String description, TaskStatus status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.subTasks = new ArrayList<>();
    }

    public Epic(int id, String name, String description, TaskStatus taskStatus, Duration duration, LocalDateTime now) {
        super(id, name, description, taskStatus, duration, now);
        this.subTasks = new ArrayList<>();
    }

    public void setSubtask(ArrayList<SubTask> subtasks) {
        this.subTasks = subtasks;
    }

    public void addSubTaskId(SubTask subTask) {
        if (subTask == null) {
            throw new IllegalArgumentException("Подзадача не может быть null");
        }
        if (this.equals(subTask)) {
            throw new IllegalArgumentException("Эпик не может быть подзадачей самого себя");
        }
        if (this.getDuration() == null) {
            this.setDuration(Duration.ZERO);
        }
        Duration subTaskDuration = subTask.getDuration();
        this.setDuration(this.getDuration().plus(subTaskDuration != null ? subTaskDuration : Duration.ZERO));
        this.subTasks.add(subTask);
    }

    public void updateStatus() {
        if (subTasks.isEmpty()) {
            this.status = TaskStatus.NEW;
            return;
        }
        boolean allNew = true;
        boolean allDone = true;
        for (SubTask subtask : subTasks) {
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) {
                this.status = TaskStatus.IN_PROGRESS;
                return;
            }
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }
        if (allNew) {
            this.status = TaskStatus.NEW;
        } else if (allDone) {
            this.status = TaskStatus.DONE;
        } else {
            this.status = TaskStatus.IN_PROGRESS;
        }
    }

    public ArrayList<SubTask> getSubtasks() {
        return this.subTasks;
    }

    @Override
    public void setStatus(TaskStatus status) {
        super.setStatus(status);
        if (status == TaskStatus.DONE) {
            for (SubTask subtask : subTasks) {
                subtask.setStatus(TaskStatus.DONE);
            }
            this.status = TaskStatus.DONE;
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", getId(), getType(), getName(), getStatus(), getDescription(), getSubtasks());
    }

    @Override
    public int getId() {
        return super.getId();
    }

    public void addSubTaskId(int epicId, Duration zero, LocalDateTime now) {
    }
}