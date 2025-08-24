package ru.yandex.taskmanager.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

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
        Subtask subtask = new Subtask("Test Subtask", "Test Description", 1);
        subtask.setId(1);

        assertNotEquals(subtask.getId(), subtask.getEpicId(), "Subtask не может быть своим же Epic");
    }

    @Test
    void shouldSetAndReturnEpicId() {
        Subtask subtask = new Subtask("Test Subtask", "Test Description", 1);

        assertEquals(1,  subtask.getEpicId(), "Epic ID не возвращается");

        subtask.setEpicId(2);

        assertEquals(2,  subtask.getEpicId(), "Epic ID не изменяется.");

    }
}