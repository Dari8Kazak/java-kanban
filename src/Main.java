import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        Task task1 = new Task("Пробежка", "Легкий бег");
        Task task2 = new Task("Упражнения", "ОФП");
        Epic epic1 = new Epic("Полумарафон", "21 км");
        Epic epic2 = new Epic("Марафон", "42 км");
        SubTask subTask1 = new SubTask("Подготовка", "Медленный бег", 2);
        SubTask subTask2 = new SubTask("Экипировка", "купить кроссовки", 2);
        SubTask subTask3 = new SubTask("Подведение", "Диета", 3);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

        manager.updateTask(new Task("Пробежка", "Легкий бег с нагрузкой", TaskStatus.IN_PROGRESS, 0));
        manager.updateTask(new Task("Упражнения", "интервалы", TaskStatus.IN_PROGRESS, 1));
        manager.updateEpic(new Epic("Полумарафон", "мед. справка", 2));
        manager.updateEpic(new Epic("Марафон", "регистрация", 3));
        manager.updateSubTask(new SubTask("Подготовка", "Обновление 1", TaskStatus.IN_PROGRESS, 2, 4));
        manager.updateSubTask(new SubTask("Экипировка", "Обновление 1", TaskStatus.IN_PROGRESS, 2, 5));
        manager.updateSubTask(new SubTask("Подведение", "Обновление 1", TaskStatus.DONE, 3, 6));

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

        manager.deleteTaskById(1);
        manager.deleteEpicById(3);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
    }
}