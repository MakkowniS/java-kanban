package ru.yandex.taskmanager.manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.tasks.Epic;
import ru.yandex.taskmanager.tasks.Subtask;
import ru.yandex.taskmanager.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private static TaskManager taskManager;
    private static Task task1;
    private static Task task2;
    private static Epic epic1;
    private static Epic epic2;
    private static Subtask subtask1;
    private static Subtask subtask2;
    private static Subtask subtask3;

    @BeforeAll
    static void creatingDefaultTasks() {
        taskManager = Managers.getDefault();

    }

    @Test
    void shouldBeEqualsIfHasEqualsId() {
        Task task11 = new Task("Задача 3", "Описание 3");
        task11.setId(task1.getId());
        assertEquals(task11, task1, "Задачи не равны");
    }

    @Test
    void shouldBeEqualsIfHasEqualsIdTaskExtends() {
        Epic epic11 = new Epic("Эпик 3", "Описание 3");
        Subtask subtask11 = new Subtask("Подзадача 3", "Описание 3", epic11.getId());
        epic11.setId(epic1.getId());
        assertEquals(epic11, epic1, "Эпики не равны");
        subtask11.setId(subtask1.getId());
        assertEquals(subtask11, subtask1, "Подзадачи не равны");
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

    @Test
    void subtaskCannotBeItsOwnEpic(){
        Epic epic = new Epic("Test Epic", "Test Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Test Description", epic.getId());
        taskManager.createSubtask(subtask);




    }

    @Test
    void shouldAddNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("Test NewTask", "Test NewTask description");
        taskManager.createTask(task);
        Task updatedTask = new Task("Test updateTask", "Test updateTask description");
        updatedTask.setId(task.getId());
        assertEquals(updatedTask, task, "ID не совпадает.");

        taskManager.updateTask(updatedTask);
        final List<Task> tasks = taskManager.getTasks();

        assertEquals(tasks.get(0), updatedTask, "Обновлённая задача не заменила оригинальную.");

    }

    @Test
    void shouldRemoveTaskAndClearTasks() {
        Task task = new Task("Test Task", "Test description");
        Task task2 = new Task("Test Task2", "Test description2");

        taskManager.createTask(task);
        taskManager.createTask(task2);

        taskManager.removeTask(task.getId());
        assertNull(taskManager.getTask(task.getId()), "Задача не удалена.");

        taskManager.clearAllTasks();
        assertTrue(taskManager.getTasks().isEmpty(), "Список задач не очищен.");

    }

    @Test
    void shouldAddNewEpic() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        taskManager.createEpic(epic);
        final int epicId = epic.getId();
        final Epic savedEpic = taskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Эпик не возвращается.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

}