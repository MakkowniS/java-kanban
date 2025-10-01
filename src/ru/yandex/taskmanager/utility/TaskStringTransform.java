package ru.yandex.taskmanager.utility;

import ru.yandex.taskmanager.tasks.*;

public class TaskStringTransform {

    public static String taskToString(Task task) {
        if (task == null) {
            return "";
        }

        String epicId = "";
        if (task.getType() == TypeOfTask.SUBTASK) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }

        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + epicId;
    }

    public static Task fromString(String str) {
        String[] split = str.split(",");

        int id = Integer.parseInt(split[0]);
        TypeOfTask type = TypeOfTask.valueOf(split[1]);
        String name = split[2];
        StatusOfTask status = StatusOfTask.valueOf(split[3]);
        String description = split[4];
        String epicIdField = split.length > 5 ? split[5] : "";

        switch (type) {
            case TASK -> {
                Task task = new Task(name, description);
                task.setId(id);
                task.setStatus(status);
                return task;
            }
            case EPIC -> {
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            }
            case SUBTASK -> {
                int epicId = epicIdField.isEmpty() ? 0 : Integer.parseInt(epicIdField);
                Subtask subtask = new Subtask(name, description, epicId);
                subtask.setId(id);
                subtask.setStatus(status);
                return subtask;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип: " + type);
        }
    }
}
