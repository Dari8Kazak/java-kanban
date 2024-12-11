import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Создали задачу, Эпик");
        TaskManager manager = new InMemoryTaskManager();
//        1. создание 2 Task
        Task task1 = new Task("Пробежка", "Легкий бег");

        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.of(2024, 12, 1, 10, 0));
        manager.createTask(task1);

        Task task2 = new Task("Упражнения", "ОФП");
        task2.setDuration(Duration.ofMinutes(30));
        task2.setStartTime(LocalDateTime.of(2024, 12, 2, 10, 0));
        manager.createTask(task2);
//        2. создание Epic
        Epic epic1 = new Epic("Полумарафон", "21 км");
        epic1.setDuration(Duration.ofMinutes(30));
        epic1.setStartTime(LocalDateTime.of(2024, 12, 1, 10, 0));
        manager.createEpic(epic1);

        Epic epic2 = new Epic("Марафон", "42 км");
        epic2.setDuration(Duration.ofMinutes(30));
        epic2.setStartTime(LocalDateTime.of(2024, 12, 1, 10, 0));
        manager.createEpic(epic2);

//        3. создание SubTask
        SubTask subTask1 = new SubTask("Подготовка", "Медленный бег", epic1.getId());
        subTask1.setEpicId(epic1.getId());
        subTask1.setDuration(Duration.ofMinutes(30));
        subTask1.setStartTime(LocalDateTime.of(2024, 12, 1, 10, 0));
        manager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("Экипировка", "купить кроссовки", epic2.getId());
        subTask2.setEpicId(epic1.getId());
        subTask2.setDuration(Duration.ofMinutes(30));
        subTask2.setStartTime(LocalDateTime.of(2024, 12, 1, 10, 0));
        manager.createSubTask(subTask2);

        SubTask subTask3 = new SubTask("Подведение", "Диета", epic1.getId());

        subTask3.setDuration(Duration.ofMinutes(30));
        subTask3.setStartTime(LocalDateTime.of(2024, 12, 1, 10, 0));
        subTask3.setEpicId(epic1.getId());
        manager.createSubTask(subTask3);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

//        4. обновление Task
        task1.setStatus(TaskStatus.DONE);
        task2.setStatus(TaskStatus.IN_PROGRESS);

//        5. обновление Epic
        epic1.setDescription("мед. справка");
        epic1.setDescription("заминка");

////        6. обновление SubTask
        subTask1.setDescription("This new description");
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        subTask3.setName("обновление 1");

        System.out.println("Обновили задачу, Эпик");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());


//        7. удаление Task
        manager.deleteTaskById(1);
//        8. удаление Epic
        manager.deleteEpicById(3);
        //        8. удаление SubTask
        manager.deleteSubTaskById(subTask1.getId());

        System.out.println("Удалили задачу, Эпик");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

    }
}