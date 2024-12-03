package service;

import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private InMemoryHistoryManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryHistoryManager();
    }

    @Test
        //добавление 2 задач и проверка размера истории и наличия задач
    void testAddTask() {
        Task task1 = new Task(1, "Task 1", TaskStatus.NEW, "Description 1");
        Task task2 = new Task(2, "Task 2", TaskStatus.NEW, "Description 2");

        manager.add(task1);
        manager.add(task2);

        ArrayList<Task> history = manager.getHistory();

        assertEquals(2, history.size());
        assertTrue(history.contains(task1));
        assertTrue(history.contains(task2));
    }

    @Test
        //проверка удаления задач
    void testRemoveTask() {
        Task task1 = new Task(1, "Task 1", TaskStatus.NEW, "Description 1");
        Task task2 = new Task(2, "Task 2", TaskStatus.NEW, "Description 2");

        manager.add(task1);
        manager.add(task2);
        manager.remove(1);
        ArrayList<Task> history = manager.getHistory();

        assertEquals(1, history.size());
        assertFalse(history.contains(task1));
        assertTrue(history.contains(task2));
    }

    @Test
        //удаление несуществующей задачи
    void testRemoveNonExistentTask() {
        Task task1 = new Task(1, "Task 1", TaskStatus.NEW, "Description 1");

        manager.add(task1);
        manager.remove(2);

        ArrayList<Task> history = manager.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.contains(task1));
    }

    @Test
        //добавление дубликата задачи
    void testAddingDuplicateTaskUpdatesHistory() {
        Task task1 = new Task(1, "Task 1", TaskStatus.NEW, "Description 1");
        Task task2 = new Task(2, "Task 2", TaskStatus.NEW, "Description 2");
        Task task3 = new Task(3, "Task 3", TaskStatus.NEW, "Description 3");
        Task task4 = new Task(4, "Task 4", TaskStatus.NEW, "Description 4");

        manager.add(task1);
        manager.add(task2);
        manager.add(task1);
        manager.add(task4);
        manager.add(task2);
        manager.add(task3);
        manager.add(task2);

        ArrayList<Task> history = manager.getHistory();

        assertEquals(4, history.size());
        assertTrue(history.contains(task1));
        assertTrue(history.contains(task2));
        assertTrue(history.contains(task3));
        assertTrue(history.contains(task4));
    }

    @Test
        //очистка истории
    void testClearHistory() {
        Task task1 = new Task(1, "Task 1", TaskStatus.NEW, "Description 1");
        Task task2 = new Task(2, "Task 2", TaskStatus.NEW, "Description 2");

        manager.add(task1);
        manager.add(task2);
        manager.remove(1);
        manager.remove(2);

        ArrayList<Task> history = manager.getHistory();

        assertEquals(0, history.size());
    }
}