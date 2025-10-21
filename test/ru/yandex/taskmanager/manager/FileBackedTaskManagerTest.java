package ru.yandex.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.exceptions.ManagerSaveException;
import ru.yandex.taskmanager.tasks.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File file;
    FileBackedTaskManager manager;
    Task task;
    Epic epic;
    Subtask subtask;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            file = File.createTempFile("tasks", ".csv");
            file.deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new FileBackedTaskManager(file);
    }

    @BeforeEach
    void setUp() {
        manager = createTaskManager();
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
        task.setStartTimeNow();
        task.setDuration(Duration.ofMinutes(20));
        manager.createTask(task);
        epic = new Epic("Epic1", "Description1");
        manager.createEpic(epic);
        subtask = new Subtask("Subtask1", "Description1", epic.getId());
        subtask.setStartTime(task.getStartTime().plusMinutes(20));
        subtask.setDuration(Duration.ofMinutes(30));
        manager.createSubtask(subtask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(file);

        assertEquals(manager.getTasks(), loaded.getTasks());
        assertEquals(manager.getSubtasks(), loaded.getSubtasks());
        assertEquals(manager.getEpics(), loaded.getEpics());
    }

    @Test
    void shouldThrowExceptionIfNotExistingFile() {
        File fakefile = new File("fake.csv");

        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(fakefile), "При попытке загрузки несуществующего файла ожидается исключение");
    }

    @Test
    void shouldNotThrowExceptionIsFileExists() {
        assertDoesNotThrow(() -> {
            File file = File.createTempFile("tasks", ".csv");
            FileBackedTaskManager manager = new FileBackedTaskManager(file);
        }, "Создание FileBackedManager с нормальным файлом не должно выбрасывать исключение");
    }

    @Test
    void shouldNotThrowWhenSavingToAccessibleFile() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task = new Task("Test Task", "Desc");
        manager.createTask(task);

        assertDoesNotThrow(manager::save, "Сохранение в доступный файл не должно кидать исключений");
    }

    @Test
    void shouldThrowWhenFileIsNotWritable() {
        File readOnlyFile = new File("/root/protected.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(readOnlyFile);

        Task task = new Task("Task", "Desc");

        assertThrows(ManagerSaveException.class, () -> manager.createTask(task), "Ожидается исключение при записи в недоступный файл");
    }


}