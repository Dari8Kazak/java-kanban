package service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
        //  Тест 1: проверяем, что что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров
    void testGetDefaultTaskManager() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        assertNotNull(taskManager);
        assertInstanceOf(InMemoryTaskManager.class, taskManager);
    }

    @Test
        //  Тест 2: проверяем, что что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров
    void testGetDefaultHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
        assertInstanceOf(InMemoryHistoryManager.class, historyManager);
    }
}