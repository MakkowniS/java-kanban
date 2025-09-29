package ru.yandex.taskmanager.manager;

import ru.yandex.taskmanager.tasks.*;
import ru.yandex.taskmanager.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            String data = Files.readString(file.toPath());
            String[] lines = data.split("\n"); // Получаем строки из файла

            for (int i = 1; i < lines.length; i++) { // Пропускаем заголовок
                String line = lines[i];

                Task task = fromString(line);
                int id = task.getId();

                if (task instanceof Epic) {
                    manager.putEpicInMap((Epic) task);
                    manager.updateIdCounter(id);
                } else if (task instanceof Subtask) {
                    manager.putSubtaskInMap((Subtask) task);
                    Epic subtasksEpic = manager.getEpic(((Subtask) task).getEpicId()); // Добавляем подзадачу к эпику
                    if (subtasksEpic != null) {
                        subtasksEpic.addSubtaskId(((Subtask) task).getId());
                    }
                } else {
                    manager.putTaskInMap(task);
                    manager.updateIdCounter(id);
                }

            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла" + file, e);
        }
        manager.updateEpicsStatus(); // Обновляем статусы эпиков
        return manager;
    }

    void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,epic").append("\n");

        for (Task task : getTasks()) {
            sb.append(taskToString(task)).append("\n");
        }
        for (Epic epic : getEpics()) {
            sb.append(taskToString(epic)).append("\n");
        }
        for (Subtask subtask : getSubtasks()) {
            sb.append(taskToString(subtask)).append("\n");
        }

        try {
            Files.writeString(file.toPath(), sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("При сохранении произошла ошибка.");
        }
    }

    public static String taskToString(Task task) {
        if (task == null) {
            return "";
        }

        TypeOfTask type;
        if (task instanceof Epic) {
            type = TypeOfTask.EPIC;
        } else if (task instanceof Subtask) {
            type = TypeOfTask.SUBTASK;
        } else {
            type = TypeOfTask.TASK;
        }

        String epicId = "";
        if (task instanceof Subtask) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }

        return task.getId() + "," + type + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + epicId;
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

    /// /// Задачи
    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }
    /// ///

    /// /// Эпики
    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }
    /// ///

    /// /// Подзадачи
    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }
}
