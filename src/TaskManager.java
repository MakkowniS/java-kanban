import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>(); // Таблица Задач
    private HashMap<Integer, Epic> epics = new HashMap<>(); // Таблица Эпиков
    private HashMap<Integer, Subtask> subtasks = new HashMap<>(); // Таблица Подзадач
    private int idCounter = 1; // Сквозной счётчик id


    ////// Блок обычных задач

    public ArrayList getAllTasks(){
        return new ArrayList(tasks.values());
    }

    public void createTask(Task task){
        if (task != null) {
            task.setId(idCounter++);
            tasks.put(task.getId(), task);
        }
    }

    public Object getTask(int id){
        return tasks.get(id);
    }

    public void clearAllTasks(){
        tasks.clear();
    }

    public void updateTask(Task updatedTask){
        if (tasks.containsKey(updatedTask.getId())){
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    public void removeTask(int id){
        tasks.remove(id);
    }

    //////
    ////// Блок эпиков

    public ArrayList getAllEpics(){
        return new ArrayList<>(epics.values());
    }

    public void createEpic(Epic epic){
        if (epic != null) {
            epic.setId(idCounter++);
            epics.put(epic.getId(), epic);
        }
    }

    public Object getEpic(int id){
        return epics.get(id);
    }

    public void clearAllEpics(){
        epics.clear();
    }

    public void updateEpic(Epic updatedEpic){
        if (epics.containsKey(updatedEpic.getId())){
            Epic epic = epics.get(updatedEpic.getId());
            epic.setName(updatedEpic.getName());
            epic.setDescription(updatedEpic.getDescription());
            epics.put(updatedEpic.getId(), epic);
        }
    }

    public void removeEpic(int id){
        epics.remove(id);
    }

    /// ///
    /// /// Блок подзадач

    public ArrayList getAllSubtasks(){
        return new ArrayList<>(subtasks.values());
    }
    public void createSubtask(Subtask subtask){
        if (subtask != null) {
            subtask.setId(idCounter++);
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId()); // Добавление подзадачи к эпику
            epic.addSubtaskId(subtask.getId());
            // Обновить статус эпика
        }
    }



    public Object getSubtask(int id){
        return subtasks.get(id);
    }

    public void updateEpicStatus(int epicId){
        Epic epic = epics.get(epicId);


    }
}
