package ru.yandex.taskmanager.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldBeEqualsIfHasEqualsId() {
        Task task = new Task("Test Task", "Test Description");
        Task task2 = new Task("Test Task2", "Test Description2");
        task2.setId(task.getId());
        assertEquals(task, task2, "Задачи не равны");
    }

}