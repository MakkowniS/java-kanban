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

    private final TaskManager manager;
    private final Gson gson;

    public SubtaskHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        try {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            if (pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                Subtask subtask = manager.getSubtask(id);

                String json = gson.toJson(subtask);
                sendResponse(exchange, json, 200);
            } else {
                List<Subtask> subtasks = manager.getSubtasks();

                String json = gson.toJson(subtasks);
                sendResponse(exchange, json, 200);
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    @Override
    protected void handlePostRequest(HttpExchange exchange) throws IOException {
        try {
            String requestBody = new String(exchange.getRequestBody().readAllBytes());
            if (requestBody.equals("")) {
                sendResponse(exchange, "Некорректное тело запроса.", 400);
                return;
            }
            Subtask subtask = gson.fromJson(requestBody, Subtask.class);

            if (subtask.getId() != 0) {
                manager.updateSubtask(subtask);
                sendResponse(exchange, "Подзадача успешно обновлена.", 200);
            } else {
                manager.createSubtask(subtask);
                sendResponse(exchange, "Подзадача успешно создана.", 201);
            }
        } catch (IllegalArgumentException e) {
            sendHasIntersection(exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String[]  pathParts = exchange.getRequestURI().getPath().split("/");

        if  (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            manager.removeSubtask(id);
            sendResponse(exchange, "Подзадача успешно удалена.", 200);
        } else {
            sendResponse(exchange, "Неверный запрос для данного метода.", 400);
        }
    }

}
