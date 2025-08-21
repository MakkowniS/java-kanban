package ru.yandex.taskmanager.manager;

import ru.yandex.taskmanager.tasks.*;

import java.util.ArrayList;
import java.util.List;

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

    void updateEpicStatus(int epicId);

    List<Task> getHistory();

    default void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : getTasks()) {
            System.out.println(task);
        }

        System.out.println("Эпики:");
        for (Epic epic : getEpics()) {
            System.out.println(epic);
            for (Subtask subtask : getSubtasksByEpicId(epic.getId())) {
                System.out.println("--> " + subtask);
            }
        }

        System.out.println("Подзадачи:");
        for (Subtask subtask : getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : getHistory()) {
            System.out.println(task);
        }
    }
}
