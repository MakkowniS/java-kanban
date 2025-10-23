package ru.yandex.taskmanager.utility;

import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;
import ru.yandex.taskmanager.tasks.Epic;
import ru.yandex.taskmanager.tasks.Subtask;
import ru.yandex.taskmanager.tasks.Task;

import java.util.List;

public class GsonListTypes {
    public static final Type TASKS_LIST = new TypeToken<List<Task>>() {}.getType();
    public static final Type SUBTASKS_LIST = new TypeToken<List<Subtask>>() {}.getType();
    public static final Type EPICS_LIST = new TypeToken<List<Epic>>() {}.getType();
}
