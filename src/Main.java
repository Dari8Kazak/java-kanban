import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.InMemoryTaskManager;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Создали задачу, Эпик");
        TaskManager manager = new InMemoryTaskManager();
//        1. создание 2 Task
        Task task1 = new Task("Пробежка", "Легкий бег");
        manager.createTask(task1);
        Task task2 = new Task("Упражнения", "ОФП");
        manager.createTask(task2);
//        2. создание Epic
        Epic epic1 = new Epic("Полумарафон", "21 км");
        manager.createEpic(epic1);
        Epic epic2 = new Epic("Марафон", "42 км");
        manager.createEpic(epic2);
//        3. создание SubTask
        SubTask subTask1 = new SubTask("Подготовка", "Медленный бег", epic1.getId());
        manager.createSubTask(subTask1);
        subTask1.setEpicId(epic1.getId());
        manager.createSubTask(subTask1);

        SubTask subTask2 = new SubTask("Экипировка", "купить кроссовки", epic2.getId());
        manager.createSubTask(subTask2);
        subTask2.setEpicId(epic1.getId());
        manager.createSubTask(subTask2);

        SubTask subTask3 = new SubTask("Подведение", "Диета", epic1.getId());
        manager.createSubTask(subTask3);
        subTask3.setEpicId(epic1.getId());
        manager.createSubTask(subTask3);

        System.out.println("Обновили задачу, Эпик");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());

//        4. обновление Task
        manager.updateTask(new Task(0, "Легкий бег с нагрузкой", TaskStatus.IN_PROGRESS, "Пробежка"));
        manager.updateTask(new Task(1, "интервалы", TaskStatus.IN_PROGRESS, "Упражнения"));
//        5. обновление Epic
        manager.updateEpic(new Epic(2, "Полумарафон", "мед. справка"));
        manager.updateEpic(new Epic(3, "Марафон", "регистрация"));
//        6. обновление SubTask
//        manager.updateSubTask(new SubTask(2, "Обновление 1", TaskStatus.DONE, "Подготовка", 4));
//        manager.updateSubTask(new SubTask(2, "Обновление 1", TaskStatus.IN_PROGRESS, "Экипировка", 5));
//        manager.updateSubTask(new SubTask(3, "Обновление 1", TaskStatus.DONE, "Подведение", 6));
        System.out.println("Удалили задачу, Эпик");
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
//        7. удаление Task
        manager.deleteTaskById(1);
//        8. удаление Epic
        manager.deleteEpicById(3);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
    }
}