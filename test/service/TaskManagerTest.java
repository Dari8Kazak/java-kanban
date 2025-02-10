package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static model.TaskStatus.*;
import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @DisplayName("Успешное создание задачи")
    @Test
    void addNewTask_whenTaskIsValid_shouldAddSuccessfully() {
        Task task = new Task("Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createTask(task);
        assertEquals(task, taskManager.getTaskById(task.getId()));
    }

    @DisplayName("Успешное обновление задачи")
    @Test
    void updateTask_whenPutNewTask_shouldBeUpdated() {
        Task task = new Task(0, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createTask(task);
        task.setDescription("Updated Task");
        task.setStatus(IN_PROGRESS);
        taskManager.updateTask(task);
        Task taskById = taskManager.getTaskById(0);
        assertEquals("Updated Task", taskById.getDescription());
    }

    @DisplayName("Успешное удаление задачи по id синхронно с историей")
    @Test
    void deleteTaskById_whenTaskIsExist_shouldDeleteId() {
        initializeAndAddTasks();
        taskManager.getTaskById(0);
        taskManager.deleteTaskById(0);

        assertEquals(1, taskManager.getAllTasks().size(), "Неверное количество задач.");
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @DisplayName("Успешное удаление всех задач")
    @Test
    void deleteAllTasks_shouldDeleteAllTask() {
        initializeAndAddTasks();
        taskManager.getTaskById(0);
        taskManager.getTaskById(1);
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Задачи не удалились");
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @DisplayName("Успешное создание эпика")
    @Test
    void addNewEpic_whenTaskIsValid_shouldAddSuccessfully() {
        Epic epic = new Epic("Epic", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createEpic(epic);
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
    }

    @DisplayName("Успешное обновление Epic")
    @Test
    void updateEpic_shouldBeUpdate() {
        Epic epic = new Epic(0, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createEpic(epic);
        epic.setDescription("update epic");
        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpicById(0);

        assertEquals("update epic", updatedEpic.getDescription());
    }

    @DisplayName("Успешное обновление статуса у Epic на статус IN_PROGRESS")
    @Test
    void updateEpicStatus_whenSubtaskChangeStatus_shouldUpdateStatusInProgress() {
        Epic epic = new Epic("Epic", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 1, 0));
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 10, 0), 0);
        taskManager.createEpic(epic);
        SubTask subTask2 = new SubTask(2, "SubTask", "Description", DONE, Duration.ofMinutes(30), LocalDateTime.now(), 0);
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);
        taskManager.updateEpic(epic);
        assertEquals(IN_PROGRESS, epic.getStatus());
    }

    @DisplayName("Успешное удаление всех Epic и Subtask")
    @Test
    void deleteAllEpics() {
        Epic epic1 = new Epic(1, "Task 1", "Description 1", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Epic epic2 = new Epic(2, "Task 2", "Description 2", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        SubTask subTask1 = new SubTask(1, "SubTask 1", "Description 1", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(3), 1);
        SubTask subTask2 = new SubTask(2, "SubTask 2", "Description 2", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(2), 1);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        taskManager.deleteAllEpics();

        assertTrue(epic1.getSubtasks().isEmpty());
        assertTrue(taskManager.getAllSubTasks().isEmpty(), "Подзадачи не удалились");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпики не удалились");
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @DisplayName("Успешное удаление epic по id синхронно с историей")
    @Test
    void deleteEpicById() {
        Epic epic = new Epic(0, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1), 0);

        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);

        taskManager.deleteEpicById(epic.getId());

        assertTrue(epic.getSubtasks().isEmpty());
        assertEquals(0, taskManager.getAllEpics().size());
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @DisplayName("Успешное создание подзадачи")
    @Test
    void addNewSubTask_whenEpicIsCreated_shouldAddSuccessfully() {
        Epic epic = new Epic("Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.createEpic(epic);

        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1), 0);
        taskManager.createSubTask(subTask);

        SubTask savedSubTask = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(savedSubTask, "Подзадача не найдена.");
        assertEquals(subTask, savedSubTask, "Подзадачи не совпадают.");
        assertEquals(1, taskManager.getAllSubTasks().size(), "Неверное количество задач.");
    }

    @DisplayName("Успешное обновление подзадачи")
    @Test
    void updateSubTask_whenPutNewSubTask_shouldBeUpdated() {
        Epic epic = new Epic(0, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 28, 10, 0));
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 28, 11, 0), 0);
        taskManager.createEpic(epic);
        taskManager.createSubTask(subTask);
        subTask.setStatus(IN_PROGRESS);
        subTask.setDescription("Updated SubTask");

        SubTask updatedSubTask = taskManager.getSubTaskById(subTask.getId());

        assertEquals("Updated SubTask", updatedSubTask.getDescription());
    }

    @DisplayName("Успешное удаление подзадачи по id синхронно с историей")
    @Test
    void deleteSubTaskById_whenSubTaskIsExist_shouldDeleteId() {
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now(), 1);
        taskManager.createSubTask(subTask);

        taskManager.deleteSubTaskById(subTask.getId());

        assertEquals(0, taskManager.getAllSubTasks().size());
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @DisplayName("Успешное удаление всех подзадач")
    @Test
    void deleteAllSubTasks_shouldDeleteAllSubTask() {
        SubTask subTask = new SubTask(1, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now(), 1);
        SubTask subTask2 = new SubTask(2, "SubTask", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now(), 1);
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask2);
        taskManager.deleteAllSubTasks();

        assertTrue(taskManager.getAllSubTasks().isEmpty());
        assertTrue(taskManager.getHistory().isEmpty());
    }

    private void initializeAndAddTasks() {
        Task task = new Task(0, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task(1, "Task", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        taskManager.createTask(task);
        taskManager.createTask(task2);
    }
}
