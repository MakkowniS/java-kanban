package ru.yandex.taskmanager.httpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.taskmanager.exceptions.NotFoundException;
import ru.yandex.taskmanager.manager.TaskManager;
import ru.yandex.taskmanager.tasks.Subtask;

import java.io.IOException;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        try {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            if (pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                Subtask subtask = manager.getSubtask(id);
                System.out.println("Задача получена.");

                String json = gson.toJson(subtask);
                sendResponse(exchange, json, 200);
            } else {
                List<Subtask> subtasks = manager.getSubtasks();
                System.out.println("Список задач получен");

                String json = gson.toJson(subtasks);
                sendResponse(exchange, json, 200);
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
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            if (requestBody.isBlank()) {
                sendResponse(exchange, "Пустое тело запроса.", 400);
                return;
            }
            Subtask subtask = gson.fromJson(requestBody, Subtask.class);
            System.out.println("Подзадача получена.");

            if (subtask.getId() != 0) {
                manager.updateSubtask(subtask);
                System.out.println("Подзадача обновлена.");
                sendResponse(exchange, "Подзадача успешно обновлена.", 200);
            } else {
                manager.createSubtask(subtask);
                System.out.println("Подзадача создана.");
                sendResponse(exchange, "Подзадача успешно создана.", 201);
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
            String[] pathParts = exchange.getRequestURI().getPath().split("/");

            if (pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                manager.removeSubtask(id);
                System.out.println("Подзадача удалена.");
                sendResponse(exchange, "Подзадача успешно удалена.", 200);
            } else if (pathParts.length < 3) {
                manager.clearAllSubtasks();
                System.out.println("Подзадачи удалены");
                sendResponse(exchange, "Подзадачи удалены.", 200);
            } else {
                sendResponse(exchange, "Неверный запрос для данного метода.", 400);
            }
        } catch (IOException e) {
            sendInternalServerError(exchange);
        }


    }

}
