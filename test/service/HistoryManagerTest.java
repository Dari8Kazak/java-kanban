package service;

import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryManagerTest {
    private HistoryManager manager;

    @BeforeEach
    public void setUp() {
        manager = new InMemoryHistoryManager();
    }

    @Test
    public void testHistory() {

        Task task1 = new Task("Пробежка", "Легкий бег");
        Task task2 = new Task("Упражнения", "ОФП");
        Epic epic1 = new Epic("Полумарафон", "21 км");
        SubTask subTask1 = new SubTask("Подготовка", "Медленный бег", epic1.getId());

        manager.add(task1);
        manager.add(task2);
        manager.add(epic1);
        manager.add(subTask1);

        List<Task> history = manager.getHistory();

        assertEquals(4, history.size(), "История должна содержать 4 задачи");

        assertTrue(history.contains(task1), "История должна содержать задачу Пробежка");
        assertTrue(history.contains(task2), "История должна содержать задачу Упражнения");
        assertTrue(history.contains(epic1), "История должна содержать эпик Полумарафон");
        assertTrue(history.contains(subTask1), "История должна содержать подзадачу Подготовка");
    }

    @Test
    void testHistoryContainsNoMoreThanTenElements() {
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Задача " + i, "Описание" + i);
            manager.add(task);
        }

        List<Task> historyTask = manager.getHistory();
        assertEquals(10, historyTask.size(), "История должна содержать не более 10 элементов");
    }
}