import java.util.HashMap;

public class Epic extends Task{
    int numberOfSubtasks; // Число входящих подзадач
    HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description, int numberOfSubtasks) {
        super(name, description);
        this.numberOfSubtasks = numberOfSubtasks; // Указывается число подзадач
        this.status = StatusOfTask.NEW; // По умолчанию Эпик - новый
        this.subtasks = new HashMap<>(); // Таблица подзадач

    }
}
