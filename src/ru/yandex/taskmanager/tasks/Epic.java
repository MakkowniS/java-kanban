package ru.yandex.taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final ArrayList<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(Epic otherEpic) {
        super(otherEpic);
        this.subtasksId = new ArrayList<>(otherEpic.subtasksId);
        this.endTime = otherEpic.endTime;
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubtaskId(int subtaskId) {
        if (subtaskId != this.getId()) {
            subtasksId.add(subtaskId);
        }
    } // Добавить подзадачу к эпику

    public void removeSubtaskId(int subtaskId) {
        subtasksId.remove(Integer.valueOf(subtaskId));
    } // Убрать подзадачу из эпика

    public void clearAllSubtasksId() {
        subtasksId.clear();
    }

    public void updateEpicTimeFields(List<Subtask> subtasks) {
        if (subtasksId.isEmpty()) {
            setDuration(Duration.ZERO);
            setStartTime(null);
            this.endTime = null;
            return;
        }

        Duration durationSum = Duration.ZERO;
        LocalDateTime firstStartTime = null;
        LocalDateTime lastEndTime = null;

        for (Subtask subtask : subtasks) {
            if (!subtasksId.contains(subtask.getId())) {
                continue;
            }

            if (subtask.getDuration() != Duration.ZERO) { // Суммирование продолжительности подзадач
                durationSum = durationSum.plus(subtask.getDuration());
            }

            LocalDateTime subtaskStartTime = subtask.getStartTime(); // Поиск самого раннего начала подзадачи
            if (firstStartTime == null || subtaskStartTime.isBefore(firstStartTime)) {
                firstStartTime = subtaskStartTime;
            }

            LocalDateTime subtaskEndTime = subtask.getEndTime(); // Поиск самого позднего конца подзадачи
            if (lastEndTime == null || subtaskEndTime.isAfter(lastEndTime)) {
                lastEndTime = subtaskEndTime;
            }
        }
        setDuration(durationSum);
        setStartTime(firstStartTime);
        this.endTime = lastEndTime;
    }

    @Override
    public TypeOfTask getType() {
        return TypeOfTask.EPIC;
    }

    @Override
    public Task objectCopy() {
        return new Epic(this);
    }

    @Override
    public String toString() {
        return "ru.yandex.taskmanager.tasks.Epic{" + "name='" + getName() + '\'' + ", description='" + getDescription() + '\'' + ", id=" + getId() + '\'' + ", status=" + getStatus() + ", subtasksId=" + subtasksId + '}';
    }
}
