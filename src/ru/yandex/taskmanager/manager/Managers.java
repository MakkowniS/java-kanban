package ru.yandex.taskmanager.manager;

import ru.yandex.taskmanager.tasks.Task;

public class Managers {

    private Managers() {

    }
    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }


}
