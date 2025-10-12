package ru.yandex.taskmanager.manager;

import ru.yandex.taskmanager.tasks.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    ArrayList<Task> getTasks();

    void createTask(Task task);

    Task getTask(int id);

    void clearAllTasks();

    void updateTask(Task updatedTask);

    void removeTask(int id);

    ArrayList<Epic> getEpics();

    void createEpic(Epic epic);

    Epic getEpic(int id);

    void clearAllEpics();

    void updateEpic(Epic updatedEpic);

    void removeEpic(int id);

    ArrayList<Subtask> getSubtasks();

    void createSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    void clearAllSubtasks();

    void updateSubtask(Subtask updatedSubtask);

    void removeSubtask(int id);

    ArrayList<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();

    TreeSet<Task> getPrioritizedTasks();

}
