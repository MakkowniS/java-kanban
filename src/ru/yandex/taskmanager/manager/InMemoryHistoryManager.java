package ru.yandex.taskmanager.manager;

import ru.yandex.taskmanager.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;

    private static class Node {
        public Node next;
        public Node prev;
        public Task data;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
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
        Node node = historyMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node node = head;

        while (node != null) {
            history.add(node.data);
            node = node.next;
        }

        return history;
    }

    private void linkLast(Task task) {
        final Node newNode = new Node(tail, task, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = newNode;
    }

    private void removeNode(Node node) {
        Node prev = node.prev;
        Node next = node.next;

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
}
