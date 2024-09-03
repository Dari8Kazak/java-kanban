package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class TestModel {

    @Test    // Тест 1: Проверка, что экземпляры класса Task равны друг другу, если равен их id
    public void testTaskEqualityById() {
        Task task1 = new Task("Task1", "Description 1");
        Task task2 = new Task("Task2", "Description 2");
        task1.setId(1);
        task2.setId(1);
        Assertions.assertEquals(task1, task2, "Task с одинаковым ID должны быть равны");
    }

    @Test    // Тест 2: Проверка, что наследники класса Task равны друг другу, если равен их id
    public void testSubtaskEqualityById() {
        Task epic = new Epic("Epic", "Epic description");
        SubTask subTask1 = new SubTask("Subtask1", "Subtask description", epic.getId());
        SubTask subTask2 = new SubTask("Subtask2", "Subtask description", epic.getId());
        subTask1.setId(1);
        subTask2.setId(1);
        Assertions.assertEquals(subTask1, subTask2, "SubTask с одинаковым ID должны быть равны");
    }

    @Test    // Тест 3: Проверка, что объект Epic нельзя добавить в самого себя в виде подзадачи
    public void testEpicCannotBeAssignedAsSubtask() {
        Epic epic = new Epic("Epic", "Epic description");
        new SubTask("Subtask", "Subtask description", epic.getId());

        assertThrows(IllegalArgumentException.class, () -> epic.addSubTaskId(epic.getId()), "Epic не может быть добавлен как его собственная подзадача");
    }
}