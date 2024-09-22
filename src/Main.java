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

        manager.updateTask(new Task("Пробежка", "Легкий бег с нагрузкой", TaskStatus.IN_PROGRESS, 1));
        manager.updateTask(new Task("Упражнения", "интервалы", TaskStatus.IN_PROGRESS, 2));
        manager.updateEpic(new Epic("Полумарафон", "мед. справка", 3));
        manager.updateEpic(new Epic("Марафон", "регистрация", 4));

        manager.updateSubTask(new SubTask("Подготовка", "Обновление 1", TaskStatus.DONE, 3, 5));
        manager.updateSubTask(new SubTask("Экипировка", "Обновление 1", TaskStatus.IN_PROGRESS, 3, 6));
        manager.updateSubTask(new SubTask("Подведение", "Обновление 1", TaskStatus.DONE, 4, 7));

        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());

        manager.removeTaskById(1);
        manager.removeEpicById(3);

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
        SubTask subTask1 = new SubTask("Подготовка", "Медленный бег", 3);
        SubTask subTask2 = new SubTask("Экипировка", "купить кроссовки", 3);
        SubTask subTask3 = new SubTask("Подведение", "Диета", 4);

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);
        return manager;
    }
}
