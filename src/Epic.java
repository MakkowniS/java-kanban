import java.util.ArrayList;

public class Epic extends Task{
     // Число входящих подзадач
    private ArrayList<Integer> subtasksId;


    public Epic(String name, String description, ArrayList<Integer> subtaskId) {
        super(name, description);
        this.subtasksId = subtaskId;
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubtaskId(int subtaskId){
        subtasksId.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId){
        subtasksId.remove(subtaskId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() + '\'' +
                ", status=" + getStatus() +
                ", subtasksId=" + subtasksId +
                '}';
    }
}
