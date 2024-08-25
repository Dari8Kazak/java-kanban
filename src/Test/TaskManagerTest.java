package Test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Model.Task;
import Model.Epic;
import Model.SubTask;
import Model.TaskStatus;
import Service.TaskManager;
import Service.InMemoryTaskManager;
import Service.InMemoryHistoryManager;

import java.util.List;

import static Service.InMemoryTaskManager.generateNewId;
import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    private TaskManager taskManager;
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();
    }

    // Тест 1: Проверка, что экземпляры класса Task равны друг другу, если равен их id
    @Test
    public void testTaskEqualityById() {
        Task task1 = new Task("Task1", "Description 1");
        Task task2 = new Task("Task2", "Description 2");
        task1.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task1, task2, "Task с одинаковым ID должны быть равны");
    }

    // Тест 2: Проверка, что наследники класса Task равны друг другу, если равен их id
    @Test
    public void testSubtaskEqualityById() {
        Task epic = new Epic("Epic", "Epic description");
        SubTask subTask1 = new SubTask("Subtask1", "Subtask description", epic.getId());
        SubTask subTask2 = new SubTask("Subtask2", "Subtask description", epic.getId());
        subTask1.setId(1);
        subTask2.setId(1);
        Assertions.assertEquals(subTask1, subTask2, "SubTask с одинаковым ID должны быть равны");
    }

    // Тест 3: Проверка, что объект Epic нельзя добавить в самого себя в виде подзадачи
    @Test
    public void testEpicCannotBeAssignedAsSubtask() {
        Epic epic = new Epic("Epic", "Epic description");
        epic.setId(generateNewId());
        new SubTask("Subtask", "Subtask description", epic.getId());

        assertThrows(IllegalArgumentException.class, () -> epic.addSubTaskId(epic.getId()), "Epic не может быть добавлен как его собственная подзадача");
    }

    // Тест 4: Проверка, что объект Subtask нельзя сделать своим же эпиком
    @Test
    public void testSubtaskCannotBeEpic() {
        SubTask subTask = new SubTask("Subtask", "Subtask description", 1);
        subTask.setEpicId(2);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> subTask.setEpicId(subTask.getId()), "Subtask не может быть добавлен как его собственный Epic");

        Assertions.assertEquals("Subtask не может быть добавлен как его собственный Epic.", thrown.getMessage());
    }

    // Тест 5: Проверка, что InMemoryTaskManager добавляет задачи разного типа
    @Test
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

    // Тест 6: Проверка, что задачи с заданным id и сгенерированным id не конфликтуют
    @Test
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

    @Test // Тест 7: что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    public void testTaskHistoryPreservation() {
        Task originalTask = new Task("Task A", "Description task A");
        originalTask.setId(1);
        historyManager.addTask(originalTask);

        Task updatedTask = new Task("Task B", "Description task B");
        updatedTask.setId(1);
        historyManager.addTask(updatedTask);

        List<Task> taskHistory = historyManager.getHistory();

        assertEquals(2, taskHistory.size(), "История должна содержать две задачи!");

        Task previousTask = taskHistory.get(0);
        Task latestTask = taskHistory.get(1);

        assertEquals("Task A", previousTask.getName(), "Имя предыдущей задачи неверно!");
        assertEquals("Description task A", previousTask.getDescription(), "Описание предыдущей задачи неверно!");
        assertEquals("Task B", latestTask.getName(), "Имя обновленной задачи неверно!");
        assertEquals("Description task B", latestTask.getDescription(), "Описание обновленной задачи неверно!");
    }

    @Test     //Тест 8: на неизменность задачи при добавлении задачи в менеджер
    public void testTaskImmutabilityOnAdd() {
        Task originalTask = new Task("Task 1", "Description task 1");

        String originalName = originalTask.getName();
        String originalDescription = originalTask.getDescription();

        taskManager.addTask(originalTask);

        Assertions.assertEquals(originalName, originalTask.getName(), "Имя задачи изменилось!");
        Assertions.assertEquals(originalDescription, originalTask.getDescription(), "Описание задачи изменилось!");

        assertNotNull(taskManager.getTaskById(originalTask.getId()), "Задача не была добавлена в менеджер!");
    }

    @Test  // Тест 9: проверка, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    public void testUniqueIdConflict() {

        Task task1 = new Task("Задача 1", "Description task 1");
        Task task2 = new Task("Задача 2", "Description task 2");

        taskManager.addTask(task1);
        int generatedId = task1.getId();

        task2.setId(generatedId);

        IllegalArgumentException thrown;
        thrown = assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.addTask(task2));

        Assertions.assertEquals("Задача с таким ID уже существует.", thrown.getMessage());

        Task task3;
        task3 = new Task("Задача 3", "Описание задачи 3");
        taskManager.addTask(task3);

        assertNotNull(taskManager.getTaskById(task3.getId()));
    }

    @Test // Тест 10: что задача сохранена и возвращается корректный объект.
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

    @Test // Тест 11: Проверка, что эпик сохранен и возвращается правильный объект.
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
    @Test // Тест 12: Проверка, что подзадача сохранена и возвращается корректный объект.
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

    @Test // Тест 13: Проверка, что обновленная задача сохранена и совпадает с новой версией.
    void updateTask() {
        Task task = new Task("Test задачи", "Test описание задачи", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);

        Task updatedTask = new Task("Обновление задачи", "Обновление описания", TaskStatus.IN_PROGRESS, taskId);
        taskManager.updateTask(updatedTask);

        Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Обновленная задача не найдена.");
        Assertions.assertEquals(updatedTask, savedTask, "Задачи не совпадают после обновления.");
    }

    @Test // Тест 14: Задача, удалена и в хранилище ее нет.
    void deleteTaskById() {
        Task task = new Task("Удаление задачи", "Описание", TaskStatus.NEW);
        int taskId = taskManager.addTask(task);

        taskManager.deleteTaskById(taskId);
        Task deletedTask = taskManager.getTaskById(taskId);

        Assertions.assertNull(deletedTask, "Задача должна быть удалена.");
    }
}
