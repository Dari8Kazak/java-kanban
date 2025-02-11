package server;

import com.sun.net.httpserver.HttpServer;
import server.handlers.*;
import service.HistoryManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) throws IOException {
        HistoryManager historyManager = manager.getHistoryManager();
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        setContextHandlers(manager, historyManager);
    }

    public static void main(String[] args) {
        try {
            TaskManager manager = Managers.getDefault();
            HttpTaskServer server = new HttpTaskServer(manager);
            server.start();
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    private void setContextHandlers(TaskManager manager, HistoryManager historyManager) {
        httpServer.createContext("/tasks", new TaskHandler(manager));
        httpServer.createContext("/subtasks", new SubTaskHandler(manager));
        httpServer.createContext("/epics", new EpicHandler(manager));
        httpServer.createContext("/history", new HistoryHandler(historyManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
    }

    public void start() {
        httpServer.start();
        System.out.println("Server started " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Server stopped");
    }
}