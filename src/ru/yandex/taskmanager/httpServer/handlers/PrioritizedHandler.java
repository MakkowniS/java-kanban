package ru.yandex.taskmanager.httpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import ru.yandex.taskmanager.manager.TaskManager;
import ru.yandex.taskmanager.tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        try {
            List<Task> prioritizedTasks = manager.getPrioritizedTasks();
            System.out.println("Сортированный список получен");
            String json = gson.toJson(prioritizedTasks);

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
