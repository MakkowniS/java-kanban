package ru.yandex.taskmanager;

import ru.yandex.taskmanager.tasks.*;
import ru.yandex.taskmanager.manager.*;


public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Задача 1", "Описание 1");
        Task task2 = new Task("Задача 2", "Описание 1");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Описание 1");
        taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "Описание 1", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание 2", epic1.getId());
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Описание 2");
        taskManager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Описание 1", "Описание 1", epic2.getId());
        taskManager.createSubtask(subtask3);

        taskManager.getTask(task1.getId());
        taskManager.getEpic(epic1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task1.getId());

        taskManager.printAllTasks();

    }
}
