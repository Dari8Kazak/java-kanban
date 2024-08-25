package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Model.Task;
import Model.Epic;
import Model.SubTask;
import Service.InMemoryHistoryManager;
import Service.HistoryManager;

import java.util.List;

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

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpicTask(epic1);
        manager.addSubTask(subTask1);

        List<Task> history = manager.getHistory();
        if (history.size() != 4) {
            throw new AssertionError("История должна содержать 4 задачи, но содержит " + history.size());
        }

        assertTrue(history.contains(task1), "История должна содержать задачу Пробежка");
        assertTrue(history.contains(task2), "История должна содержать задачу Упражнения");
        assertTrue(history.contains(epic1), "История должна содержать эпик Полумарафон");
        assertTrue(history.contains(subTask1), "История должна содержать подзадачу Подготовка");
    }
    @Test
    void testHistoryContainsNoMoreThanTenElements() {
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Задача " + i, "Описание" + i);
            manager.addTask(task);
        }

        List<Task> historyTask = manager.getHistory();
        if (historyTask.size() > 10) {
            throw new AssertionError("История содержит больше 10 элементов: " + historyTask.size());
        }
    }
}