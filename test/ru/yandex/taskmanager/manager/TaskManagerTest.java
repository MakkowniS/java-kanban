package ru.yandex.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.exceptions.NotFoundException;
import ru.yandex.taskmanager.tasks.*;

import java.time.Duration;
import java.util.List;

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
        assertThrows(NotFoundException.class , () -> taskManager.getTask(task.getId()));
    }

    @Test
    void shouldRemoveEpicsWithSubtasks() {
        taskManager.removeEpic(epic.getId());
        assertThrows(NotFoundException.class , () -> taskManager.getEpic(epic.getId()));
        assertThrows(NotFoundException.class , () -> taskManager.getSubtask(subtask.getId()));
    }

    @Test
    void shouldRemoveSubtask() {
        taskManager.removeSubtask(subtask.getId());
        assertThrows(NotFoundException.class , () -> taskManager.getSubtask(subtask.getId()));
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
    void shouldUpdateEpicStatusDependsOnSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask1", "Description", epic.getId());
        taskManager.createSubtask(subtask);
        Subtask subtask3 = new Subtask("Test NewSubtask", "Test NewSubtask description", epic.getId());
        taskManager.createSubtask(subtask3);

        assertEquals(StatusOfTask.NEW, taskManager.getEpic(subtask3.getEpicId()).getStatus(), "Изначальный статус эпика должен быть NEW");

        subtask.setStatus(StatusOfTask.DONE);
        subtask3.setStatus(StatusOfTask.DONE);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask3);

        assertEquals(StatusOfTask.DONE, taskManager.getEpic(subtask3.getEpicId()).getStatus(), "Статус эпика не DONE");

        subtask.setStatus(StatusOfTask.NEW);
        taskManager.updateSubtask(subtask);

        assertEquals(StatusOfTask.IN_PROGRESS, taskManager.getEpic(subtask3.getEpicId()).getStatus(), "Статус эпика не IN_PROGRESS");

        subtask.setStatus(StatusOfTask.IN_PROGRESS);
        subtask3.setStatus(StatusOfTask.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        taskManager.updateSubtask(subtask3);

        assertEquals(StatusOfTask.IN_PROGRESS, taskManager.getEpic(subtask3.getEpicId()).getStatus(), "Статус эпика не IN_PROGRESS");
    }

    @Test
    void shouldThrowExceptionWhenTasksOverlap() {
        Task task1 = new Task("Task1", "desc");
        task1.setStartTimeNow();
        task1.setDuration(Duration.ofMinutes(60));
        taskManager.createTask(task1);

        Task task2 = new Task("Task2", "desc");
        task2.setStartTime(task1.getStartTime().plusMinutes(30)); // пересечение
        task2.setDuration(Duration.ofMinutes(60));

        assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void shouldReturnTasksSorted() {
        Task task1 = new Task("Task1", "desc");
        task1.setStartTimeNow();
        task1.setDuration(Duration.ofMinutes(60));
        taskManager.createTask(task1);

        Task task2 = new Task("Task2", "desc");
        task2.setStartTime(task1.getStartTime().plusMinutes(60));
        task2.setDuration(Duration.ofMinutes(60));
        taskManager.createTask(task2);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(List.of(task1, task2), List.copyOf(prioritized));

    }

    @Test
    void taskWithManualAndAutoIdShouldNotConflict() {
        task.setId(50);

        assertNotEquals(taskManager.getTasks().getFirst(), taskManager.getTask(task2.getId()), "Id должны быть уникальны");
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

