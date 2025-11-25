import java.util.ArrayList;
import java.util.List;

/**
 * User class represents a user in the beekeeping system
 * Users can be administrators (like Pavel) or employees (workers)
 * Each user has credentials, personal information, and a role
 */
public class User {
    private String username;
    private String password;
    private String name;
    private String role; // "admin" or "employee"
    private List<Task> assignedTasks;

    /**
     * Constructor for User
     * @param username The username for login
     * @param password The password for login
     * @param name The real name of the user
     * @param role The role of the user (admin/employee)
     */
    public User(String username, String password, String name, String role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.role = role;
        this.assignedTasks = new ArrayList<>();
    }

    // Getter methods
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public List<Task> getAssignedTasks() {
        return assignedTasks;
    }

    // Setter methods
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Assign a task to this user
     * @param task The task to assign
     */
    public void assignTask(Task task) {
        if (!assignedTasks.contains(task)) {
            assignedTasks.add(task);
        }
    }

    /**
     * Remove a task from this user
     * @param task The task to remove
     */
    public void removeTask(Task task) {
        assignedTasks.remove(task);
    }

    /**
     * Check if this user has a specific task
     * @param task The task to check
     * @return true if the user has the task, false otherwise
     */
    public boolean hasTask(Task task) {
        return assignedTasks.contains(task);
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", assignedTasks=" + assignedTasks.size() +
                '}';
    }
}