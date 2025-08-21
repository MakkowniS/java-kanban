package ru.yandex.taskmanager.manager;

import ru.yandex.taskmanager.tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();
}
