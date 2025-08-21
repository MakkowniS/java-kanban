package ru.yandex.taskmanager.tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtasksId;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(Epic otherEpic) {
        super(otherEpic);
        this.subtasksId = new ArrayList<>(otherEpic.subtasksId);
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubtaskId(int subtaskId) {
        subtasksId.add(subtaskId);
    } // Добавить подзадачу к эпику

    public void removeSubtaskId(int subtaskId) {
        subtasksId.remove(Integer.valueOf(subtaskId));
    } // Убрать подзадачу из эпика

    public void clearAllSubtasksId() {
        subtasksId.clear();
    }

    @Override
    public String toString() {
        return "ru.yandex.taskmanager.tasks.Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() + '\'' +
                ", status=" + getStatus() +
                ", subtasksId=" + subtasksId +
                '}';
    }
}
