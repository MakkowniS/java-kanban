package ru.yandex.taskmanager.manager;

import ru.yandex.taskmanager.tasks.Epic;
import ru.yandex.taskmanager.tasks.Subtask;
import ru.yandex.taskmanager.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history = new ArrayList<>(); // Список для хранения истории
    private final int historyLimit = 10;

    @Override
    public void add(Task task){
        if (task instanceof Subtask){ // Копирование объекта класса в историю
            history.add(new Subtask((Subtask)task));
        } else if (task instanceof Epic){
            history.add(new Epic((Epic)task));
        } else {
            history.add(new Task(task));
        }
        if (history.size() >= historyLimit) {
            history.removeFirst();
        }
    }

    @Override
    public List<Task> getHistory(){
        return history;
    }
}
