import java.util.ArrayList;
import java.util.List;

public class Tasks {
    private List<Task> tasks;

    public Tasks() {
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void addTask(String name, String description, String location) {
        tasks.add(new Task(name, description, location));
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public List<Task> getTasksByLocation(String location) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getLocation().equals(location)) {
                result.add(task);
            }
        }
        return result;
    }

    public List<Task> getTasksByStatus(String status) {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getStatus().equals(status)) {
                result.add(task);
            }
        }
        return result;
    }

    public List<Task> getPendingTasks() {
        return getTasksByStatus("pending");
    }

    public List<Task> getCompletedTasks() {
        return getTasksByStatus("completed");
    }

    public List<Task> getOverdueTasks() {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isOverdue()) {
                result.add(task);
            }
        }
        return result;
    }

    public int getTaskCount() {
        return tasks.size();
    }

    public void removeTask(Task task) {
        tasks.remove(task);
    }

    public String getTaskStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Task Statistics ===\n");
        stats.append("Total Tasks: ").append(getTaskCount()).append("\n");
        stats.append("Pending Tasks: ").append(getPendingTasks().size()).append("\n");
        stats.append("Completed Tasks: ").append(getCompletedTasks().size()).append("\n");
        stats.append("Overdue Tasks: ").append(getOverdueTasks().size()).append("\n");
        return stats.toString();
    }
}