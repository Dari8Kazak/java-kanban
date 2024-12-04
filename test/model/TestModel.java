package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestModel {

    @Test    // Тест 1: Проверка, что экземпляры класса Task равны друг другу, если равен их id
    public void testTaskEqualityById() {
        Task task1 = new Task("Task1", "Description 1");
        Task task2 = new Task("Task2", "Description 2");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Task с одинаковым ID должны быть равны");
    }

    @Test    // Тест 2: Проверка, что наследники класса Task равны друг другу, если равен их id
    public void testSubtaskEqualityById() {
        Task epic = new Epic("Epic", "Epic description");
        SubTask subTask1 = new SubTask("Subtask1", "Subtask description", epic.getId());
        SubTask subTask2 = new SubTask("Subtask2", "Subtask description", epic.getId());
        subTask1.setId(1);
        subTask2.setId(1);
        assertEquals(subTask1, subTask2, "SubTask с одинаковым ID должны быть равны");
    }

    @Test
    void testEpicCannotBeAddedAsSubTaskToItself() {
        // Создаем объект Epic
        Epic epic = new Epic("Epic Task", "Description of the epic");
        int epicId = epic.getId(); // Получаем ID эпика

        // Создаем подзадачу с тем же ID, что и у эпика
        SubTask subTask = new SubTask("SubTask", "Description of the subtask", epicId);

        // Проверяем на выброс исключения
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            subTask.setEpicId(epicId);
        });

        assertEquals("Subtask не может быть добавлен как его собственный Epic.", thrown.getMessage());
    }
}

