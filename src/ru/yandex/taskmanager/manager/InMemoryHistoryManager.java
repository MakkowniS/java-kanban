package ru.yandex.taskmanager.manager;

import ru.yandex.taskmanager.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    private void removeNode(Node<Task> node) {
        Node<Task> prev = node.prev;
        Node<Task> next = node.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
        }
        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) return;

        remove(task.getId()); // Удаление предыдущего просмотра задачи, если есть

        Task taskToAdd = task.objectCopy(); // Копирование задачи для добавления
        linkLast(taskToAdd); // Добавление в конец списка
        historyMap.put(taskToAdd.getId(), tail);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = historyMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node<Task> node = head;

        while (node != null) {
            history.add(node.data);
            node = node.next;
        }

        return history;
    }
}
