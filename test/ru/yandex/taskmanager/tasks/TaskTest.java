package ru.yandex.taskmanager.tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.manager.Managers;
import ru.yandex.taskmanager.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private static TaskManager taskManager;

    @BeforeEach
    void creatingDefaultTasks() {
        taskManager = Managers.getDefault();

    }

    @Test
    void shouldBeEqualsIfHasEqualsId() {
        Task task = new Task("Test Task", "Test Description");
        Task task2 = new Task("Test Task2", "Test Description2");
        task2.setId(task.getId());
        assertEquals(task, task2, "Задачи не равны");
    }

    @Test
    void taskWithManualAndAutoIdShouldNotConflict(){
        Task manualTask = new Task("Test Task", "Test Description");
        manualTask.setId(50);
        taskManager.createTask(manualTask);
        Task autoTask = new Task("Test Task2", "Test Description2");
        taskManager.createTask(autoTask);

        assertNotNull(taskManager.getTask(manualTask.getId()), "Задача с ручным Id не добавлена.");
        assertNotNull(taskManager.getTask(autoTask.getId()), "Задача с авто Id не добавлена.");

        assertNotEquals(taskManager.getTask(manualTask.getId()), taskManager.getTask(autoTask.getId()), "Id " +
                " должны быть уникальны");
    }

    @Test
    void taskFieldsShouldNotChangeAfterAdding(){
        Task newTask = new Task("Test Task", "Test Description");
        String newTaskName = newTask.getName(); // Получаем поля задачи до добавления
        String newTaskDescription = newTask.getDescription();
        StatusOfTask newTaskStatus = newTask.getStatus();
        taskManager.createTask(newTask);

        Task createdTask = taskManager.getTask(1);
        assertEquals(newTaskName, createdTask.getName(), "Название новой задачи и добавленной не совпадает.");
        assertEquals(newTaskDescription, createdTask.getDescription(), "Описание новой задачи и добавленной не совпадает.");
        assertEquals(newTaskStatus, createdTask.getStatus(), "Статус новой задачи и добавленной не совпадает.");
    }
}