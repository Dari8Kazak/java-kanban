package Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Task;
import model.Epic;
import model.SubTask;
import model.TaskStatus;
import service.TaskManager;
import service.InMemoryTaskManager;

import java.util.List;

class TaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testTaskEqualityById() {
        Task task1 = new Task("Задача1", "Описание 1");
        Task task2 = new Task("Задача2", "Описание 2");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Задачи с одинаковым идентификатором должны быть равны");
    }

    @Test
    public void testSubtaskEqualityById() {
        Task epic = new Epic("Epic", "Epic описание");
        SubTask subTask1 = new SubTask("Subtask1", "описание подзадачи", epic.getId());
        SubTask subTask2 = new SubTask("Subtask2", "описание подзадачи", epic.getId());
        subTask1.setId(1);
        subTask2.setId(1);
        assertEquals(subTask1, subTask2, "SubTask instances with the same ID should be equal");
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask Описание", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);

        Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Test Epic", "Test Epic Описание");
        int epicId = taskManager.addEpic(epic);

        Epic savedEpic = (Epic) taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addNewSubTask() {
        Epic epic = new Epic("Test Epic", "Test Epic описание");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Test подзадачи", "Test описание подзадачи", epic.getId());
        int subTaskId = taskManager.addSubTask(subTask);

        SubTask savedSubTask = (SubTask) taskManager.getSubTaskById(subTaskId);


        List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Подзадачи не возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task task = new Task("Test задачи", "Test описание задачи", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);

        Task updatedTask = new Task("Обновление задачи", "Обновление описания", TaskStatus.IN_PROGRESS, taskId);
        taskManager.updateTask(updatedTask);

        Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Обновленная задача не найдена.");
        assertEquals(updatedTask, savedTask, "Задачи не совпадают после обновления.");
    }

    @Test
    void deleteTaskById() {
        Task task = new Task("Удаление задачи", "Описание", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);

        taskManager.deleteTaskById(taskId);
        Task deletedTask = taskManager.getTaskById(taskId);

        assertNull(deletedTask, "Задача должна быть удалена.");
    }
}
