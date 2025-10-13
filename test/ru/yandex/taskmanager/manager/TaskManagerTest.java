package ru.yandex.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.tasks.*;

import static org.junit.jupiter.api.Assertions.*;


public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task;
    protected Task task2;
    protected Epic epic;
    protected Epic epic2;
    protected Subtask subtask;
    protected Subtask subtask2;

    protected abstract T createTaskManager();

    @BeforeEach
    void setup() {
        taskManager = createTaskManager();
        task = new Task("Test Task", "Test Description");
        task2 = new Task("Test Task2", "Test Description2");
        epic = new Epic("Test Epic", "Test Epic Description");
        epic2 = new Epic("Test Epic2", "Test Epic Description2");

        taskManager.createTask(task);
        taskManager.createTask(task2);
        taskManager.createEpic(epic);
        taskManager.createEpic(epic2);

        subtask = new Subtask("Test Subtask", "Test Description", epic.getId());
        subtask2 = new Subtask("Test Subtask2", "Test Description2", epic2.getId());
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask2);
    }

    @Test
    void shouldAddAndGetTasks() {
        assertEquals(2, taskManager.getTasks().size(), "Кол-во задач должно быть равно 2.");
        assertEquals(task, taskManager.getTask(task.getId()), "Задача не возвращается из менеджера.");
    }

    @Test
    void shouldAddAndGetEpics() {
        assertEquals(2, taskManager.getEpics().size());
        assertEquals(epic, taskManager.getEpic(epic.getId()));
    }

    @Test
    void shouldAddAndGetSubtasks() {
        assertEquals(2, taskManager.getSubtasks().size());
        assertEquals(subtask, taskManager.getSubtask(subtask.getId()));
    }

    @Test
    void shouldRemoveTask() {
        taskManager.removeTask(task.getId());
        assertNull(taskManager.getTask(task.getId()), "Задача не удалена.");
    }

    @Test
    void shouldRemoveEpicsWithSubtasks() {
        taskManager.removeEpic(epic.getId());
        assertNull(taskManager.getEpic(epic.getId()), "Эпик не удалён.");
        assertNull(taskManager.getSubtask(subtask.getId()), "Подзадачи эпика не удалены.");
    }

    @Test
    void shouldRemoveSubtask() {
        taskManager.removeSubtask(subtask.getId());
        assertNull(taskManager.getSubtask(subtask.getId()), "Подзадача не удалена.");
        assertFalse(epic.getSubtasksId().contains(subtask.getId()), "Подзадача не удалилась из списка эпика.");
    }

    @Test
    void clearAllSubtasksShouldClearAllEpicsSubtasksLists() {
        assertNotNull(epic.getSubtasksId(), "В Эпике нет записанных подзадач.");
        assertNotNull(epic2.getSubtasksId(), "В Эпике нет записанных подзадач.");
        taskManager.clearAllSubtasks();
        assertEquals(0, epic.getSubtasksId().size(), "Эпики не очистились от подзадач.");
        assertEquals(0, epic2.getSubtasksId().size(), "Эпики не очистились от подзадач.");
    }

    @Test
    void shouldClearAllTasks() {
        taskManager.clearAllTasks();
        assertTrue(taskManager.getTasks().isEmpty(), "Список задач не очищен.");
    }

    @Test
    void shouldClearAllEpicsWithSubtasks() {
        taskManager.clearAllEpics();
        assertTrue(taskManager.getEpics().isEmpty(), "Список эпиков не очистился.");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Список подзадач не очистился вместе со списком эпиков.");
    }

    @Test
    void shouldClearAllSubtasks() {
        taskManager.clearAllSubtasks();
        assertTrue(taskManager.getSubtasks().isEmpty(), "Список подзадач не очистился.");
    }

    @Test
    void shouldUpdateExistingTask() {
        Task newTask = new Task("Update", "Description");
        newTask.setId(task.getId());
        newTask.setStartTimeNow();
        taskManager.updateTask(newTask);
        assertEquals(newTask.getName(), taskManager.getTask(task.getId()).getName(), "Имя задачи не обновилось.");
    }

    @Test
    void shouldUpdateExistingEpic() {
        Epic newEpic = new Epic("Update", "Test Description");
        newEpic.setId(epic.getId());
        taskManager.updateEpic(newEpic);
        assertEquals(newEpic.getName(), taskManager.getEpic(epic.getId()).getName(), "Имя эпика не обновилось.");
    }

    @Test
    void shouldUpdateExistingSubtaskAndChangeEpicStatus() {
        Subtask newSubtask = new Subtask("Update", "Test NewSubtask description", epic.getId());
        newSubtask.setId(subtask.getId());
        newSubtask.setStatus(StatusOfTask.DONE);
        newSubtask.setStartTimeNow();
        taskManager.updateSubtask(newSubtask);

        assertEquals(taskManager.getSubtask(subtask.getId()).getName(), newSubtask.getName(), "Имя подзадачи не обновилось.");
        assertEquals(taskManager.getSubtask(subtask.getId()).getStatus(), newSubtask.getStatus(), "Статус подзадачи не обновился.");
        assertEquals(StatusOfTask.DONE, taskManager.getEpic(newSubtask.getEpicId()).getStatus(), "Статус эпика не поменялся вслед за подзадачей.");
    }

    @Test
    void taskWithManualAndAutoIdShouldNotConflict() {
        task.setId(50);

        assertNotEquals(taskManager.getTask(task.getId()), taskManager.getTask(task2.getId()), "Id должны быть уникальны");
    }

    @Test
    void taskFieldsShouldNotChangeAfterAdding() {
        Task newTask = new Task("Test Task", "Test Description");
        String newTaskName = newTask.getName(); // Получаем поля задачи до добавления
        String newTaskDescription = newTask.getDescription();
        StatusOfTask newTaskStatus = newTask.getStatus();
        taskManager.createTask(newTask);
        Task createdTask = taskManager.getTask(newTask.getId());

        assertEquals(newTaskName, createdTask.getName(), "Название новой задачи и добавленной не совпадает.");
        assertEquals(newTaskDescription, createdTask.getDescription(), "Описание новой задачи и добавленной не совпадает.");
        assertEquals(newTaskStatus, createdTask.getStatus(), "Статус новой задачи и добавленной не совпадает.");
    }

    @Test
    void editingReturnedTaskFromListChangesStoredTask() {
        Task returnedTask = taskManager.getTask(task.getId());
        returnedTask.setName("New Name Task");

        assertEquals("New Name Task", taskManager.getTask(task.getId()).getName(), "Изменение" + "возвращаемого объекта должно повлиять на объект внутри менеджера.");
    }

    @Test
    void shouldReturnHistoryList() {
        taskManager.getTask(task.getId());
        assertNotNull(taskManager.getHistory(), "История не возвращается.");
    }
}

