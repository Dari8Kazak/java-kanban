package service;


import exceptions.FileException;
import model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;

import static java.lang.Integer.parseInt;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final String pathToFile = System.getProperty("user.home") + File.separator + "register.csv";

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public FileBackedTaskManager(File existFile) {
        checkFile(existFile);
    }

    @Override
    public int createTask(Task task) {
        super.createTask(task);
        super.getPrioritizedTasks().add(task);
        save();
        return 0;
    }

    @Override
    public int createEpic(Epic epic) {
        super.createEpic(epic);
        super.getPrioritizedTasks().add(epic);
        save();
        return epic.getId();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        super.getPrioritizedTasks().add(subTask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subtask = getSubTaskById(id);
        if (subtask != null) {
            super.getPrioritizedTasks().remove(subtask);
        }
        super.deleteSubTaskById(id);
        save();
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    private void checkFile(File existFile) {
        if (!existFile.exists()) {
            System.out.println("Проверка файла на существование");
            Path path = Paths.get(pathToFile);
            try {
                Files.createDirectories(path.getParent());
                if (!Files.exists(path)) {
                    System.out.println("Файл не обнаружен");
                } else
                    System.out.println("Файл существует");
            } catch (FileException | IOException e) {
                throw new FileException("Не удалось создать директорию или файл не обнаружен");
            }
        }
        load();
    }

    private void load() {

        try (BufferedReader reader = new BufferedReader(new FileReader(pathToFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ID")) continue;
                String[] parts = line.split(",");

                int id = parseInt(parts[0]);
                String type = parts[1];
                String name = parts[2];
                String status = parts[3];
                String description = parts[4];

                LocalDateTime startTime = null;
                Duration duration = null;

                if (parts.length > 5)
                    startTime = !parts[5].isEmpty() ? LocalDateTime.parse(parts[5], formatter) : null;
                if (parts.length > 6)
                    duration = !parts[6].isEmpty() ? Duration.ofMinutes(Long.parseLong(parts[6])) : null;

                if (type.equals(TaskType.TASK.name())) {
                    Task task = new Task(id, name, description, TaskStatus.valueOf(status), duration, startTime);
                    getTasks().put(task.getId(), task);
                }

                if (type.equals(TaskType.SUBTASK.name())) {
                    int epicId = parseInt(parts[7]);
                    SubTask subtask = new SubTask(id, name, description, TaskStatus.valueOf(status), duration, startTime, epicId);
                    getSubTasks().put(subtask.getId(), subtask);
                }

                if (type.equals(TaskType.EPIC.name())) {
                    Epic epic = new Epic(id, name, TaskStatus.valueOf(status), description, duration, startTime);
                    getEpicTasks().put(epic.getId(), epic);
                }
            }
        } catch (IOException e) {
            System.out.println("Не удалось загрузить задачи из файла: " + e.getMessage());
        }
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pathToFile))) {
            writer.write("id,type,name,status,description,duration,startTime,epicId");
            writer.newLine();

            for (Task task : super.getAllTasks()) {
                writer.write(taskToString(task));
                writer.newLine();
            }

            for (Epic epic : super.getAllEpics()) {
                writer.write(epicToString(epic));
                writer.newLine();
            }

            for (SubTask subTask : super.getAllSubTasks()) {
                writer.write(subTaskToString(subTask));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new FileException("Ошибка при сохранении данных в файл: " + e.getMessage());
        }
    }

    private String taskToString(Task task) {
        return String.join(",", String.valueOf(task.getId()),
                task.getType().toString(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                String.valueOf(task.getDuration().toMinutes()),
                task.getStartTime().format(formatter));
    }

    private String epicToString(Epic epic) {
        return String.join(",", String.valueOf(epic.getId()),
                epic.getType().toString(),
                epic.getName(),
                epic.getStatus().toString(),
                epic.getDescription(),
                String.valueOf(epic.getDuration().toMinutes()),
                epic.getStartTime().format(formatter));
    }

    private String subTaskToString(SubTask subTask) {
        return String.join(",", String.valueOf(subTask.getId()),
                subTask.getType().toString(),
                subTask.getName(),
                subTask.getStatus().toString(),
                subTask.getDescription(),
                String.valueOf(subTask.getDuration().toMinutes()),
                subTask.getStartTime().format(formatter));
    }
}
