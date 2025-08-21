package ru.yandex.taskmanager.tasks;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private StatusOfTask status;

    public Task(String name, String description) { // Конструктор для обычных задач и подзадач
        this.name = name;
        this.description = description;
        this.status = StatusOfTask.NEW;
    }

    public Task(Task otherTask){
        this.name = otherTask.getName();
        this.description = otherTask.getDescription();
        this.id = otherTask.getId();
        this.status = otherTask.getStatus();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public StatusOfTask getStatus() {
        return status;
    }

    public void setStatus(StatusOfTask status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ru.yandex.taskmanager.tasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}
