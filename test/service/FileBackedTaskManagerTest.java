//package service;
//
//import exceptions.FileException;
//import model.Task;
//import model.TaskStatus;
//import org.junit.jupiter.api.*;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.time.Duration;
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class FileBackedTaskManagerTest {
//
//    private FileBackedTaskManager manager;
//    private File testFile;
//
//    @BeforeEach
//    public void setUp() {
//        testFile = new File(System.getProperty("user.home") + File.separator + "test_register.csv");
//        manager = new FileBackedTaskManager(testFile);
//    }
//
////    @AfterEach
////    public void tearDown() {
////        if (testFile.exists()) {
//////            testFile.delete();
////        }
////    }
////
////    @Test
////    public void testSaveEmptyFile() throws IOException {
////        manager.save(); // Сохраняем пустой менеджер
////        assertTrue(testFile.exists(), "Файл должен существовать после сохранения.");
////        assertEquals(0, Files.readAllLines(testFile.toPath()).size(), "Файл должен быть пустым.");
////    }
////
//        @Test
//    public void testSaveMultipleTasks() throws IOException {
//        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
//        Task task2 = new Task(2, "Task 2", "Description 2", TaskStatus.IN_PROGRESS, Duration.ofMinutes(60), LocalDateTime.now());
//        manager.createTask(task1);
//        manager.createTask(task2);
//
//        manager.save(); // Сохраняем задачи
//
//        // Проверяем, что файл не пустой и содержит задачи
////        assertTrue(testFile.exists(), "Файл должен существовать после сохранения.");
////        assertTrue(Files.readAllLines(testFile.toPath()).size() > 1, "Файл должен содержать задачи.");
//    }
//////
////    @Test
////    public void testLoadMultipleTasks() throws FileException {
////        // Сохраняем несколько задач в файл
////        Task task1 = new Task(1, "Task 1", "Description 1");
////        task1.setDuration(Duration.ofMinutes(30));
////        task1.setStartTime(LocalDateTime.of(2024, 12, 1, 10, 0));
////        manager.createTask(task1);
////        manager.save();
////        Task task2 = new Task(2, "Task 2", "Description 2");
////        task2.setDuration(Duration.ofMinutes(30));
////        task2.setStartTime(LocalDateTime.of(2024, 11, 1, 10, 0));
////        manager.createTask(task2);
////        manager.save();
////
//////        // Создаем новый менеджер для загрузки из файла
////        FileBackedTaskManager loadedManager = (FileBackedTaskManager) FileBackedTaskManager.loadFromFile(testFile);
////
////        // Проверяем, что все задачи загружены
////        assertEquals(2, loadedManager.getPrioritizedTasks().size(), "Должно быть загружено 2 задачи.");
////        assertTrue(loadedManager.getPrioritizedTasks().contains(task1), "Менеджер должен содержать Task 1.");
////        assertTrue(loadedManager.getPrioritizedTasks().contains(task2), "Менеджер должен содержать Task 2.");
////    }
//}
