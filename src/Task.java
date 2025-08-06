public class Task {
    String name;
    String description;
    StatusOfTask status;


    public Task(String name, String description, StatusOfTask status) { // Конструктор для обычных задач и подзадач
        TaskManager.id++;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String description) { // Конструктор для эпиков
        TaskManager.id++;
        this.name = name;
        this.description = description;
    }


}
