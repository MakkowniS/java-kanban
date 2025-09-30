package ru.yandex.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.tasks.Epic;
import ru.yandex.taskmanager.tasks.Subtask;
import ru.yandex.taskmanager.tasks.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    FileBackedTaskManager manager;
    File file;
    Task task;
    Epic epic;
    Subtask subtask;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        file.deleteOnExit();
        manager = new FileBackedTaskManager(file);

    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        task = new Task("Task1", "Description1");
        manager.createTask(task);
        manager.removeTask(task.getId());

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loaded.getTasks().isEmpty());
        assertTrue(loaded.getSubtasks().isEmpty());
        assertTrue(loaded.getEpics().isEmpty());
    }

    @Test
    void shouldSaveAndLoadTasks() {
        task = new Task("Task1", "Description1");
        manager.createTask(task);
        epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        subtask = new Subtask("Subtask1", "Description1", epic.getId());
        manager.createSubtask(subtask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(manager.getTasks(), loaded.getTasks());
        assertEquals(manager.getSubtasks(), loaded.getSubtasks());
        assertEquals(manager.getEpics(), loaded.getEpics());
    }
}