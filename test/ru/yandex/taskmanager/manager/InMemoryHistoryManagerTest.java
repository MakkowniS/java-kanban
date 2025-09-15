package ru.yandex.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;
    private static Task task1;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Test Task", "Test Description");
        task1.setId(1);
    }

    @Test
    void add() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }


    @Test
    void historyMustKeepPreviousVersionOfTask() {
        historyManager.add(task1);
        task1.setName("New Name Task");
        Task taskInHistory = historyManager.getHistory().get(0);
        assertEquals("Test Task", taskInHistory.getName(), "Должно сохраниться исходное имя.");
    }

    @Test
    void remove() {
        historyManager.add(task1);
        assertNotNull(historyManager.getHistory(), "История не должна быть пустой.");

        historyManager.remove(task1.getId());
        assertEquals(0, historyManager.getHistory().size(), "Задача должна удалиться из истории.");
    }

}