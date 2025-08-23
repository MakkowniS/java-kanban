package ru.yandex.taskmanager.tasks;

public class Subtask extends Task {

    private int epicId; // Принадлежность к эпику

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(Subtask otherSubtask) {
        super(otherSubtask);
        this.epicId = otherSubtask.epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public Task objectCopy() {
        return new Subtask(this);
    }

    @Override
    public String toString() {
        return "ru.yandex.taskmanager.tasks.Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}
