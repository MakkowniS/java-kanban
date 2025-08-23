package ru.yandex.taskmanager.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.manager.Managers;
import ru.yandex.taskmanager.manager.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    private static TaskManager taskManager;

    @BeforeEach
    void creatingDefaultTasks() {
        taskManager = Managers.getDefault();

    }

    @Test
    void shouldBeEqualsIfHasEqualsIdTaskExtends() {
        Epic epic = new Epic("Эпик 3", "Описание 3");
        Subtask subtask = new Subtask("Подзадача 3", "Описание 3", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 3", "Описание 3", epic.getId());
        subtask.setId(subtask2.getId());
        assertEquals(subtask, subtask2, "Подзадачи не равны");
    }

    @Test
    void subtaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Test Epic", "Test Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Test Description", epic.getId() + 1);
        taskManager.createSubtask(subtask);

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertFalse(subtasks.contains(subtask)); // Проверка на наличие Subtask, которая является своим эпиком в таблице
    }
}