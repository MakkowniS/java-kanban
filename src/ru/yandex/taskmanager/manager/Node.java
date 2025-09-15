package ru.yandex.taskmanager.manager;

public class Node<T> {
    public Node<T> next;
    public Node<T> prev;
    public T data;

    public Node(Node<T> prev, T data, Node<T> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}
