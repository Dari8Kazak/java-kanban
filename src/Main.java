import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.InMemoryTaskManager;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {

        TaskManager manager = getTaskManager();

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

        manager.updateTask(new Task(0, "Легкий бег с нагрузкой", TaskStatus.IN_PROGRESS, "Пробежка"));
        manager.updateTask(new Task(1, "интервалы", TaskStatus.IN_PROGRESS, "Упражнения"));
        manager.updateEpic(new Epic("Полумарафон", "мед. справка"));
        manager.updateEpic(new Epic("Марафон", "регистрация"));
        manager.updateSubTask(new SubTask(2, "Обновление 1", TaskStatus.DONE, "Подготовка", 4));
        manager.updateSubTask(new SubTask(2, "Обновление 1", TaskStatus.IN_PROGRESS, "Экипировка", 5));
        manager.updateSubTask(new SubTask(3, "Обновление 1", TaskStatus.DONE, "Подведение", 6));

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

        manager.deleteTaskById(1);
        manager.deleteEpicById(3);

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
    }

    private static TaskManager getTaskManager() {
        TaskManager manager = new InMemoryTaskManager();

        Task task1 = new Task("Пробежка", "Легкий бег");
        Task task2 = new Task("Упражнения", "ОФП");
        Epic epic1 = new Epic("Полумарафон", "21 км");
        Epic epic2 = new Epic("Марафон", "42 км");
        SubTask subTask1 = new SubTask("Подготовка", "Медленный бег", 2);
        SubTask subTask2 = new SubTask("Экипировка", "купить кроссовки", 2);
        SubTask subTask3 = new SubTask("Подведение", "Диета", 3);

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);
        manager.createSubTask(subTask3);
        return manager;
    }
}