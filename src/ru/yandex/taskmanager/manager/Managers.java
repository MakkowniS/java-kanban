package ru.yandex.taskmanager.manager;

public class Managers {

    public TaskManager getDefault(){
        return new InMemoryTaskManager();
    }
}
