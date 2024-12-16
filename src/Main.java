import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static model.TaskStatus.NEW;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();

        //        1. создание 2 Task
        System.out.println("Создали 2 задачи");

        Task task1 = new Task("Task1", "Task Description", NEW, Duration.ofMinutes(10), LocalDateTime.now().plusHours(1));
        Task task2 = new Task("Task2", "Task Description", NEW, Duration.ofMinutes(10), LocalDateTime.now().plusHours(2));

        manager.createTask(task1);
        manager.createTask(task2);

        System.out.println(manager.getAllTasks());

//        2. создание Epic
        System.out.println("Создали 2 Эпика");

        Epic epic1 = new Epic(3, "Epic1", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(3));
        Epic epic2 = new Epic(4, "Epic2", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(4));
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        System.out.println(manager.getAllEpics());

//        3. создание SubTask
        System.out.println("Создали 3 подзадачи");

        SubTask subTask1 = new SubTask(4, "SubTask1", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(5), epic1.getId());
        SubTask subTask2 = new SubTask(5, "SubTask2", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(6), epic2.getId());
        SubTask subTask3 = new SubTask(6, "SubTask3", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(7), epic1.getId());

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);

        System.out.println(manager.getAllSubTasks());

        System.out.println("Все подзадачи эпика 1");
        System.out.println(manager.getAllEpicSubTasks(epic1));

        System.out.println("Все подзадачи эпика 2");
        System.out.println(manager.getAllEpicSubTasks(epic2));

//        4. обновление Task
        task1.setStatus(TaskStatus.DONE);
        task2.setStatus(TaskStatus.IN_PROGRESS);

        System.out.println("Обновили задачу 1,2");
        System.out.println(manager.getAllTasks());

//        5. обновление Epic
        epic1.setDescription("мед. справка");
        epic2.setDescription("заминка");

        System.out.println("Обновили Эпик 1, 2");
        System.out.println(manager.getAllEpics());


//        6. обновление SubTask
        subTask1.setDescription("This new description");
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        subTask3.setName("обновление 1");

        System.out.println("обновление SubTask 1,2,3");
        System.out.println(manager.getAllSubTasks());

//        7. удаление Task
        System.out.println("Удалили Task1");
        manager.deleteTaskById(task1.getId());
        System.out.println(manager.getAllTasks());

//        8. удаление SubTask
        System.out.println("Удалили subTask1");
        manager.deleteSubTaskById(subTask1.getId());
        System.out.println(manager.getAllSubTasks());

//        9. удаление Epic
        System.out.println("Удалили Epic2");
        manager.deleteEpicById(epic2.getId());
        System.out.println(manager.getAllEpics());

    }
}