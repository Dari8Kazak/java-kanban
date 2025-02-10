package HttpServiceTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http_server.HttpTaskServer;
import http_server.adapters.SubTaskAdapter;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SubTaskHandlerTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    HttpClient client;
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(SubTask.class, new SubTaskAdapter())
            .create();

    public SubTaskHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpics();
    }

    @Test
    public void addSubTask_shouldAddSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(3));
        SubTask subTask = new SubTask("SubTask1", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(5), epic.getId());

        String subJson = gson.toJson(subTask);
        manager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<SubTask> subTasksFromManager = manager.getAllSubTasks();

        assertNotNull(subTasksFromManager);
        assertEquals(1, subTasksFromManager.size());
        assertEquals("SubTask1", subTasksFromManager.getFirst().getName());
    }

    @Test
    public void updateSubTask_shouldUpdateSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(3));
        manager.createEpic(epic);

        SubTask subTask = new SubTask("SubTask1", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(5), epic.getId());
        manager.createSubTask(subTask);

        SubTask subUpd = new SubTask(1, "UPDATE", "Testing sub 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 0);
        String subJson = gson.toJson(subUpd);

        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<SubTask> subTasksFromManager = manager.getAllSubTasks();

        assertNotNull(subTasksFromManager);
        assertEquals(1, subTasksFromManager.size());
        assertEquals("UPDATE", subTasksFromManager.getFirst().getName());
    }

    @Test
    public void getSubTask_shouldGetSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(3));
        manager.createEpic(epic);

        SubTask subTask = new SubTask("SubTask1", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(5), epic.getId());
        manager.createSubTask(subTask);

        Task actualSub = manager.getSubTaskById(subTask.getId());

        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task jsonSub = gson.fromJson(response.body(), SubTask.class);
        assertEquals(actualSub, jsonSub);
    }

    @Test
    public void deleteSubTask_shouldRemoveSubTask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic1", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(3));
        manager.createEpic(epic);

        SubTask subTask = new SubTask("SubTask1", "Description", NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(5), epic.getId());
        manager.createSubTask(subTask);

        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        assertEquals(1, manager.getAllSubTasks().size());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllSubTasks().size());
    }

    @Test
    public void getSubTask_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteSubTask_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/subtasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void addSubTask_shouldNotAddSubTaskIntersection() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.createEpic(epic);

        SubTask sub1 = new SubTask("Sub 1", "Testing sub 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10), 0);
        SubTask sub2 = new SubTask("Sub 2", "Testing sub 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10), 0);
        manager.createSubTask(sub1);
        String subJson = gson.toJson(sub2);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    @Test
    public void updateTask_shouldNotUpdateSubTaskIntersection() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.createEpic(epic);

        SubTask sub1 = new SubTask("Sub 1", "Testing sub 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(5), epic.getId());
        SubTask sub2 = new SubTask(1, "UPDATE", "Testing sub 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(30), epic.getId());
        Task task = new Task("Test 2", "Testing task 2",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(50));

        manager.createSubTask(sub1);
        manager.createTask(task);

        String subJson = gson.toJson(sub2);

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void addTask_return400WithEmptyBody() throws IOException, InterruptedException {
        String taskJson = "";

        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }
}