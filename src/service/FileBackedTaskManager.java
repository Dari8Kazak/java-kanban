package service;

import enums.TaskType;
import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private final String pathToFile = System.getProperty("user.home") + File.separator + "register.csv";

    public FileBackedTaskManager(File existFile) {
        this.file = existFile;
    }

    public static TaskManager loadFromFile(File file) throws ManagerLoadException {
        TaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();

            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                Task task = fromString(line);
                TaskType taskType = task.getType();
                switch (taskType) {
                    case TASK -> manager.addTask(task);
                    case EPIC -> manager.addEpic((Epic) task);
                    case SUBTASK -> manager.addSubTask((SubTask) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка при загрузке данных из файла: " + e.getMessage());
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

        if (taskType == TaskType.SUBTASK) {
            int epicId = Integer.parseInt(contents[5]);
            return new SubTask(epicId, taskId, taskName, taskDescription, taskStatus);
        }
        return switch (taskType) {
            case TASK -> new Task(taskName, taskDescription, taskStatus, taskId);
            case EPIC -> new Epic(taskId, taskName, taskDescription, taskStatus);
            default -> throw new IllegalArgumentException("Неверный тип задачи: " + taskType);
        };
    }

    @Override
    public int addTask(Task task) {
        super.addTask(task);
        save();
        return task.getId();
    }

    @Override
    public int addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic.getId();
    }

    @Override
    public int addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
        return subTask.getId();
    }

    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("id,type,name,status,description,epic\n");

            for (Task task : getAllTasks()) {
                bw.write(task.toString() + "\n");
            }

            for (Epic epic : getAllEpics()) {
                bw.write(epic.toString() + "\n");
            }

            for (SubTask subTask : getAllSubTasks()) {
                bw.write(subTask.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задач в файл: " + e.getMessage(), e);
        }
    }

    public String getPathToFile() {
        return pathToFile;
    }
}
