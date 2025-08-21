package ru.yandex.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.tasks.Epic;
import ru.yandex.taskmanager.tasks.StatusOfTask;
import ru.yandex.taskmanager.tasks.Subtask;
import ru.yandex.taskmanager.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

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
    void shouldBeEqualsIfHasEqualsIdTaskExtends() {
        Epic epic = new Epic("Эпик 3", "Описание 3");
        Epic epic2 = new Epic("Эпик 3", "Описание 3");
        Subtask subtask = new Subtask("Подзадача 3", "Описание 3", epic.getId());
        Subtask subtask2 = new Subtask("Подзадача 3", "Описание 3", epic.getId());
        epic.setId(epic2.getId());
        assertEquals(epic, epic2, "Эпики не равны");
        subtask.setId(subtask2.getId());
        assertEquals(subtask, subtask2, "Подзадачи не равны");
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
    void managersShouldReturnWorkingTaskManager() {
        TaskManager testTaskManager = Managers.getDefault();

        assertNotNull(testTaskManager, "Managers,getDefault не должен возвращать null");

        Task task = new Task("Test Task", "Test Description");
        testTaskManager.createTask(task);

        Task createdTask = testTaskManager.getTask(task.getId());
        assertEquals(task, createdTask, "Работающий TaskManager должен создавать и возвращать Task");
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

    @Test
    void historyMustKeepPreviousVersionOfTask(){
        Task task = new Task("Test Task", "Test Description");
        taskManager.createTask(task);
        taskManager.getTask(task.getId());
        task.setName("New Name");
        task.setDescription("New Description");
        taskManager.updateTask(task);
        Task historyTask = taskManager.getHistory().get(0);

        assertEquals("Test Task", historyTask.getName(), "Должно сохраниться исходное имя.");
        assertEquals("Test Description", historyTask.getDescription(), "Должно сохраниться исходное описание.");
    }

    @Test
    void shouldAddAndGetNewTasksEpicsSubtasks() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(taskManager.getTask(taskId), "Задача не возвращается.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Список задач не возвращается.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "В список добавлена не та задача.");

        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        taskManager.createEpic(epic);
        final int epicId = epic.getId();

        assertNotNull(taskManager.getEpic(epicId), "Эпик не возвращается.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Список эпиков не возвращается.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "В список добавлен не тот эпик.");

        Subtask subtask = new Subtask("Test Subtask", "Test Subtask description", epicId);
        taskManager.createSubtask(subtask);
        final int subtaskId = subtask.getId();

        assertNotNull(taskManager.getSubtask(subtaskId), "Подзадача не возвращается");

        final  List<Subtask> subtasks = taskManager.getSubtasks();
        assertNotNull(taskManager.getSubtasksByEpicId(epicId), "Список задач по Id эпика не возвращается.");
        assertNotNull(subtasks, "Список подзадач не возвращается.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "В список добавлена не та подзадача.");
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
        List<Task> tasks = taskManager.getTasks();

        assertFalse(tasks.contains(task), "Задача не удалена.");

        taskManager.clearAllTasks();
        assertTrue(taskManager.getTasks().isEmpty(), "Список задач не очищен.");

    }

}