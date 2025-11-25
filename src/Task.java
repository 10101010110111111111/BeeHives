import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Task class represents a task in the beekeeping system
 * Tasks can be assigned to users and have various statuses
 */
public class Task {
    private static int nextId = 1;
    private int id;
    private String name;
    private String description;
    private String location;
    private String status; // "pending", "in progress", "completed"
    private LocalDateTime createdDate;
    private LocalDateTime completedDate;
    private String notes;

    /**
     * Constructor for Task
     * @param name The name of the task
     * @param description The description of the task
     * @param location The location where the task should be performed
     */
    public Task(String name, String description, String location) {
        this.id = nextId++;
        this.name = name;
        this.description = description;
        this.location = location;
        this.status = "pending";
        this.createdDate = LocalDateTime.now();
        this.notes = "";
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getCompletedDate() {
        return completedDate;
    }

    public String getNotes() {
        return notes;
    }

    // Setter methods
    public void setId(int id) {
        this.id = id;
        // Update nextId if necessary
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Set the status of the task
     * @param status The new status
     */
    public void setStatus(String status) {
        this.status = status;
        if (status.equals("completed") && completedDate == null) {
            completedDate = LocalDateTime.now();
        }
    }

    /**
     * Mark the task as completed
     */
    public void complete() {
        setStatus("completed");
        completedDate = LocalDateTime.now();
    }

    /**
     * Add notes to the task
     * @param notes Additional notes to add
     */
    public void addNotes(String notes) {
        if (this.notes.isEmpty()) {
            this.notes = notes;
        } else {
            this.notes += "; " + notes;
        }
    }

    /**
     * Check if the task is overdue
     * @return true if the task is overdue, false otherwise
     */
    public boolean isOverdue() {
        // For simplicity, we'll consider tasks overdue if they're pending for more than 7 days
        return status.equals("pending") &&
                createdDate.isBefore(LocalDateTime.now().minusDays(7));
    }

    /**
     * Get a formatted string representation of the creation date
     * @return Formatted creation date
     */
    public String getFormattedCreatedDate() {
        return createdDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    /**
     * Get a formatted string representation of the completion date
     * @return Formatted completion date or "Not completed" if not completed
     */
    public String getFormattedCompletedDate() {
        if (completedDate != null) {
            return completedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } else {
            return "Not completed";
        }
    }

    /**
     * Get the next available ID
     * @return the next ID
     */
    public static int getNextId() {
        return nextId;
    }

    /**
     * Set the next available ID
     * @param nextId the next ID to set
     */
    public static void setNextId(int nextId) {
        Task.nextId = nextId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + getFormattedCreatedDate() +
                '}';
    }
}