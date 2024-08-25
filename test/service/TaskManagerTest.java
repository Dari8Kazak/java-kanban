package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Task;
import model.Epic;
import model.SubTask;
import model.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test    // Тест 1: Проверка, что объект Subtask нельзя сделать своим же эпиком
    public void testSubtaskCannotBeEpic() {
        SubTask subTask = new SubTask("Subtask", "Subtask description", 1);
        subTask.setEpicId(2);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> subTask.setEpicId(subTask.getId()), "Subtask не может быть добавлен как его собственный Epic");
        Assertions.assertEquals("Subtask не может быть добавлен как его собственный Epic.", thrown.getMessage());
    }

    @Test    // Тест 2: Проверка, что InMemoryTaskManager добавляет задачи разного типа
    public void testInMemoryTaskManagerAddsDifferentTaskTypes() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Task1", "Description 1");
        Epic epic = new Epic("Epic", "Epic description");

        manager.addEpic(epic);
        SubTask subTask = new SubTask("Subtask", "Subtask description", epic.getId());

        manager.addTask(task);
        manager.addSubTask(subTask);

        assertNotNull(manager.getTaskById(task.getId()), "Находим task по ID");
        assertNotNull(manager.getEpicById(epic.getId()), "Находим epic по ID");
        assertNotNull(manager.getSubTaskById(subTask.getId()), "Находим subtask по ID");
    }

    @Test  // Тест 3: Проверка, что задачи с заданным id и сгенерированным id не конфликтуют
    public void testNoIdConflictInTaskManager() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Task1", "Description 1");
        Task task2 = new Task("Task1", "Description 2");

        manager.addTask(task1);
        manager.addTask(task2);

        List<Task> tasks = manager.getAllTasks();

        Assertions.assertEquals(2, tasks.size(), "Добавляем 2 tasks с одинаковым ID");
        assertTrue(tasks.contains(task1), "Task 1 проверяем наличие в списке");
        assertTrue(tasks.contains(task2), "Task 2 проверяем наличие в списке");
    }

    @Test     //Тест 4: на неизменность задачи при добавлении задачи в менеджер
    public void testTaskImmutabilityOnAdd() {
        Task originalTask = new Task("Task 1", "Description task 1");

        String originalName = originalTask.getName();
        String originalDescription = originalTask.getDescription();

        taskManager.addTask(originalTask);

        Assertions.assertEquals(originalName, originalTask.getName(), "Имя задачи изменилось!");
        Assertions.assertEquals(originalDescription, originalTask.getDescription(), "Описание задачи изменилось!");
        assertNotNull(taskManager.getTaskById(originalTask.getId()), "Задача не была добавлена в менеджер!");
    }

    @Test  // Тест 5: проверка, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    public void testUniqueIdConflict() {
        Task task1 = new Task("Задача 1", "Description task 1");
        int existingId = taskManager.addTask(task1);

        Task task2 = new Task("Задача 2", "Description task 2");
        task2.setId(existingId);

        int result = taskManager.addTask(task2);
        assertEquals(-1, result, "Ожидался результат -1, так как задача с таким ID уже существует");

        Task task3 = new Task("Задача 3", "Описание задачи 3");
        int newTask3Id = taskManager.addTask(task3);

        assertNotNull(taskManager.getTaskById(newTask3Id), "Задача 3 должна быть добавлена и найдена по ID");
    }

    @Test     // Тест 6: что задача сохранена и возвращается корректный объект.
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask Описание", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);

        Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        Assertions.assertEquals(task, savedTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач.");
        Assertions.assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
        Assertions.assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статус задачи не совпадает!");
    }

    @Test     // Тест 7: Проверка, что эпик сохранен и возвращается правильный объект.
    void addNewEpic() {
        Epic epic = new Epic("Test Epic", "Test Epic Описание");
        int epicId = taskManager.addEpic(epic);

        assertTrue(epicId >= 0, "Неверный ID эпика.");

        Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        Assertions.assertEquals(epic, savedEpic, "Эпики не совпадают.");

        List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        Assertions.assertEquals(1, epics.size(), "Неверное количество эпиков.");
        Assertions.assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test    // Тест 8: Проверка, что подзадача сохранена и возвращается корректный объект.
    void addNewSubTask() {
        Epic epic = new Epic("Test Epic", "Test Epic описание");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Test подзадачи", "Test описание подзадачи", epic.getId());
        int subTaskId = taskManager.addSubTask(subTask);

        taskManager.getSubTaskById(subTaskId);

        List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Подзадачи не возвращаются.");
        Assertions.assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        Assertions.assertEquals(subTask, subTasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test     // Тест 9: Проверка, что обновленная задача сохранена и совпадает с новой версией.
    void updateTask() {
        Task task = new Task("Test задачи", "Test описание задачи", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);

        Task updatedTask = new Task("Обновление задачи", "Обновление описания", TaskStatus.IN_PROGRESS, taskId);
        taskManager.updateTask(updatedTask);

        Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Обновленная задача не найдена.");
        Assertions.assertEquals(updatedTask, savedTask, "Задачи не совпадают после обновления.");
    }

    @Test     // Тест 10: Задача, удалена и в хранилище ее нет.
    void deleteTaskById() {
        Task task = new Task("Удаление задачи", "Описание", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);

        taskManager.deleteTaskById(taskId);
        Task deletedTask = taskManager.getTaskById(taskId);

        Assertions.assertNull(deletedTask, "Задача должна быть удалена.");
    }
}