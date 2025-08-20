package ru.yandex.taskmanager.manager;

import ru.yandex.taskmanager.tasks.*;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    ArrayList<Task> getAllTasks();

    void createTask(Task task);

    Task getTask(int id);

    void clearAllTasks();

    void updateTask(Task updatedTask);

    void removeTask(int id);

    ArrayList<Epic> getAllEpics();

    void createEpic(Epic epic);

    Epic getEpic(int id);

    void clearAllEpics();

    void updateEpic(Epic updatedEpic);

    void removeEpic(int id);

    ArrayList<Subtask> getAllSubtasks();

    void createSubtask(Subtask subtask);

    Subtask getSubtask(int id);

    void clearAllSubtasks();

    void updateSubtask(Subtask updatedSubtask);

    void removeSubtask(int id);

    void addInHistory(Task task);

    List<Task> getHistory();

    ArrayList<Subtask> getSubtasksByEpicId(int epicId);

    void updateEpicStatus(int epicId);
}
