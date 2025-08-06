import java.util.ArrayList;

public class Epic extends Task{

    private ArrayList<Integer> subtasksId;


    public Epic(String name, String description) {
        super(name, description);
        this.subtasksId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    public void addSubtaskId(int subtaskId){
        subtasksId.add(subtaskId);
    } // Добавить подзадачу к эпику

    public void removeSubtaskId(int subtaskId){
        subtasksId.remove(subtaskId);
    } // Убрать подзадачу из эпика

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
