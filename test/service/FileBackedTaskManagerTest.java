package service;

import exceptions.FileException;
import model.Epic;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static service.InMemoryTaskManager.getSubTasks;
import static service.InMemoryTaskManager.getTasks;

class FileBackedTaskManagerTest {
    private static File testFile;

    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        testFile = File.createTempFile("testFile", ".csv");
        manager = new FileBackedTaskManager(testFile);
    }

    @AfterEach
    public void tearDown() {
        getTasks().clear();
        getSubTasks().clear();
        testFile.deleteOnExit();
    }

    @Test
    public void testSaveEmptyFile() throws IOException {

        manager.save(testFile);

        try (BufferedReader br = new BufferedReader(new FileReader(testFile))) {

            String header = br.readLine();
            assertEquals("id,type,name,status,description,duration,startTime,epicId", header);
            assertNull(br.readLine());
            assertTrue(testFile.exists(), "Файл должен существовать после сохранения.");
        }
    }

    @Test
    public void testSaveMultipleTasks() {

        Task task1 = new Task(0, "Пробежка", "Легкий бег", TaskStatus.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.of(2024, 12, 1, 10, 0));
        Epic epic2 = new Epic(2, "Epic2", "Description epic2", TaskStatus.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 2, 10, 0));

        manager.createTask(task1);
        manager.createEpic(epic2);

        manager.save(testFile);

        assertEquals(1, InMemoryTaskManager.getTasks().size());
        assertEquals(1, InMemoryTaskManager.getEpicTasks().size());
    }

    @Test
    public void testLoadMultipleTasks() throws FileException {

        // Сохраняем несколько задач в файл
        Task task1 = new Task(0, "Name1", "Description1", TaskStatus.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.of(2024, 12, 1, 10, 0));
        Task task2 = new Task(1, "Name2", "Description2", TaskStatus.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.of(2024, 1, 1, 10, 0));
        Task task3 = new Task(2, "Name3", "Description2", TaskStatus.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.of(2025, 1, 1, 10, 0));
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        manager.save(testFile);

        FileBackedTaskManager loadedManager = new FileBackedTaskManager(testFile);

        FileBackedTaskManager.loadFromFile(testFile, loadedManager);

        // Проверяем, что все задачи загружены
        assertEquals(3, loadedManager.getPrioritizedTasks().size(), "Должно быть загружено 3 задачи.");
        assertTrue(loadedManager.getPrioritizedTasks().contains(task1), "Менеджер должен содержать Task 1.");
        assertTrue(loadedManager.getPrioritizedTasks().contains(task2), "Менеджер должен содержать Task 2.");
    }
}