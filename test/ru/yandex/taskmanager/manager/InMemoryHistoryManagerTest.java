package ru.yandex.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.taskmanager.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;
    private static Task task1;
    private static Task task2;
    private static Task task3;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Test Task", "Test Description");
        task2 = new Task("Test Task2", "Test Description2");
        task3 = new Task("Test Task3", "Test Description3");
        task1.setId(1);
        task2.setId(2);
        task3.setId(3);
    }

    @Test
    void add() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "После добавления задачи, история не должна быть пустой.");
    }


    @Test
    void historyMustKeepPreviousVersionOfTask() {
        historyManager.add(task1);
        task1.setName("New Name Task");
        Task taskInHistory = historyManager.getHistory().get(0);
        assertEquals("Test Task", taskInHistory.getName(), "Должно сохраниться исходное имя.");
    }

    @Test
    void remove() {
        historyManager.add(task1);
        assertNotNull(historyManager.getHistory(), "История не должна быть пустой.");

        historyManager.remove(task1.getId());
        assertEquals(0, historyManager.getHistory().size(), "Задача должна удалиться из истории.");
    }

    @Test
    void shouldRemoveTaskFromBeginning(){
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task1.getId());

        assertEquals(List.of(task2, task3), historyManager.getHistory(), "Списки не совпадают.");
    }

    @Test
    void shouldRemoveTaskFromCenter(){
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task2.getId());

        assertEquals(List.of(task1, task3), historyManager.getHistory(), "Списки не совпадают.");
    }

    @Test
    void shouldRemoveTaskFromEnd(){
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(task3.getId());

        assertEquals(List.of(task1, task2), historyManager.getHistory(), "Списки не совпадают.");
    }

    @Test
    void shouldReturnEmptyHistory(){
        assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой. если задачи не добавлены.");
    }

    @Test
    void shouldDeleteDuplicates(){
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "История не должна содержать дублей.");
        assertEquals(task1, history.get(1), "task1 должна перейти в конец.");
    }
}