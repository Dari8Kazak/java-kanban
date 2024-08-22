package Test;//package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.Task;
import model.Epic;
import model.SubTask;
import service.HistoryManager;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.util.List;

public class HistoryManagerTest {
    private TaskManager manager;

    @BeforeEach
    public void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    public void testHistory() {
        // Создаем задачи
        Task task1 = new Task("Пробежка", "Легкий бег");
        Task task2 = new Task("Упражнения", "ОФП");
        Epic epic1 = new Epic("Полумарафон", "21 км");
        SubTask subTask1 = new SubTask("Подготовка", "Медленный бег", epic1.getId());

        // Добавляем задачи
        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addSubTask(subTask1);

        // Получаем историю и проверяем размер
        List<Task> history = manager.getHistory();
        assertEquals(4, history.size(), "История должна содержать 4 задачи");


        // Проверяем, что в истории содержатся правильные задачи
        assertTrue(history.contains(task1), "История должна содержать задачу Пробежка");
        assertTrue(history.contains(task2), "История должна содержать задачу Упражнения");
//        assertTrue(history.contains(epic1), "История должна содержать эпик Полумарафон");
//        assertTrue(history.contains(subTask1), "История должна содержать подзадачу Подготовка");
    }
}
