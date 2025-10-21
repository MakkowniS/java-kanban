package ru.yandex.taskmanager.utility;

import ru.yandex.taskmanager.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskStringTransform {

    public static String taskToString(Task task) {
        if (task == null) {
            return "";
        }

        String epicId = "";
        if (task.getType() == TypeOfTask.SUBTASK) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }

        return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," +
                task.getDescription() + "," + task.getStartTime() + "," + task.getDuration().toMinutes() + "," + epicId;
    }

    public static Task fromString(String str) {
        String[] split = str.split(",");

        int id = Integer.parseInt(split[0]);
        TypeOfTask type = TypeOfTask.valueOf(split[1]);
        String name = split[2];
        StatusOfTask status = StatusOfTask.valueOf(split[3]);
        String description = split[4];
        LocalDateTime startTime = LocalDateTime.parse(split[5]);
        Duration duration = Duration.ofMinutes(Long.parseLong(split[6]));
        String epicIdField = split.length > 7 ? split[7] : "";
        Task task;

        switch (type) {
            case TASK -> task = new Task(name, description);
            case EPIC -> task = new Epic(name, description);
            case SUBTASK -> {
                int epicId = epicIdField.isEmpty() ? 0 : Integer.parseInt(epicIdField);
                task = new Subtask(name, description, epicId);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип: " + type);
        }
        task.setId(id);
        task.setStatus(status);
        task.setStartTime(startTime);
        task.setDuration(duration);

        return task;
    }
}
