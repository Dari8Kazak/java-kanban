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

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final String pathToFile = System.getProperty("user.home") + File.separator + "register.csv";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public FileBackedTaskManager(File file) {
        checkFile(file);
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        super.getPrioritizedTasks().add(task);
        save();
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        super.getPrioritizedTasks().add(epic);
        save();
        return epic;
    }

    public static void loadFromFile(File file, FileBackedTaskManager loadedManager) throws FileException {

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Пропускаем первую строку (заголовок)

            while ((line = reader.readLine()) != null) {
                if (line.contains("ID")) continue;
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String type = parts[1];
                String name = parts[2];
                String status = parts[3];
                String description = parts[4];
                Duration duration = parts.length > 5 && !parts[5].isEmpty() ? Duration.ofMinutes(Long.parseLong(parts[5])) : null;
                LocalDateTime startTime = parts.length > 6 && !parts[6].isEmpty() ? LocalDateTime.parse(parts[6], formatter) : null;

                switch (TaskType.valueOf(type)) {
                    case TASK:
                        Task task = new Task(id, name, description, TaskStatus.valueOf(status), duration, startTime);
                        getTasks().put(task.getId(), task);
                        loadedManager.createTask(task);
                        break;
                    case SUBTASK:
                        int epicId = Integer.parseInt(parts[6]);
                        SubTask subtask = new SubTask(id, name, description, TaskStatus.valueOf(status), duration, startTime, epicId);
                        getSubTasks().put(subtask.getId(), subtask);
                        loadedManager.createSubTask(subtask);
                        break;
                    case EPIC:
                        Epic epic = new Epic(id, name, TaskStatus.valueOf(status), description, duration, startTime);
                        getEpicTasks().put(epic.getId(), epic);
                        loadedManager.createEpic(epic);
                        break;
                }
            }
        } catch (IOException e) {
            throw new FileException("Ошибка загрузки данных из файла: " + e.getMessage());
        }
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
        return epic;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        super.getPrioritizedTasks().add(subTask);
        save();
        return subTask;
    }

    @Override
    public Task deleteTaskById(Integer id) {
        super.deleteTaskById(id);
        save();
        return null;
    }

    @Override
    public Epic deleteEpicById(Integer id) {
        super.deleteEpicById(id);
        save();
        return null;
    }

    @Override
    public SubTask deleteSubTaskById(Integer id) {
        SubTask subtask = getSubTaskById(id);
        if (subtask != null) {
            super.getPrioritizedTasks().remove(subtask);
        }
        super.deleteSubTaskById(id);
        save();
        return null;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        super.getPrioritizedTasks().add(subTask);
        save();
        return subTask;
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
    }

    private String taskToString(Task task) {
        return String.join(",", String.valueOf(task.getId()),
                task.getTaskType().toString(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                String.valueOf(task.getDuration().toMinutes()),
                task.getStartTime().format(formatter));
    }

    private String epicToString(Epic epic) {
        return String.join(",", String.valueOf(epic.getId()),
                epic.getTaskType().toString(),
                epic.getName(),
                epic.getStatus().toString(),
                epic.getDescription(),
                String.valueOf(epic.getDuration().toMinutes()),
                epic.getStartTime().format(formatter));
    }

    private String subTaskToString(SubTask subTask) {
        return String.join(",", String.valueOf(subTask.getId()),
                subTask.getTaskType().toString(),
                subTask.getName(),
                subTask.getStatus().toString(),
                subTask.getDescription(),
                String.valueOf(subTask.getDuration().toMinutes()),
                subTask.getStartTime().format(formatter));
    }

    public void save(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
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
}