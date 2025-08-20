package ru.yandex.taskmanager;

import ru.yandex.taskmanager.tasks.*;
import ru.yandex.taskmanager.manager.*;


public class Main {

    public static void main(String[] args) {

        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();

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

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());

        task1.setStatus(StatusOfTask.IN_PROGRESS);
        taskManager.updateTask(task1);

        epic1.setName("Новый эпик 1");
        taskManager.updateEpic(epic1);

        subtask1.setStatus(StatusOfTask.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);

        subtask3.setStatus(StatusOfTask.DONE);
        taskManager.updateSubtask(subtask3);

        System.out.println(taskManager.getTask(task1.getId()));
        System.out.println(taskManager.getEpic(epic1.getId()));
        System.out.println(taskManager.getEpic(subtask3.getEpicId()));
        System.out.println(taskManager.getSubtask(subtask1.getId()));

        System.out.println(taskManager.getSubtasksByEpicId(subtask2.getEpicId()));

        taskManager.removeTask(task1.getId());
        taskManager.removeEpic(epic1.getId());
        taskManager.removeSubtask(subtask3.getId());

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllSubtasks());

        taskManager.clearAllEpics();
        taskManager.clearAllTasks();
        taskManager.clearAllSubtasks();

    }
}
