package ru.yandex.taskmanager.manager;

import ru.yandex.taskmanager.tasks.*;


import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>(); // Таблица Задач
    private final HashMap<Integer, Epic> epics = new HashMap<>(); // Таблица Эпиков
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>(); // Таблица Подзадач
    private int idCounter = 1; // Сквозной счётчик id

    /// /// Блок обычных задач

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void createTask(Task task) {
        if (task != null) {
            task.setId(idCounter++);
            tasks.put(task.getId(), task);
        }
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public void clearAllTasks() {
        tasks.clear();
    }

    public void updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    /// ///
    /// /// Блок эпиков

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void createEpic(Epic epic) {
        if (epic != null) {
            epic.setId(idCounter++);
            epics.put(epic.getId(), epic);
        }
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public void clearAllEpics() {
        epics.clear();
        subtasks.clear(); // Т.к. подзадачи - часть эпиков, удаляются и они
    }

    public void updateEpic(Epic updatedEpic) {
        if (epics.containsKey(updatedEpic.getId())) {
            Epic epic = epics.get(updatedEpic.getId());
            epic.setName(updatedEpic.getName());
            epic.setDescription(updatedEpic.getDescription());
            epics.put(updatedEpic.getId(), epic);
        }
    }

    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> epicSubtasksIds = new ArrayList<>(epics.get(id).getSubtasksId()); // Получаем список id подзадач из эпика
            for (Integer subtaskId : epicSubtasksIds) { // Пробегая по списку, удаляем из хэшмапы подзадачи, которые там есть
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
        }
    }

    /// ///
    /// /// Блок подзадач

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void createSubtask(Subtask subtask) {
        if (subtask != null && epics.containsKey(subtask.getEpicId())) {
            subtask.setId(idCounter++);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId()); // Добавление подзадачи в список к эпику
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public void clearAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearAllSubtasksId();
            updateEpicStatus(epic.getId());
        }
    }

    public void updateSubtask(Subtask updatedSubtask) {
        if (subtasks.containsKey(updatedSubtask.getId())) {
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
        }
        updateEpicStatus(updatedSubtask.getEpicId());
    }

    public void removeSubtask(int id) {
        int epicId = subtasks.get(id).getEpicId();
        if (subtasks.containsKey(id)) {
            subtasks.remove(id);
            Epic epic = epics.get(epicId);
            epic.removeSubtaskId(id);
        }
        updateEpicStatus(epicId);

    }

    /// ///

    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> subtasksByEpic = new ArrayList<>(); // Новый список для вывода
        if (epics.get(epicId).getSubtasksId() != null) {
            for (int subtaskId : epics.get(epicId).getSubtasksId()) { // Пробег по списку id, полученному из эпика
                subtasksByEpic.add(subtasks.get(subtaskId));
            }
        }
        return subtasksByEpic;
    }

    private void updateEpicStatus(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);

            ArrayList<Subtask> subtasksByEpic = getSubtasksByEpicId(epicId);
            if (subtasksByEpic.isEmpty()) {
                epic.setStatus(StatusOfTask.NEW);
                return;
            }
            boolean allDone = true;
            boolean allNew = true;
            for (Subtask subtask : subtasksByEpic) {
                if (subtask.getStatus() != StatusOfTask.DONE) {
                    allDone = false;
                }
                if (subtask.getStatus() != StatusOfTask.NEW) {
                    allNew = false;
                }
            }
            if (allDone) {
                epic.setStatus(StatusOfTask.DONE);
            } else if (allNew) {
                epic.setStatus(StatusOfTask.NEW);
            } else {
                epic.setStatus(StatusOfTask.IN_PROGRESS);
            }


        }


    }
}
