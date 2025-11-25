import java.util.ArrayList;
import java.util.List;

/**
 * BeeHive class represents a beehive in the beekeeping system
 * Each hive has an ID, tag color, location, and status information
 */
public class BeeHive {
    private static int nextId = 1;
    private int id;
    private int tagNumber;
    private String tagColor;
    private String location;
    private String status; // "healthy", "needs attention", "queenless", "empty", etc.
    private int honeyLevel; // 0-100 percentage
    private boolean hasQueen;
    private String notes;
    private List<String> requiredActions; // List of actions needed for this hive

    /**
     * Constructor for BeeHive
     * @param tagNumber The tag number of the hive
     * @param tagColor The color of the tag
     * @param location The location where the hive is placed
     */
    public BeeHive(int tagNumber, String tagColor, String location) {
        this.id = nextId++;
        this.tagNumber = tagNumber;
        this.tagColor = tagColor;
        this.location = location;
        this.status = "unknown";
        this.honeyLevel = 0;
        this.hasQueen = true;
        this.notes = "";
        this.requiredActions = new ArrayList<>();
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public int getTagNumber() {
        return tagNumber;
    }

    public String getTagColor() {
        return tagColor;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public int getHoneyLevel() {
        return honeyLevel;
    }

    public boolean hasQueen() {
        return hasQueen;
    }

    public String getNotes() {
        return notes;
    }

    public List<String> getRequiredActions() {
        return requiredActions;
    }

    // Setter methods
    public void setId(int id) {
        this.id = id;
        // Update nextId if necessary
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    public void setTagNumber(int tagNumber) {
        this.tagNumber = tagNumber;
    }

    public void setTagColor(String tagColor) {
        this.tagColor = tagColor;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setHoneyLevel(int honeyLevel) {
        if (honeyLevel >= 0 && honeyLevel <= 100) {
            this.honeyLevel = honeyLevel;
        }
    }

    public void setHasQueen(boolean hasQueen) {
        this.hasQueen = hasQueen;
        // If queen is missing, add to required actions
        if (!hasQueen && !requiredActions.contains("Need to acquire queen")) {
            requiredActions.add("Need to acquire queen");
        }
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Add notes to the hive
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
     * Add a required action for this hive
     * @param action The action needed
     */
    public void addRequiredAction(String action) {
        if (!requiredActions.contains(action)) {
            requiredActions.add(action);
        }
    }

    /**
     * Mark that a queen has been acquired for this hive
     */
    public void markQueenAcquired() {
        if (!hasQueen) {
            requiredActions.remove("Need to acquire queen");
            requiredActions.add("Queen acquired - needs installation");
        }
    }

    /**
     * Mark that a queen has been installed in this hive
     */
    public void markQueenInstalled() {
        if (!hasQueen) {
            hasQueen = true;
            requiredActions.remove("Queen acquired - needs installation");
            addNotes("Queen installed");
        }
    }

    /**
     * Check if the hive needs attention
     * @return true if the hive needs attention, false otherwise
     */
    public boolean needsAttention() {
        return status.equals("needs attention") || status.equals("queenless") ||
                status.equals("empty") || honeyLevel < 10 || !hasQueen || !requiredActions.isEmpty();
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
        BeeHive.nextId = nextId;
    }

    @Override
    public String toString() {
        return "BeeHive{" +
                "id=" + id +
                ", tagNumber=" + tagNumber +
                ", tagColor='" + tagColor + '\'' +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                ", honeyLevel=" + honeyLevel +
                ", hasQueen=" + hasQueen +
                ", requiredActions=" + requiredActions +
                '}';
    }
}