package ru.yandex.taskmanager.tasks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.manager.Managers;
import ru.yandex.taskmanager.manager.TaskManager;

class EpicTest {

    private static TaskManager taskManager;

    @BeforeEach
    void creatingDefaultTasks() {
        taskManager = Managers.getDefault();

    }

    @Test
    void shouldBeEqualsIfHasEqualsIdTaskExtends() {
        Epic epic = new Epic("Эпик 3", "Описание 3");
        Epic epic2 = new Epic("Эпик 3", "Описание 3");
        epic.setId(epic2.getId());
        assertEquals(epic, epic2, "Эпики не равны");
    }

    @Test
    void epicCannotBeItsOwnSubtask() {
        Epic epic = new Epic("Test Epic", "Test Description");
        taskManager.createEpic(epic);

        assertTrue(epic.getSubtasksId().isEmpty()); // У эпика нет подзадач

        Subtask subtask = new Subtask("Test Subtask", "Test Description", epic.getId());
        taskManager.createSubtask(subtask);

        assertFalse(epic.getSubtasksId().contains(epic.getId()));
    }
}