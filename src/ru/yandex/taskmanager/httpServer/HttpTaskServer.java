package ru.yandex.taskmanager.httpServer;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.taskmanager.httpServer.handlers.*;
import ru.yandex.taskmanager.manager.Managers;
import ru.yandex.taskmanager.manager.TaskManager;
import ru.yandex.taskmanager.utility.GsonUtil;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private final int serverPort = 8080;
    private final TaskManager manager;
    private final Gson gson;
    private HttpServer httpServer;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
        this.gson = GsonUtil.getGson(); // Получаем gson, который может сериализовать Duration и LocalDateTime
    }

    public void startServer() {
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(serverPort), 0);

            httpServer.createContext("/tasks", new TaskHandler(manager, gson));
            httpServer.createContext("/subtasks", new SubtaskHandler(manager, gson));
            httpServer.createContext("/epics", new EpicHandler(manager, gson));
            httpServer.createContext("/history", new HistoryHandler(manager, gson));
            httpServer.createContext("/prioritized", new PrioritizedHandler(manager, gson));

            httpServer.start();
            System.out.println("Сервер запущен на порту: " + serverPort);
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время запуска сервера. " + e.getMessage());
        }

    }

    public void stopServer() {
        System.out.println("Сервер остановлен.");
        httpServer.stop(0);
    }

    public Gson getGson() {
        return gson;
    }

    public static void main(String[] args) throws IOException {
    }
}
