package ru.yandex.taskmanager.httpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.taskmanager.manager.TaskManager;
import ru.yandex.taskmanager.tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.manager = taskManager;
        this.gson = gson;
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        try {
            List<Task> tasks = manager.getHistory();
            System.out.println("История получена.");
            String json = gson.toJson(tasks);

            sendResponse(exchange, json, 200);
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "Метод для данного ресурса не поддерживается.", 405);
    }

    @Override
    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {
        sendResponse(exchange, "Метод для данного ресурса не поддерживается.", 405);
    }

}
