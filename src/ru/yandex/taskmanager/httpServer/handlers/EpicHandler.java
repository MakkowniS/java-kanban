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



    public EpicHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    protected void handleGetRequest(HttpExchange exchange) throws IOException {
        try {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");

            if (pathParts.length == 3) {
                int id = Integer.parseInt(pathParts[2]);
                Epic epic = manager.getEpic(id);
                System.out.println("Эпик получен.");
                String json = gson.toJson(epic);

                sendResponse(exchange, json, 200);
            } else if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                int id = Integer.parseInt(pathParts[2]);
                List<Subtask> subtasks = manager.getSubtasksByEpicId(id);
                System.out.println("Список подзадач эпика получен.");
                String json = gson.toJson(subtasks);

                sendResponse(exchange, json, 200);
            } else if (pathParts.length < 3) {
                List<Epic> epics = manager.getEpics();
                System.out.println("Список эпиков получен.");
                String json = gson.toJson(epics);

                sendResponse(exchange, json, 200);
            } else {
                sendResponse(exchange, "Неверный запрос для данного метода", 400);
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
            Epic epic = gson.fromJson(requestBody, Epic.class);
            if (epic.getId() != 0) {
                manager.updateEpic(epic);
                System.out.println("Эпик обновлён.");
                sendResponse(exchange, "Эпик успешно обновлён.", 200);
            } else {
                manager.createEpic(epic);
                System.out.println("Эпик создан.");
                sendResponse(exchange, "Эпик успешно создан.", 201);
            }
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
                manager.removeEpic(id);
                System.out.println("Эпик удалён.");

                sendResponse(exchange, "Эпик успешно удалён.", 200);
            } else if (pathParts.length < 3) {
                manager.clearAllEpics();
                System.out.println("Эпики удалены.");
                sendResponse(exchange, "Эпики успешно удалены.", 200);
            } else {
                sendResponse(exchange, "Некорректный запрос.", 400);
            }
        } catch (IOException e) {
            sendInternalServerError(exchange);
        }
    }

}
