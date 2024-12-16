package model;

public enum TaskType {
    TASK, SUBTASK, EPIC;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
}