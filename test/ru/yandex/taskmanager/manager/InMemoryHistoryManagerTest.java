package ru.yandex.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;
    private static Task task;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Test Task", "Test Description");
    }

    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }

    @Test
    void shouldDeleteFirstIfHistorySize11() {
        historyManager.add(task);
        for (int i = 2; i <= 11; i++) {
            Task newTask = new Task("Test Task" + i, "Test Description" + i);
            historyManager.add(newTask);
        }
        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "История выходит за указанный лимит.");
    }

    @Test
    void historyMustKeepPreviousVersionOfTask() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Test Task", "Test Description");
        taskManager.createTask(task);
        taskManager.getTask(task.getId());
        task.setName("New Name");
        task.setDescription("New Description");
        taskManager.updateTask(task);
        Task historyTask = taskManager.getHistory().get(0);

        assertEquals("Test Task", historyTask.getName(), "Должно сохраниться исходное имя.");
        assertEquals("Test Description", historyTask.getDescription(), "Должно сохраниться исходное описание.");
    }

}