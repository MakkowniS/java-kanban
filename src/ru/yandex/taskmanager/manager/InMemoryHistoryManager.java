package ru.yandex.taskmanager.manager;

import ru.yandex.taskmanager.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history = new ArrayList<>(); // Список для хранения истории
    private final int historyLimit = 10;

    @Override
    public void add(Task task){
        if (history.size() >= historyLimit) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory(){
        return history;
    }
}
