import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Initialize data structures
        List<User> users = new ArrayList<>();
        List<BeeHive> hives = new ArrayList<>();
        List<Task> tasks = new ArrayList<>();
        List<String> locations = new ArrayList<>();

        // Initialize data manager
        DataManager dataManager = new DataManager();

        // Load existing data or initialize with defaults
        dataManager.loadData(users, hives, tasks, locations);

        // Create program loop and run
        ProgramLoop program = new ProgramLoop(users, hives, tasks, locations, dataManager);
        program.run();
    }
}