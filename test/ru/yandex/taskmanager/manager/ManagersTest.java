package ru.yandex.taskmanager.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void managersShouldReturnWorkingTaskManager() {
        TaskManager testTaskManager = Managers.getDefault();

        assertNotNull(testTaskManager, "Managers,getDefault не должен возвращать null");

        Task task = new Task("Test Task", "Test Description");
        testTaskManager.createTask(task);

        Task createdTask = testTaskManager.getTask(task.getId());
        assertEquals(task, createdTask, "Работающий TaskManager должен создавать и возвращать Task");
    }

    @Test
    void managersShouldReturnWorkingHistoryManager() {
        HistoryManager testHistoryManager = Managers.getDefaultHistory();

        assertNotNull(testHistoryManager, "Managers,getDefaultHistory не должен возвращать null");

        Task task = new Task("Test Task", "Test Description");
        task.setId(1);
        testHistoryManager.add(task);
        List<Task> history = testHistoryManager.getHistory();

        assertEquals(task, history.get(0), "Задача не добавилась в историю.");

    }
}