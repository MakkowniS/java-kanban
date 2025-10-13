package ru.yandex.taskmanager.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private StatusOfTask status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description) { // Конструктор для обычных задач и подзадач
        this.name = name;
        this.description = description;
        this.status = StatusOfTask.NEW;
        this.duration = Duration.ZERO;
    }

    public Task(Task otherTask) { // Конструктор для копирования в историю
        this.name = otherTask.getName();
        this.description = otherTask.getDescription();
        this.id = otherTask.getId();
        this.status = otherTask.getStatus();
        this.duration = otherTask.getDuration();
        this.startTime = otherTask.getStartTime();
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

    public TypeOfTask getType() {
        return TypeOfTask.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTimeNow() {
        this.startTime = LocalDateTime.now(ZoneId.systemDefault());
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Task objectCopy() {
        return new Task(this);
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
        return "ru.yandex.taskmanager.tasks.Task{" + "name='" + name + '\'' + ", description='" + description + '\'' + ", id=" + id + ", status=" + status + '}';
    }
}
