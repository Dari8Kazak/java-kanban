package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskConverter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static Task fromString(String line) {
        String[] parts = line.split(",");

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        String description = parts[3];
        String status = parts[4];

        Duration duration = parts.length > 5 && !parts[5].isEmpty() ?
                Duration.ofMinutes(Long.parseLong(parts[5])) : null;
        LocalDateTime startTime = parts.length > 6 && !parts[6].isEmpty() ?
                LocalDateTime.parse(parts[6], formatter) : null;


        return switch (parts[1]) {
            case "TASK" -> new Task(id, name, description, TaskStatus.valueOf(status), duration, startTime);
            case "SUBTASK" -> {
                int epicId = Integer.parseInt(parts[7]);
                yield new SubTask(id, name, description, TaskStatus.valueOf(status), duration, startTime, epicId);
            }
            case "EPIC" -> new Epic(id, name, TaskStatus.valueOf(status), description, duration, startTime);
            default -> throw new IllegalArgumentException("Unknown task type: " + parts[1]);
        };
    }
}
