package ru.yandex.taskmanager.httpServer.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.taskmanager.exceptions.NotFoundException;
import ru.yandex.taskmanager.manager.TaskManager;
import ru.yandex.taskmanager.tasks.Epic;
import ru.yandex.taskmanager.tasks.Subtask;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {

    private final TaskManager manager;
    private final Gson gson;

    public EpicHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        try {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");

            if (pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                Epic epic = manager.getEpic(id);
                String json = gson.toJson(epic);

                sendResponse(exchange, json, 200);
            } else if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                int id = Integer.parseInt(pathParts[2]);
                List<Subtask> subtasks = manager.getSubtasksByEpicId(id);
                String json = gson.toJson(subtasks);

                sendResponse(exchange, json, 200);
            } else if (pathParts.length < 3) {
                List<Epic> epics = manager.getEpics();
                String json = gson.toJson(epics);

                sendResponse(exchange, json, 200);
            } else {
                sendResponse(exchange, "Неверный запрос для данного метода", 400);
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
            Epic epic = gson.fromJson(requestBody, Epic.class);
            manager.createEpic(epic);

            sendResponse(exchange, "Эпик успешно создан.", 200);
        } catch (IllegalArgumentException e) {
            sendHasIntersection(exchange);
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    @Override
    protected void handleDeleteRequest(HttpExchange exchange) throws IOException {
        try {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");

            if (pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                manager.removeEpic(id);
                sendResponse(exchange, "Эпик успешно удалён.", 200);
            } else {
                sendResponse(exchange, "Неверный запрос для данного метода.", 400);
            }

        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

}
