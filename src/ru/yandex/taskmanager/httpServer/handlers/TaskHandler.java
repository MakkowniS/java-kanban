package ru.yandex.taskmanager.httpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.taskmanager.exceptions.NotFoundException;
import ru.yandex.taskmanager.manager.TaskManager;
import ru.yandex.taskmanager.tasks.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public TaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;

    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        try {
            String[] parts = exchange.getRequestURI().getPath().split("/");

            if (parts.length == 3) {
                int id = Integer.parseInt(parts[2]);
                System.out.println("→ Пытаюсь получить задачу с id=" + id);
                Task task = manager.getTask(id);
                System.out.println("← Получена задача: " + task);

                String responseString = gson.toJson(task);
                sendResponse(exchange, responseString, 200);
            } else {
                List<Task> tasks = manager.getTasks();
                String jsonResponse = gson.toJson(tasks);
                sendResponse(exchange, jsonResponse, 200);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (IOException e) {
            sendInternalServerError(exchange);
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            if (requestBody.equals("")) {
                sendResponse(exchange, "Некорректное тело запроса.", 400);
                return;
            }

            Task task = gson.fromJson(requestBody, Task.class);
            if (task.getId() != 0) {
                manager.updateTask(task);
                sendResponse(exchange, "Задача успешно обновлена.", 200);
            } else {
                manager.createTask(task);
                sendResponse(exchange, "Задача успешно создана.", 201);
            }
        } catch (IllegalArgumentException e) {
            sendHasIntersection(exchange);
        } catch (IOException e) {
            sendInternalServerError(exchange);
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        if (parts.length == 3) {
            int id = Integer.parseInt(parts[2]);
            manager.removeTask(id);
            sendResponse(exchange, "Задача успешно удалена.", 200);
        } else {
            sendResponse(exchange, "Неверный запрос для данного метода.", 400);
        }
    }
}

