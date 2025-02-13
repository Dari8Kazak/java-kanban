package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import service.TaskManager;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class TaskHandler extends server.handlers.BaseHttpHandler {

    protected final TaskManager manager;
    protected final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange h) {
        try {
            String requestMethod = h.getRequestMethod();
            String requestPath = h.getRequestURI().getPath();
            String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            if (Pattern.matches("/tasks/", requestPath) ||
                    Pattern.matches("/tasks", requestPath)) {
                switch (requestMethod) {
                    case "GET":
                        sendText(h, taskListSerialize(manager.getAllTasks()), 200);
                        break;
                    case "POST":
                        if (body.isEmpty() || body.isBlank()) {
                            sendText(h, "request body is empty", 400);
                        } else if (!body.contains("\"id\"")) {
                            addTask(h, body);
                        } else {
                            updateTask(h, body);
                        }
                        break;
                }
            } else if (Pattern.matches("/tasks/\\d+", requestPath) || Pattern.matches("/tasks/\\d+/", requestPath)) {
                Optional<Integer> id = getId(requestPath);
                if (id.isPresent()) {
                    switch (requestMethod) {
                        case "GET" -> getTask(h, id.get());
                        case "DELETE" -> deleteTask(h, id.get());
                    }
                }
            } else {
                sendText(h, "Unknown request", 404);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while handle task request", e);
        }
    }

    private void addTask(HttpExchange h, String body) {
        try {
            Task task = (gson.fromJson(body, Task.class));
            manager.createTask(task);
            Integer id = task.getId();

            Task createdTask = manager.getTaskById(id);

            if (Objects.nonNull(createdTask)) {
                sendText(h, taskSerialize(createdTask), 201);
            } else {
                sendText(h, "Task time overlaps with existing tasks", 406);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while add task", e);
        }
    }

    private void updateTask(HttpExchange h, String body) {
        try {
            Task updatedTask = manager.updateTask(gson.fromJson(body, Task.class));
            if (Objects.nonNull(updatedTask)) {
                sendText(h, taskSerialize(updatedTask), 201);
            } else {
                sendText(h, "Task time overlaps with existing tasks", 406);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while update task", e);
        }
    }

    private void getTask(HttpExchange h, Integer taskId) {
        try {

            Task task = manager.getTaskById(taskId);

            if (Objects.isNull(task)) {
                sendText(h, "Task with id " + taskId + " is not exist", 404);
            } else {
                sendText(h, taskSerialize(task), 200);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while get task", e);
        }
    }

    private void deleteTask(HttpExchange h, Integer taskId) {
        try {
            Task delTask = manager.getTaskById(taskId);

            if (delTask != null) {
                manager.deleteTaskById(taskId);
                String response = "Successful remove task: " + "id: "
                        + delTask.getId() + ", type: " + delTask.getTaskType();
                sendText(h, response, 200);
            } else {
                sendText(h, "Task with id " + taskId + " does not exist", 404);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while delete task", e);
        }
    }

    protected String taskSerialize(Task task) {
        return gson.toJson(task);
    }

    protected String taskListSerialize(List<? extends Task> tasks) {
        return gson.toJson(tasks);
    }

    protected Optional<Integer> getId(String requestPath) {
        String[] pathParts = requestPath.split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}