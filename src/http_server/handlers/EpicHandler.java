package http_server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import http_server.adapters.EpicAdapter;
import http_server.adapters.SubTaskAdapter;
import model.Epic;
import model.SubTask;
import model.Task;
import service.TaskManager;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class EpicHandler extends TaskHandler {

    protected final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Epic.class, new EpicAdapter())
            .create();

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange h) {
        try {
            String requestMethod = h.getRequestMethod();
            String requestPath = h.getRequestURI().getPath();
            String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            if (Pattern.matches("/epics/", requestPath) || Pattern.matches("/epics", requestPath)) {
                switch (requestMethod) {
                    case "GET":
                        sendText(h, epicListSerialize(manager.getAllEpics()), 200);
                        break;
                    case "POST":
                        if (body.isEmpty() || body.isBlank()) {
                            sendText(h, "request body is empty", 400);
                        } else if (!body.contains("\"id\"")) {
                            addEpic(h, body);
                        } else {
                            updateEpic(h, body);
                        }
                        break;
                }

            } else if (Pattern.matches("/epics/\\d+", requestPath)
                    || Pattern.matches("/epics/\\d+/", requestPath)) {
                Optional<Integer> id = getId(requestPath);
                if (id.isPresent()) {
                    switch (requestMethod) {
                        case "GET" -> getEpic(h, id.get());
                        case "DELETE" -> deleteEpic(h, id.get());
                    }
                }

            } else if (Pattern.matches("/epics/\\d+/subtasks", requestPath)
                    || Pattern.matches("/epics/\\d+/subtasks/", requestPath)) {
                Optional<Integer> id = getId(requestPath);
                if (id.isPresent()) {
                    if ("GET".equals(requestMethod)) {
                        getEpicSubtasks(h, id.get());
                    }
                }

            } else {
                sendText(h, "Unknown request", 404);
            }

        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while handle epic request", e);
        }
    }

    private void addEpic(HttpExchange h, String body) {
        try {
            Epic epic = (gson.fromJson(body, Epic.class));
            manager.createEpic(epic);
            Integer id = epic.getId();
            Epic createdEpic = manager.getEpicById(id);

            if (Objects.nonNull(createdEpic)) {
                sendText(h, epicSerialize(createdEpic), 201);
            } else {
                sendText(h, "Epic is null", 404);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while add epic", e);
        }
    }

    private void updateEpic(HttpExchange h, String body) {
        try {
            Epic updatedEpic = manager.updateEpic(gson.fromJson(body, Epic.class));
            if (Objects.nonNull(updatedEpic)) {
                sendText(h, epicSerialize(updatedEpic), 201);
            } else {
                sendText(h, "Epic id does not exist", 404);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while update epic", e);
        }
    }

    private void getEpic(HttpExchange h, Integer epicId) {
        try {
            Epic epic = manager.getEpicById(epicId);
            if (Objects.isNull(epic)) {
                sendText(h, "Epic with id " + epicId + " is not exist", 404);
            } else {
                sendText(h, epicSerialize(epic), 200);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while get epic", e);
        }
    }

    private void getEpicSubtasks(HttpExchange h, int epicId) {
        try {
            Epic epic = manager.getEpicById(epicId);
            if (Objects.isNull(epic)) {
                sendText(h, "Epic with id " + epicId + " does not exist", 404);
            } else {
                sendText(h, subtaskListSerialize(manager.getAllEpicSubTasks(epic)), 200);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while getting epic subtasks", e);
        }
    }

    private void deleteEpic(HttpExchange h, Integer epicId) {
        try {
            Epic delEpic = manager.getEpicById(epicId);
            if (delEpic != null) {

                manager.deleteEpicById(epicId);

                String response = "Successful remove epic: " + "id: "
                        + delEpic.getId() + ", type: " + delEpic.getTaskType();
                sendText(h, response, 200);
            } else {
                sendText(h, "Task with id " + epicId + " does not exist", 404);
            }
        } catch (Exception e) {

            sendInternalError(h);
            logger.log(Level.SEVERE, "error while delete epic", e);
        }
    }

    protected String epicSerialize(Epic epic) {
        return gson.toJson(epic);
    }

    protected String epicListSerialize(List<? extends Task> epics) {
        return gson.toJson(epics);
    }

    protected String subtaskListSerialize(List<? extends Task> subs) {
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapter(SubTask.class, new SubTaskAdapter())
                .create();
        return gson.toJson(subs);
    }
}