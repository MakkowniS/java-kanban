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

    public TaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        try {
            String[] parts = exchange.getRequestURI().getPath().split("/");

            if (parts.length == 3) {
                int id = Integer.parseInt(parts[2]);
                Task task = manager.getTask(id);
                System.out.println("Задача получена");

                String responseString = gson.toJson(task);
                sendResponse(exchange, responseString, 200);
            } else {
                List<Task> tasks = manager.getTasks();
                System.out.println("Задачи получены");
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
            if (requestBody.isBlank()) {
                sendResponse(exchange, "Пустое тело запроса.", 400);
                return;
            }

            Task task = gson.fromJson(requestBody, Task.class);
            System.out.println("Задача получена.");
            if (task.getId() != 0) {
                manager.updateTask(task);
                System.out.println("Задача успешно обновлена.");
                sendResponse(exchange, "Задача успешно обновлена.", 200);
            } else {
                manager.createTask(task);
                System.out.println("Задача успешно создана.");
                sendResponse(exchange, "Задача успешно создана.", 201);
            }
        } catch (IllegalArgumentException e) {
            sendHasIntersection(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (IOException e) {
            sendInternalServerError(exchange);
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {
        try {
            String[] parts = exchange.getRequestURI().getPath().split("/");
            if (parts.length == 3) {
                int id = Integer.parseInt(parts[2]);
                manager.removeTask(id);
                System.out.println("Задача удалена.");
                sendResponse(exchange, "Задача успешно удалена.", 200);
            } else if (parts.length < 3){
                manager.clearAllTasks();
                System.out.println("Все задачи удалены.");
                sendResponse(exchange, "Задачи удалены.", 200);
            } else {
                sendResponse(exchange, "Некорректный запрос.", 400);
            }
        } catch (IOException e) {
            sendInternalServerError(exchange);
        }

    }
}

