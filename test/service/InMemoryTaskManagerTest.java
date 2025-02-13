package service;

import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<TaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @AfterEach
    public void tearDown() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTasks();
        taskManager.deleteAllEpics();
    }

    @DisplayName("Тест 1: Проверка, что объект Subtask нельзя сделать своим же эпиком")
    @Test
    public void testSubtaskCannotBeEpic() {
        SubTask subTask = new SubTask(1, "Subtask", NEW, "Subtask description", 1);
        subTask.setEpicId(2);
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
                subTask.setEpicId(subTask.getId()), "Subtask не может быть добавлен как его собственный Epic");
        Assertions.assertEquals("Subtask не может быть добавлен как его собственный Epic.", thrown.getMessage());
    }

    @DisplayName("Тест 2: Проверка, что InMemoryTaskManager добавляет задачи разного типа")
    @Test
    public void testInMemoryTaskManagerAddsDifferentTaskTypes() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task = new Task("Task", "Task Description", NEW, Duration.ofMinutes(10), LocalDateTime.now().plusHours(3));
        manager.createTask(task);
        Epic epic = new Epic(0, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createEpic(epic);
        SubTask subTask = new SubTask(5, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1), epic.getId());
        manager.createSubTask(subTask);

        // Проверка, что SubTask принадлежит Epic
        assertEquals(epic.getId(), subTask.getEpicId());

        // Проверка, что задачи добавлены в менеджер
        assertNotNull(manager.getTaskById(task.getId()), "Находим task по ID");
        assertNotNull(manager.getEpicById(epic.getId()), "Находим epic по ID");
        assertNotNull(manager.getSubTaskById(subTask.getId()), "Находим subtask по ID");
    }

    @DisplayName("Тест 3: Проверка, что задачи с заданным id и сгенерированным id не конфликтуют")
    @Test
    public void testNoIdConflictInTaskManager() {
        InMemoryTaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Task1", "Description 1");
        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.of(2024, 12, 1, 10, 0));

        Task task2 = new Task("Task1", "Description 2");
        task2.setDuration(Duration.ofMinutes(30));
        task2.setStartTime(LocalDateTime.of(2024, 12, 13, 10, 0));

        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> tasks = manager.getAllTasks();

        Assertions.assertEquals(2, tasks.size(), "Добавляем 2 tasks с одинаковым ID");
        assertTrue(tasks.contains(task1), "Task 1 проверяем наличие в списке");
        assertTrue(tasks.contains(task2), "Task 2 проверяем наличие в списке");
    }

    @DisplayName("Тест 4: на неизменность задачи при добавлении задачи в менеджер")
    @Test
    public void testTaskImmutabilityOnAdd() {
        Task originalTask = new Task("Task 1", "Description task 1");

        String originalName = originalTask.getName();
        String originalDescription = originalTask.getDescription();

        taskManager.createTask(originalTask);

        Assertions.assertEquals(originalName, originalTask.getName(), "Имя задачи изменилось!");
        Assertions.assertEquals(originalDescription, originalTask.getDescription(), "Описание задачи изменилось!");
        assertNotNull(taskManager.getTaskById(originalTask.getId()), "Задача не была добавлена в менеджер!");
    }

    @DisplayName("Тест 5: проверка, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;")
    @Test
    public void testUniqueIdConflict() {
        Task task1 = new Task("Задача 1", "Description task 1");
        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.of(2024, 12, 1, 10, 0));
        taskManager.createTask(task1);
        Integer existingId = taskManager.getTaskById(task1.getId()).getId();

        assertEquals(existingId, task1.getId(), "ID должен совпадать с присвоенным ID задачи 1");

        Task task2 = new Task(2, "Задача 2", "Description task 2");
        task2.setDuration(Duration.ofMinutes(30));
        task2.setStartTime(LocalDateTime.of(2024, 12, 3, 10, 0));
        taskManager.createTask(task2);

        assertNotEquals(task2.getId(), task1.getId(), "ID задачи 2 должен быть уникальным и не совпадать с ID задачи 1");

        Task task3 = new Task("Задача 3", "Описание задачи 3");
        task3.setId(existingId);

        assertEquals(task3.getId(), task1.getId(), "ID задачи 2 должен быть уникальным и не совпадать с ID задачи 1");

        Task task4 = new Task("Задача 4", "Описание задачи 4");
        task4.setDuration(Duration.ofMinutes(30));
        task4.setStartTime(LocalDateTime.of(2024, 11, 1, 11, 0));
        taskManager.createTask(task4);
        Integer newTask4Id = taskManager.getTaskById(task4.getId()).getId();

        assertNotNull(taskManager.getTaskById(newTask4Id), "Задача 4 должна быть добавлена и найдена по ID");
    }

    @DisplayName("Тест 6: что задача сохранена и возвращается корректный объект.")
    @Test
    void addNewTask() {
        Task task = new Task(1, "Test addNewTask", TaskStatus.NEW, "Test addNewTask Описание");
        taskManager.createTask(task);

        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        Assertions.assertEquals(task, savedTask, "Задачи не совпадают.");

        List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач.");
        Assertions.assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
        Assertions.assertEquals(TaskStatus.NEW, savedTask.getStatus(), "Статус задачи не совпадает!");
    }

    @DisplayName("Тест 7: Проверка, что эпик сохранен и возвращается правильный объект.")
    @Test
    void createNewEpic() {
        Epic epic = new Epic("Test Epic", "Test Epic Описание");
        taskManager.createEpic(epic);

        assertTrue(epic.getId() >= 0, "Неверный ID эпика.");

        Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        Assertions.assertEquals(epic, savedEpic, "Эпики не совпадают.");

        List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        Assertions.assertEquals(1, epics.size(), "Неверное количество эпиков.");
        Assertions.assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @DisplayName("Тест 8: Проверка, что подзадача сохранена и возвращается корректный объект.")
    @Test
    void createNewSubTask() {
        Epic epic = new Epic(0, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask = new SubTask(5, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1), epic.getId());

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);

        List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Подзадачи не возвращаются.");
        Assertions.assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        Assertions.assertEquals(subTask, subTasks.getFirst(), "Подзадачи не совпадают.");
    }

    @DisplayName("Тест 9: Проверка, что обновленная задача сохранена и совпадает с новой версией.")
    @Test
    void updateTask() {
        Task task = new Task("Test задачи", "Test описание задачи");
        taskManager.createTask(task);

        Task updatedTask = new Task(task.getId(), "Обновление описания", TaskStatus.IN_PROGRESS, "Обновление задачи");
        taskManager.updateTask(updatedTask);

        Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Обновленная задача не найдена.");
        Assertions.assertEquals(updatedTask, savedTask, "Задачи не совпадают после обновления.");
    }

    @DisplayName("Тест 10: Задача, удалена и в хранилище ее нет.")
    @Test
    void deleteTaskById() {
        Task task = new Task("Удаление задачи", "Описание");
        taskManager.createTask(task);

        taskManager.deleteTaskById(task.getId());

        // Проверяем, что задача успешно удалена
        assertFalse(taskManager.getAllTasks().contains(task), "Задача должна быть удалена.");
    }
}