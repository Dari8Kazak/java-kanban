package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class PrioritizedHandler extends BaseHttpHandler {

    protected final TaskManager manager;
    protected final Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange h) {
        try {
            String requestMethod = h.getRequestMethod();
            String requestPath = h.getRequestURI().getPath();

            if (Pattern.matches("/prioritized/", requestPath) || Pattern.matches("/prioritized", requestPath)) {
                if ("GET".equals(requestMethod)) {
                    sendText(h, taskListSerialize(manager.getPrioritizedTasks()), 200);

                } else {
                    sendText(h, "Unknown request", 404);
                }
            }
        } catch (Exception e) {
            sendInternalError(h);
            logger.log(Level.SEVERE, "error while handle prioritized request", e);
        }
    }

    private String taskListSerialize(TreeSet<Task> prioritizedTasks) {
        return gson.toJson(prioritizedTasks);
    }
}