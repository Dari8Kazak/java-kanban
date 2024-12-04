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

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public FileBackedTaskManager(File existFile) {
        checkFile(existFile);
    }

    public static TaskManager loadFromFile(File file) throws FileException {
        TaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();

            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                Task task = fromString(line);
                TaskType taskType = task.getType();
                switch (taskType) {
                    case TASK -> manager.createTask(task);
                    case EPIC -> manager.createEpic((Epic) task);
                    case SUBTASK -> manager.createSubTask((SubTask) task);
                }
            }
        } catch (IOException e) {
            throw new FileException("Ошибка при загрузке данных из файла: " + e.getMessage());
        }
        return manager;
    }

    private static Task fromString(String taskFromFile) {
        String[] contents = taskFromFile.split(",");
        int taskId = Integer.parseInt(contents[0]);
        TaskType taskType = TaskType.valueOf(contents[1]);
        String taskName = contents[2];
        TaskStatus taskStatus = TaskStatus.valueOf(contents[3]);
        String taskDescription = contents[4];
        Duration duration = Duration.ofMinutes(Integer.parseInt(contents[5]));
        LocalDateTime startTime = LocalDateTime.parse(contents[6]);

        if (taskType == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(contents[7]);
            return new SubTask(taskId, taskName, taskDescription, taskStatus, duration, startTime, epicId);
        }
        return switch (taskType) {
            case TASK -> new Task(taskId, taskName, taskDescription, taskStatus, duration, startTime);
            case EPIC -> new Epic(taskName, taskDescription, taskStatus, duration, startTime);
            default -> throw new IllegalArgumentException("Неверный тип задачи: " + taskType);
        };
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    public void deleteSubTaskById(int id) {
        SubTask subtask = getSubTaskById(id);
        if (subtask != null) {
            super.getPrioritizedTasks().remove(subtask);
        }
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    public void updateSubtask(SubTask subtask) {
        super.updateSubTask(subtask);
        super.getPrioritizedTasks().add(subtask);
        save();
    }

    @Override
    public int createEpic(Epic epic) {
        super.createEpic(epic);
        super.getPrioritizedTasks().add(epic);
        save();
        return epic.getId();
    }

    public int createTask(Task task) {
        super.createTask(task);
        super.getPrioritizedTasks().add(task);
        save();
        return 0;
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
                    System.out.println("Файла существует");
            } catch (FileException | IOException e) {
                throw new FileException("Не удалось создать директорию или файл не обнаружен");
            }
        }
        load();
    }

    // Обновляем метод загрузки с учетом новых полей
    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(pathToFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("ID")) continue;
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String type = parts[1];
                String name = parts[2];
                String status = parts[3];
                String description = parts[4];
                LocalDateTime startTime = null;
                Duration duration = null;
                if (parts.length > 5) startTime = !parts[5].isEmpty() ? LocalDateTime.parse(parts[5], formatter) : null;
                if (parts.length > 6)
                    duration = !parts[6].isEmpty() ? Duration.ofMinutes(Long.parseLong(parts[6])) : null;

                if (type.equals(TaskType.TASK.name())) {
                    Task task = new Task(id, name, description, TaskStatus.valueOf(status), duration, startTime);
                    getTasks().put(task.getId(), task);
                }

                if (type.equals(TaskType.SUBTASK.name())) {
                    int epicId = Integer.parseInt(parts[7]);
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
            writer.write("ID,TYPE,NAME,STATUS,DESCRIPTION,START_TIME,DURATION,EPIC\n");
            for (Task task : getTasks().values()) {
                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : getEpicTasks().values()) {
                writer.write(taskToString(epic) + "\n");
            }
            for (SubTask subtask : getSubTasks().values()) {
                writer.write(taskToString(subtask) + "\n");
            }
        } catch (IOException e) {
            System.out.println("Failed to save tasks to file: " + e.getMessage());
        }
    }

    public String taskToString(Task task) {
        String startTime = (task.getStartTime() != null) ? task.getStartTime().format(formatter) : "";
        String duration = (task.getDuration() != null) ? String.valueOf(task.getDuration().toMinutes()) : "";
        int epicId = 0;
        if (task instanceof SubTask) {
            epicId = ((SubTask) task).getEpicId();
        }

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s", task.getId(), task instanceof SubTask ? TaskType.SUBTASK : (task instanceof Epic ? TaskType.EPIC : TaskType.TASK), task.getName(), task.getStatus(), task.getDescription(), startTime, duration, epicId == 0 ? "" : epicId);
    }

}
