package ru.yandex.taskmanager.manager;

import ru.yandex.taskmanager.tasks.*;
import ru.yandex.taskmanager.exceptions.*;
import ru.yandex.taskmanager.utility.TaskStringTransform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileBackedTaskManager extends InMemoryTaskManager {
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

                Task task = TaskStringTransform.fromString(line);
                int id = task.getId();

                switch (task.getType()) {
                    case EPIC -> manager.epics.put(id, (Epic) task);
                    case TASK -> manager.tasks.put(id, task);
                    case SUBTASK -> {
                        manager.subtasks.put(id, (Subtask) task);
                        Epic subtasksEpic = manager.epics.get(((Subtask) task).getEpicId()); // Добавляем подзадачу к эпику
                        if (subtasksEpic != null) {
                            subtasksEpic.addSubtaskId(task.getId());
                        }
                    }
                    default -> throw new IllegalArgumentException("Неизвестный тип: " + task.getType());
                }
                if (id > manager.idCounter) {
                    manager.idCounter = id;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения файла" + file, e);
        }
        return manager;
    }

    protected void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,name,status,description,startTime,duration,epic").append("\n");

        for (Task task : getTasks()) {
            sb.append(TaskStringTransform.taskToString(task)).append("\n");
        }
        for (Epic epic : getEpics()) {
            sb.append(TaskStringTransform.taskToString(epic)).append("\n");
        }
        for (Subtask subtask : getSubtasks()) {
            sb.append(TaskStringTransform.taskToString(subtask)).append("\n");
        }

        try {
            Files.writeString(file.toPath(), sb.toString());
        } catch (IOException e) {
            throw new ManagerSaveException("При сохранении произошла ошибка.");
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
