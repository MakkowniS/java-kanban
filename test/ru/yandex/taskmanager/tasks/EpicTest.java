package ru.yandex.taskmanager.tasks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class EpicTest {

    @Test
    void shouldBeEqualsIfHasEqualsIdTaskExtends() {
        Epic epic = new Epic("Эпик 3", "Описание 3");
        Epic epic2 = new Epic("Эпик 3", "Описание 3");
        epic.setId(1);
        epic2.setId(1);
        assertEquals(epic, epic2, "Эпики не равны");
    }

    @Test
    void epicCannotBeItsOwnSubtask() {
        Epic epic = new Epic("Test Epic", "Test Description");

        epic.addSubtaskId(epic.getId());
        assertFalse(epic.getSubtasksId().contains(epic.getId()),
                "Эпик не должен ссылаться сам на себя как на подзадачу");
    }
}