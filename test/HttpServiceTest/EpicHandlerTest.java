package HttpServiceTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import httpServer.HttpTaskServer;
import httpServer.adapters.EpicAdapter;
import httpServer.adapters.SubTaskAdapter;
import model.Epic;
import model.SubTask;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EpicHandlerTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    HttpClient client;

    Gson epicGson = new GsonBuilder()
            .registerTypeAdapter(Epic.class, new EpicAdapter())
            .create();

    Gson subtaskGson = new GsonBuilder()
            .registerTypeAdapter(SubTask.class, new SubTaskAdapter())
            .create();

    public EpicHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubTasks();
        manager.deleteAllEpics();
        taskServer.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test //Добавить Epic
    public void addEpic_shouldAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String epicJson = epicGson.toJson(epic);

        System.out.println(epicJson);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager);
        assertEquals(1, epicsFromManager.size());
        assertEquals("Epic 1", epicsFromManager.getFirst().getName());
    }

    @Test
    public void updateEpic_shouldUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.createEpic(epic);

        Epic updEpic = new Epic(0, "UPDATE", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String epicJson = epicGson.toJson(updEpic);

        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();

        assertNotNull(epicsFromManager);
        assertEquals(1, epicsFromManager.size());
        assertEquals("UPDATE", epicsFromManager.getFirst().getName());
    }

    @Test // Получение Epic
    public void getEpic_shouldGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.createEpic(epic);
        Epic actualEpic = manager.getEpicById(epic.getId());

        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Epic jsonEpic = epicGson.fromJson(response.body(), Epic.class);
        assertEquals(actualEpic, jsonEpic);
    }

    @Test //Удаление Epic
    public void deleteEpic_shouldRemoveEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        assertEquals(1, manager.getAllEpics().size());
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getAllEpics().size());
    }

    @Test //Взять Epic вернется ошибка 404
    public void getEpic_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteEpic_shouldReturn404() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void addEpic_return400WithEmptyBody() throws IOException, InterruptedException {
        String taskJson = "";

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
    }

    @Test
    public void getEpicSubtasks_shouldGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Testing epic 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        SubTask subtask = new SubTask("Subtask 1", "Testing subtask 1",
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(30), 0);
        manager.createEpic(epic);
        manager.createSubTask(subtask);
        Integer id = subtask.getId();
        SubTask actualSubtask = manager.getSubTaskById(id);

        URI url = URI.create("http://localhost:8080/epics/0/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<SubTask> subs = subtaskGson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        assertEquals(actualSubtask, subs.getFirst());
    }
}

class SubtaskListTypeToken extends TypeToken<List<SubTask>> {
}