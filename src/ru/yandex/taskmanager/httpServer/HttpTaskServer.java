package ru.yandex.taskmanager.httpServer;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.taskmanager.httpServer.handlers.*;
import ru.yandex.taskmanager.manager.Managers;
import ru.yandex.taskmanager.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int serverPort = 8080;
    private static TaskManager manager;
    private static Gson gson;

    public HttpTaskServer() {
        manager = Managers.getDefault();
        gson = new Gson();
    }

    public void startServer() throws IOException {
        HttpServer httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(serverPort), 0);

        httpServer.createContext("/tasks", new TaskHandler(manager, gson));
        httpServer.createContext("/subtasks", new SubtaskHandler(manager, gson));
        httpServer.createContext("/epics", new EpicHandler(manager, gson));
        httpServer.createContext("/history", new HistoryHandler(manager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(manager, gson));

        httpServer.start();
        System.out.println("Server started on port " + serverPort);
    }

    public static void main(String[] args) throws IOException {
        new HttpTaskServer().startServer();
    }
}
