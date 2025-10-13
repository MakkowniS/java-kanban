package ru.yandex.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.tasks.*;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.List;
import java.util.TreeSet;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager>{

    private TaskManager taskManager;

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @BeforeEach
    void setUp(){
        taskManager = createTaskManager();
    }

    @Test
    void shouldUpdateEpicStatusDependsOnSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Description", epic.getId());
        taskManager.createSubtask(subtask);
        Subtask subtask3 = new Subtask("Test NewSubtask", "Test NewSubtask description", epic.getId());
        taskManager.createSubtask(subtask3);

        assertEquals(StatusOfTask.NEW, taskManager.getEpic(subtask3.getEpicId()).getStatus(), "Изначальный статус эпика должен быть NEW");

        subtask.setStatus(StatusOfTask.DONE);
        subtask3.setStatus(StatusOfTask.DONE);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask3);

        assertEquals(StatusOfTask.DONE, taskManager.getEpic(subtask3.getEpicId()).getStatus(), "Статус эпика не DONE");

        subtask.setStatus(StatusOfTask.NEW);
        taskManager.updateSubtask(subtask);

        assertEquals(StatusOfTask.IN_PROGRESS, taskManager.getEpic(subtask3.getEpicId()).getStatus(), "Статус эпика не IN_PROGRESS");

        subtask.setStatus(StatusOfTask.IN_PROGRESS);
        subtask3.setStatus(StatusOfTask.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask3);

        assertEquals(StatusOfTask.IN_PROGRESS, taskManager.getEpic(subtask3.getEpicId()).getStatus(), "Статус эпика не IN_PROGRESS");
    }

    @Test
    void shouldThrowExceptionWhenTasksOverlap() {
        Task task1 = new Task("Task1", "desc");
        task1.setDuration(Duration.ofMinutes(60));
        taskManager.createTask(task1);

        Task task2 = new Task("Task2", "desc");
        task2.setStartTime(task1.getStartTime().plusMinutes(30)); // пересечение
        task2.setDuration(Duration.ofMinutes(60));

        assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void shouldReturnTasksSorted() {
        Task task1 = new Task("Task1", "desc");
        task1.setDuration(Duration.ofMinutes(60));
        taskManager.createTask(task1);

        Task task2 = new Task("Task2", "desc");
        task2.setStartTime(task1.getStartTime().plusMinutes(90));
        task2.setDuration(Duration.ofMinutes(60));
        taskManager.createTask(task2);

        TreeSet<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(List.of(task1, task2), List.copyOf(prioritized));

    }
}