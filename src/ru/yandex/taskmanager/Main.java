package ru.yandex.taskmanager;

import ru.yandex.taskmanager.tasks.*;
import ru.yandex.taskmanager.manager.*;


public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();



        Task task = new Task("Task Name", "Task Description");
        Task task2 = new Task("Task Name2", "Task Description2");
        taskManager.createTask(task);
        taskManager.createTask(task2);

        Epic epic = new Epic("Epic Name", "Epic Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask Name", "Subtask Description", epic.getId());
        Subtask subtask2 = new Subtask("Subtask Name2", "Subtask Description2", epic.getId());
        Subtask subtask3 = new Subtask("Subtask Name3", "Subtask Description3", epic.getId());
        taskManager.createSubtask(subtask);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        Epic epic2 = new Epic("Epic Name2", "Epic Description2");
        taskManager.createEpic(epic2);

        taskManager.getTask(task.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getSubtask(subtask2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getEpic(epic.getId());
        System.out.println(taskManager.getHistory());

        taskManager.getTask(task.getId()); // Проверка на повторы
        System.out.println(taskManager.getHistory());
        taskManager.getEpic(epic.getId());
        System.out.println(taskManager.getHistory());

        taskManager.removeTask(task.getId()); // Проверка на удаление задачи
        System.out.println(taskManager.getHistory());

        taskManager.getSubtask(subtask.getId()); // Проверка на удаление подзадач
        taskManager.getSubtask(subtask3.getId());
        taskManager.getTask(task2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.removeEpic(epic.getId());
        System.out.println(taskManager.getHistory()); // Остаётся только task2

    }
}
