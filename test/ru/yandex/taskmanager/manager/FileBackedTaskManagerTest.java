package ru.yandex.taskmanager.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.tasks.Epic;
import ru.yandex.taskmanager.tasks.Subtask;
import ru.yandex.taskmanager.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    @Test
    void shouldSaveAndLoadEmptyManager() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loaded.getTasks().isEmpty());
        assertTrue(loaded.getSubtasks().isEmpty());
        assertTrue(loaded.getEpics().isEmpty());
    }

    @Test
    void shouldSaveTasks() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task = new Task("Task1", "Description1");
        manager.createTask(task);
        Epic epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Description1", epic.getId());
        manager.createSubtask(subtask);

        manager.save();
        String content = Files.readString(file.toPath());

        assertTrue(content.contains("Task1"));
        assertTrue(content.contains("Epic1"));
        assertTrue(content.contains("Subtask1"));
    }

    @Test
    void shouldLoadTasks() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task = new Task("Task1", "Description1");
        manager.createTask(task);
        Epic epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Description1", epic.getId());
        manager.createSubtask(subtask);

        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);
        assertEquals(1, loaded.getTasks().size());
        assertEquals(1, loaded.getEpics().size());
        assertEquals(1, loaded.getSubtasks().size());

        Epic savedEpic = loaded.getEpic(epic.getId());
        assertEquals(subtask.getId(), savedEpic.getSubtasksId().getFirst());
    }
}