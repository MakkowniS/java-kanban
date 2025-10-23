package ru.yandex.taskmanager.httpServer;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.exceptions.NotFoundException;
import ru.yandex.taskmanager.manager.Managers;
import ru.yandex.taskmanager.manager.TaskManager;
import ru.yandex.taskmanager.tasks.Epic;
import ru.yandex.taskmanager.tasks.Subtask;
import ru.yandex.taskmanager.tasks.Task;
import ru.yandex.taskmanager.utility.GsonListTypes;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    private final TaskManager manager = Managers.getDefault();

    private final HttpTaskServer taskServer = new HttpTaskServer(manager);
    private final Gson gson = taskServer.getGson();
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    public void setup() {
        manager.clearAllTasks();
        manager.clearAllSubtasks();
        manager.clearAllEpics();
        taskServer.startServer();

        task = new Task("Test", "Test Desc");
        task.setDuration(Duration.ofMinutes(5));
        task.setStartTimeNow();
        manager.createTask(task);
        epic = new Epic("Test", "Test Desc");
        manager.createEpic(epic);
        subtask = new Subtask("Test Subtask", "Test Desc", epic.getId());
        subtask.setDuration(Duration.ofMinutes(5));
        subtask.setStartTime(task.getStartTime().plusMinutes(5));
        manager.createSubtask(subtask);

    }

    @AfterEach
    public void shutdown() {
        taskServer.stopServer();
    }

    // Обработчик Task
    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task newTask = new Task("Test", "Test Desc");
        String taskJson = gson.toJson(newTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task newTask = new Task("New Test", "New Desc");
        newTask.setDuration(Duration.ofMinutes(5));
        newTask.setStartTime(task.getStartTime());
        newTask.setId(task.getId());
        String taskJson = gson.toJson(newTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(1, manager.getTasks().size(), "Некорректное количество задач.");
        assertEquals(newTask.getName(), manager.getTasks().getFirst().getName(), "Некорректное имя задачи.");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromServer = gson.fromJson(response.body(), GsonListTypes.TASKS_LIST);
        assertNotNull(tasksFromServer, "Задачи не возвращаются");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task taskFromServer = gson.fromJson(response.body(), Task.class);
        assertEquals(taskFromServer, manager.getTask(task.getId()), "Возвращается некорректная задача.");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertThrows(NotFoundException.class, () -> manager.getTask(task.getId()), "Задача не удалена.");
    }

    @Test
    public void shouldThrowNotFoundExceptionOnTask() throws IOException, InterruptedException {
        manager.removeTask(task.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionOnTask() throws IOException, InterruptedException {
        Task newTask = new Task("New Test", "New Desc");
        newTask.setDuration(Duration.ofMinutes(10));
        newTask.setStartTimeNow();

        String taskJson = gson.toJson(newTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    // Обработчик Subtask
    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Subtask newSubtask = new Subtask("New", "Test Desc", epic.getId());

        String taskJson = gson.toJson(newSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getSubtasks();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(2, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("New", subtasksFromManager.get(1).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Subtask newSubtask = new Subtask("New", "New Desc", epic.getId());
        newSubtask.setDuration(Duration.ofMinutes(10));
        newSubtask.setStartTime(task.getStartTime().plusMinutes(10));
        newSubtask.setId(subtask.getId());
        String taskJson = gson.toJson(newSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertEquals(1, manager.getTasks().size(), "Некорректное количество задач.");
        assertEquals(newSubtask.getName(), manager.getSubtasks().getFirst().getName(), "Некорректное имя задачи.");
    }

    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromServer = gson.fromJson(response.body(), GsonListTypes.SUBTASKS_LIST);
        assertNotNull(subtasksFromServer, "Задачи не возвращаются");
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" +  subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask subtaskFromServer = gson.fromJson(response.body(), Subtask.class);
        assertEquals(subtaskFromServer, manager.getSubtask(subtask.getId()), "Возвращается некорректная задача.");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        assertThrows(NotFoundException.class, () -> manager.getSubtask(subtask.getId()), "Задача не удалена.");
    }

    @Test
    public void shouldThrowNotFoundExceptionOnSubtask() throws IOException, InterruptedException {
        manager.removeSubtask(subtask.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionOnSubtask() throws IOException, InterruptedException {
        Subtask newSubtask = new Subtask("New Test", "New Desc", epic.getId());
        newSubtask.setDuration(Duration.ofMinutes(10));
        newSubtask.setStartTimeNow();

        String subtaskJson = gson.toJson(newSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());
    }

    // Обработчик Epic
    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("New", "New Desc");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        assertEquals(2, manager.getEpics().size(), "Количество эпиков некорректное.");
        assertEquals(epic.getName(), manager.getEpics().get(1).getName(), "Имя эпика некорректное.");
    }
}
