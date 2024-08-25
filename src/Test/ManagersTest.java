package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import Service.InMemoryTaskManager;
import Service.HistoryManager;
import Service.Managers;
import Service.TaskManager;
import Service.InMemoryHistoryManager;

class ManagersTest {

    @Test //  Тест 1: проверяем, что что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров
    void testGetDefaultTaskManager() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        assertNotNull(taskManager);
        assertInstanceOf(InMemoryTaskManager.class, taskManager);
    }
    @Test //  Тест 2: проверяем, что что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров
    void testGetDefaultHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
        assertInstanceOf(InMemoryHistoryManager.class, historyManager);
    }
}