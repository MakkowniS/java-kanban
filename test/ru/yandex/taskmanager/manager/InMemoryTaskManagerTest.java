package ru.yandex.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.tasks.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;
    private Task task;
    private Task task2;
    private Epic epic;
    private Epic epic2;
    private Subtask subtask;
    private Subtask subtask2;

    @BeforeEach
    void creatingDefaultStructure() {
        taskManager = Managers.getDefault();
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
    void shouldAddAndGetNewTasks() {
        final int taskId = task.getId();

        assertNotNull(taskManager.getTask(taskId), "Задача не возвращается.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Список задач не возвращается.");
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "В список добавлена не та задача.");
    }

    @Test
    void shouldAddAndGetNewEpics() {
        final int epicId = epic.getId();

        assertNotNull(taskManager.getEpic(epicId), "Эпик не возвращается.");

        final List<Epic> epics = taskManager.getEpics();

        assertNotNull(epics, "Список эпиков не возвращается.");
        assertEquals(2, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "В список добавлен не тот эпик.");
    }

    @Test
    void shouldAddAndGetNewSubtasks() {
        final int subtaskId = subtask.getId();

        assertNotNull(taskManager.getSubtask(subtaskId), "Подзадача не возвращается");

        final List<Subtask> subtasks = taskManager.getSubtasks();
        List<Integer> epicSubtasks = epic.getSubtasksId();

        assertNotNull(taskManager.getSubtasksByEpicId(epic.getId()), "Список задач по Id эпика не возвращается.");
        assertTrue(epicSubtasks.contains(subtask.getId()), "Подзадача не появилась в списке эпика.");
        assertNotNull(subtasks, "Список подзадач не возвращается.");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "В список добавлена не та подзадача.");
    }

    @Test
    void shouldRemoveTask() {
        taskManager.removeTask(task.getId());

        assertNull(taskManager.getTask(task.getId()), "Задача не удалена.");
    }

    @Test
    void shouldRemoveEpicWithSubtasks() {
        taskManager.removeEpic(epic.getId());

        assertNull(taskManager.getEpic(epic.getId()), "Эпик не удалён.");
        assertNull(taskManager.getSubtask(subtask.getId()), "Подзадача должна удалиться вместе с эпиком.");
    }

    @Test
    void editingReturnedTaskFromListChangesStoredTask(){
        Task returnedTask = taskManager.getTask(task.getId());
        returnedTask.setName("New Name Task");

        assertEquals("New Name Task", taskManager.getTask(task.getId()).getName(), "Изменение" +
                "возвращаемого объекта должно повлиять на объект внутри менеджера.");
    }

    @Test
    void removedSubtaskShouldBeRemovedFromEpicSubtasksList(){
        assertTrue(epic.getSubtasksId().contains(subtask.getId()), "Epic не знает о своей подзадаче.");
        taskManager.removeSubtask(subtask.getId());
        assertFalse(epic.getSubtasksId().contains(subtask.getId()), "Подзадача не удалилась из Эпика.");
    }

    @Test
    void clearAllSubtasksShouldClearAllEpicsSubtasksLists(){
        assertNotNull(epic.getSubtasksId(), "В Эпике нет записанных подзадач.");
        assertNotNull(epic2.getSubtasksId(), "В Эпике нет записанных подзадач.");
        taskManager.clearAllSubtasks();
        assertNull(epic.getSubtasksId(), "Эпики не очистились от подзадач.");
        assertNull(epic2.getSubtasksId(), "Эпики не очистились от подзадач.");

    }

    @Test
    void shouldRemoveSubtask() {
        List<Integer> epicSubtasks = epic.getSubtasksId();
        taskManager.removeSubtask(subtask.getId());

        assertNull(taskManager.getSubtask(subtask.getId()), "Подзадача не удалена.");
        assertFalse(epicSubtasks.contains(subtask.getId()), "Подзадача не удалилась из списка эпика.");
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
        Task newTask = new Task("Test NewTask", "Test NewTask Description");
        newTask.setId(task.getId());
        taskManager.updateTask(newTask);

        assertEquals(taskManager.getTask(task.getId()).getName(), newTask.getName(), "Имя задачи не обновилось.");
        assertEquals(taskManager.getTask(task.getId()).getDescription(), newTask.getDescription(), "Описание задачи не обновилось.");
    }

    @Test
    void shouldUpdateExistingEpic() {
        Epic newEpic = new Epic("Test NewEpic", "Test NewEpic Description");
        newEpic.setId(epic.getId());
        taskManager.updateEpic(newEpic);

        assertEquals(taskManager.getEpic(epic.getId()).getName(), newEpic.getName(), "Имя эпика не обновилось.");
        assertEquals(taskManager.getEpic(epic.getId()).getDescription(), newEpic.getDescription(), "Описание эпика не обновилось.");
    }

    @Test
    void shouldUpdateExistingSubtaskAndChangeEpicStatus() {
        Subtask newSubtask = new Subtask("Test NewSubtask", "Test NewSubtask description", epic.getId());
        newSubtask.setId(subtask.getId());
        newSubtask.setStatus(StatusOfTask.DONE);
        taskManager.updateSubtask(newSubtask);

        assertEquals(taskManager.getSubtask(subtask.getId()).getName(), newSubtask.getName(), "Имя подзадачи не обновилось.");
        assertEquals(taskManager.getSubtask(subtask.getId()).getDescription(), newSubtask.getDescription(), "Описание подзадачи не обновилось.");
        assertEquals(taskManager.getSubtask(subtask.getId()).getStatus(), newSubtask.getStatus(), "Статус подзадачи не обновился.");
        assertEquals(StatusOfTask.DONE, taskManager.getEpic(newSubtask.getEpicId()).getStatus(), "Статус эпика не поменялся вслед за подзадачей.");
    }

    @Test
    void shouldReturnListOfEpicsSubtasks() {
        List<Subtask> epicSubtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertNotNull(epicSubtasks, "Список подзадач не возвращается.");
    }

    @Test
    void shouldReturnHistoryList() {
        taskManager.getTask(task.getId());
        assertNotNull(taskManager.getHistory(), "История не возвращается.");
    }
}