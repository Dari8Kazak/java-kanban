package httpServer.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import httpServer.adapters.SubTaskAdapter;
import model.SubTask;
import model.Task;
import service.TaskManager;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class SubTaskHandler extends TaskHandler {

    protected final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(SubTask.class, new SubTaskAdapter())
            .create();

    public SubTaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange h) {
        try {
            String requestMethod = h.getRequestMethod();
            String requestPath = h.getRequestURI().getPath();
            String body = new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            if (Pattern.matches("/subtasks/", requestPath) || Pattern.matches("/subtasks", requestPath)) {
                switch (requestMethod) {
                    case "GET":
                        sendText(h, subTaskListSerialize(manager.getAllSubTasks()), 200);
                        break;
                    case "POST":
                        if (body.isEmpty() || body.isBlank()) {
                            sendText(h, "request body is empty", 400);
                        } else if (!body.contains("\"id\"")) {
                            addSubTask(h, body);
                        } else {
                            updateSubTask(h, body);
                        }
                        break;
                }

            } else if (Pattern.matches("/subtasks/\\d+", requestPath)
                    || Pattern.matches("/subtasks/\\d+/", requestPath)) {
                Optional<Integer> id = getId(requestPath);
                if (id.isPresent()) {
                    switch (requestMethod) {
                        case "GET" -> getSubTask(h, id.get());
                        case "DELETE" -> deleteSubTask(h, id.get());
                    }
                }

            } else {
                sendText(h, "Unknown request", 404);
            }

        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while handle subtask request", e);
        }
    }

    private void addSubTask(HttpExchange h, String body) {
        try {
            SubTask subTask = (gson.fromJson(body, SubTask.class));
            manager.createSubTask(subTask);
            Integer id = subTask.getId();

            SubTask addedSubTask = manager.getSubTaskById(id);

            if (Objects.nonNull(addedSubTask)) {
                sendText(h, subTaskSerialize(addedSubTask), 201);
            } else {
                sendText(h, "Epic does not exist or subtask time overlaps with existing tasks", 406);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while add subtask", e);
        }
    }

    private void updateSubTask(HttpExchange h, String body) {
        try {
            SubTask subTask = (gson.fromJson(body, SubTask.class));
            manager.updateSubTask(subTask);
            Integer id = subTask.getId();

            SubTask updatedSubTask = manager.getSubTaskById(id);

            if (Objects.nonNull(updatedSubTask)) {
                sendText(h, subTaskSerialize(updatedSubTask), 201);
            } else {
                sendText(h, "Subtask id does not exist or time overlaps with existing tasks", 406);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while update subtask", e);
        }
    }

    private void getSubTask(HttpExchange h, Integer subTaskId) {
        try {
            SubTask subTask = manager.getSubTaskById(subTaskId);
            if (Objects.isNull(subTask)) {
                sendText(h, "Subtask with id " + subTaskId + " is not exist", 404);
            } else {
                sendText(h, subTaskSerialize(subTask), 200);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while get subtask", e);
        }
    }

    private void deleteSubTask(HttpExchange h, Integer subId) {
        try {
            SubTask delSub = manager.getSubTaskById(subId);
            if (delSub != null) {
                manager.deleteSubTaskById(subId);

                String response = "Successful remove subtask: " + "id: "
                        + delSub.getId() + ", type: " + delSub.getTaskType();
                sendText(h, response, 200);
            } else {
                sendText(h, "Subtask with id " + subId + " does not exist", 404);
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while remove subtask", e);
        }
    }

    protected String subTaskSerialize(SubTask subTask) {
        return gson.toJson(subTask);
    }

    protected String subTaskListSerialize(List<? extends Task> subs) {
        return gson.toJson(subs);
    }
}