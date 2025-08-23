package ru.yandex.taskmanager.manager;

import ru.yandex.taskmanager.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history = new ArrayList<>(); // Список для хранения истории
    static final int HISTORY_LIMIT = 10;

    @Override
    public void add(Task task) {
        if (task != null) {
            history.add(task.objectCopy());
            if (history.size() > HISTORY_LIMIT) {
                history.removeFirst();
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
