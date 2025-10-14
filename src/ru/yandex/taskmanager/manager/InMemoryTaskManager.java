package ru.yandex.taskmanager.manager;

import ru.yandex.taskmanager.tasks.*;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Comparator;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>(); // Таблица Задач
    protected final HashMap<Integer, Epic> epics = new HashMap<>(); // Таблица Эпиков
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>(); // Таблица Подзадач
    protected int idCounter = 1; // Сквозной счётчик id
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    /// /// Блок обычных задач

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void createTask(Task task) {
        if (task != null) {
            task.setId(idCounter++);
            checkAndAddToPrioritized(task);
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public Task getTask(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void clearAllTasks() {
        tasks.keySet().stream()
                .forEach(historyManager::remove);
        prioritizedTasks.removeIf(task -> task.getType() == TypeOfTask.TASK);
        tasks.clear();
    }

    @Override
    public void updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            checkAndAddToPrioritized(updatedTask);
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(tasks.get(id));
    }

    /// ///
    /// /// Блок эпиков

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void createEpic(Epic epic) {
        if (epic != null) {
            epic.setId(idCounter++);
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public Epic getEpic(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void clearAllEpics() {
        Stream.concat(epics.keySet().stream(), subtasks.keySet().stream()) // Объединяем стримы ключей
                .forEach(historyManager::remove);
        prioritizedTasks.removeIf(subtask -> subtask.getType() == TypeOfTask.SUBTASK); // Удаляем подзадачи из отсортированного списка
        epics.clear();
        subtasks.clear(); // Т.к. подзадачи - часть эпиков, удаляются и они
    }

    @Override
    public void updateEpic(Epic updatedEpic) {
        if (epics.containsKey(updatedEpic.getId())) {
            Epic epic = epics.get(updatedEpic.getId());
            epic.setName(updatedEpic.getName());
            epic.setDescription(updatedEpic.getDescription());
            epics.put(updatedEpic.getId(), epic);
        }
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            epics.get(id).getSubtasksId().stream() // Пробегая по списку, удаляем из хэшмапы подзадачи, которые там есть
                    .forEach(subtaskId -> {
                        subtasks.remove(subtaskId);
                        historyManager.remove(subtaskId);
                        prioritizedTasks.remove(subtasks.get(subtaskId));
                    });
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    /// ///
    /// /// Блок подзадач

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (subtask != null && epics.containsKey(subtask.getEpicId())) {
            subtask.setId(idCounter++);
            checkAndAddToPrioritized(subtask);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId()); // Добавление подзадачи в список к эпику
            epic.addSubtaskId(subtask.getId());
            updateEpicTime(subtask.getEpicId()); // Обновляем временные поля эпика
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public Subtask getSubtask(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void clearAllSubtasks() {
        subtasks.keySet().stream() // Очищаем историю
                .forEach(historyManager::remove);
        prioritizedTasks.removeIf(task -> task.getType() == TypeOfTask.SUBTASK);
        subtasks.clear();
        epics.values().stream().forEach(epic -> {
            epic.clearAllSubtasksId();
            updateEpicTime(epic.getId());
            updateEpicStatus(epic.getId());
        });
    }

    @Override
    public void updateSubtask(Subtask updatedSubtask) {
        if (subtasks.containsKey(updatedSubtask.getId())) {
            checkAndAddToPrioritized(updatedSubtask);
            subtasks.put(updatedSubtask.getId(), updatedSubtask);
        }
        updateEpicTime(updatedSubtask.getEpicId());
        updateEpicStatus(updatedSubtask.getEpicId());
    }

    @Override
    public void removeSubtask(int id) {
        int epicId = subtasks.get(id).getEpicId();
        if (subtasks.containsKey(id)) {
            subtasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(subtasks.get(id));
            Epic epic = epics.get(epicId);
            epic.removeSubtaskId(id);
        }
        updateEpicTime(epicId);
        updateEpicStatus(epicId);

    }

    /// ///
    /// История

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    /// ///
    /// Пересечения

    private boolean isOverlap(Task task1, Task task2) { // Проверка наложения отрезков
        return task1.getStartTime().isBefore(task2.getEndTime()) && task2.getStartTime().isBefore(task1.getEndTime());
    }

    private boolean isIntersection(Task task) {
        return getPrioritizedTasks().stream().filter(existingTask -> existingTask.getStartTime() != null && existingTask.getEndTime() != null) // Фильтрация null
                .anyMatch(existingTask -> isOverlap(existingTask, task)); // Поиск наложения
    }

    /// ///

    private void checkAndAddToPrioritized(Task task) {
        if (task.getStartTime() != null) {
            if (isIntersection(task)) {
                throw new IllegalArgumentException("Задача пересекается во времени");
            }
            prioritizedTasks.add(task);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        List<Integer> epicsSubtasksIds = epics.get(epicId).getSubtasksId();
        if (epicsSubtasksIds.isEmpty()) {
            return new ArrayList<>();
        }
        return epicsSubtasksIds.stream().map(subtasks::get).collect(Collectors.toList());
    }

    private void updateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);
        List<Subtask> subtasksByEpic = getSubtasksByEpicId(epicId);
        epic.updateEpicTimeFields(subtasksByEpic);
    }

    private void updateEpicStatus(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);

            List<Subtask> subtasksByEpic = getSubtasksByEpicId(epicId);
            if (subtasksByEpic.isEmpty()) {
                epic.setStatus(StatusOfTask.NEW);
                return;
            }

            boolean allDone = subtasksByEpic.stream().allMatch(subtask -> subtask.getStatus() == StatusOfTask.DONE);
            boolean allNew = subtasksByEpic.stream().allMatch(subtask -> subtask.getStatus() == StatusOfTask.NEW);

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
