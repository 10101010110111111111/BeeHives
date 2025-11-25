import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgramLoop{
    private Scanner scanner;
    private List<User> users;
    private Hives hivesManager;
    private Tasks tasksManager;
    private List<String> locations;
    private User currentUser;
    private DataManager dataManager;
    private ExecutorService executor;

    public ProgramLoop(List<User> users, List<BeeHive> hives, List<Task> tasks, List<String> locations, DataManager dataManager) {
        this.scanner = new Scanner(System.in);
        this.users = users;
        this.hivesManager = new Hives();
        this.tasksManager = new Tasks();
        this.locations = locations;
        this.dataManager = dataManager;
        this.executor = Executors.newSingleThreadExecutor();

        // Initialize hives manager with existing hives
        for (BeeHive hive : hives) {
            this.hivesManager.addBeeHive(hive);
        }

        // Initialize tasks manager with existing tasks
        for (Task task : tasks) {
            this.tasksManager.addTask(task);
        }
    }

    public void run() {
        System.out.println("=========================================");
        System.out.println("  Beekeeping Management System");
        System.out.println("=========================================");
        System.out.println();

        while (true) {
            // Check for data updates
            checkForDataUpdates();

            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    private void showLoginMenu() {
        System.out.println("--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = authenticateUser(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Welcome, " + currentUser.getName() + "!");
            System.out.println();
        } else {
            System.out.println("Invalid username or password!");
            System.out.println();
        }
    }

    private User authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    private void showMainMenu() {
        System.out.println("=========================================");
        System.out.println("  Main Menu - Logged in as: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        System.out.println("=========================================");
        System.out.println("1. View Tasks");
        System.out.println("2. View Hives");
        System.out.println("3. " + (currentUser.getRole().equals("admin") ? "Manage Hives" : "View Hive Details"));
        System.out.println("4. " + (currentUser.getRole().equals("admin") ? "Manage Tasks" : "Complete Task"));
        System.out.println("5. View Statistics");
        System.out.println("6. Logout");
        System.out.println("0. Exit");
        System.out.println();
        System.out.print("Select an option: ");

        String choice = scanner.nextLine().trim();
        System.out.println();

        switch (choice) {
            case "1":
                viewTasks();
                break;
            case "2":
                viewHives();
                break;
            case "3":
                if (currentUser.getRole().equals("admin")) {
                    manageHives();
                } else {
                    viewHiveDetails();
                }
                break;
            case "4":
                if (currentUser.getRole().equals("admin")) {
                    manageTasks();
                } else {
                    completeTask();
                }
                break;
            case "5":
                viewStatistics();
                break;
            case "6":
                logout();
                break;
            case "0":
                System.out.println("Goodbye!");
                // Save data before exiting
                executor.submit(() -> {
                    List<BeeHive> hivesList = hivesManager.getAllHives();
                    List<Task> tasksList = tasksManager.getAllTasks();
                    dataManager.saveData(users, hivesList, tasksList, locations);
                });
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                System.out.println();
        }
    }

    private void viewTasks() {
        System.out.println("--- My Tasks ---");
        List<Task> userTasks = tasksManager.getAllTasks(); // For now, show all tasks

        if (userTasks.isEmpty()) {
            System.out.println("No tasks assigned.");
        } else {
            for (int i = 0; i < userTasks.size(); i++) {
                Task task = userTasks.get(i);
                System.out.println((i + 1) + ". " + task.getName());
                System.out.println("   Description: " + task.getDescription());
                System.out.println("   Location: " + task.getLocation());
                System.out.println("   Status: " + task.getStatus());
                System.out.println();
            }
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void viewHives() {
        System.out.println("--- All Hives ---");
        List<BeeHive> hivesList = hivesManager.getAllHives();

        if (hivesList.isEmpty()) {
            System.out.println("No hives in the system.");
        } else {
            for (int i = 0; i < hivesList.size(); i++) {
                BeeHive hive = hivesList.get(i);
                System.out.println((i + 1) + ". Hive ID: " + hive.getId());
                System.out.println("   Tag Number: " + hive.getTagNumber());
                System.out.println("   Tag Color: " + hive.getTagColor());
                System.out.println("   Location: " + hive.getLocation());
                System.out.println("   Status: " + hive.getStatus());
                System.out.println("   Honey Level: " + hive.getHoneyLevel() + "%");
                System.out.println("   Has Queen: " + (hive.hasQueen() ? "Yes" : "No"));
                System.out.println();
            }
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void viewHiveDetails() {
        System.out.println("--- Hive Details ---");
        List<BeeHive> hivesList = hivesManager.getAllHives();
        if (hivesList.isEmpty()) {
            System.out.println("No hives in the system.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Display hives
        for (int i = 0; i < hivesList.size(); i++) {
            BeeHive hive = hivesList.get(i);
            System.out.println((i + 1) + ". Hive " + hive.getId() +
                    " (Tag: " + hive.getTagNumber() +
                    ", Location: " + hive.getLocation() + ")");
        }

        System.out.println();
        System.out.print("Select hive number (0 to cancel): ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("0")) {
            return;
        }

        try {
            int hiveIndex = Integer.parseInt(choice) - 1;
            if (hiveIndex >= 0 && hiveIndex < hivesList.size()) {
                BeeHive selectedHive = hivesList.get(hiveIndex);

                System.out.println("\n--- Hive " + selectedHive.getId() + " Details ---");
                System.out.println("Tag Number: " + selectedHive.getTagNumber());
                System.out.println("Tag Color: " + selectedHive.getTagColor());
                System.out.println("Location: " + selectedHive.getLocation());
                System.out.println("Status: " + selectedHive.getStatus());
                System.out.println("Honey Level: " + selectedHive.getHoneyLevel() + "%");
                System.out.println("Has Queen: " + (selectedHive.hasQueen() ? "Yes" : "No"));
                System.out.println("Notes: " + selectedHive.getNotes());

                // Show required actions
                List<String> actions = selectedHive.getRequiredActions();
                if (!actions.isEmpty()) {
                    System.out.println("\nRequired Actions:");
                    for (String action : actions) {
                        System.out.println("  - " + action);
                    }
                }

                // Show action options for employees
                System.out.println("\n--- Actions ---");
                System.out.println("1. Mark Queen as Missing");
                System.out.println("2. Mark Queen as Acquired");
                System.out.println("3. Mark Queen as Installed");
                System.out.println("4. Add Custom Action");
                System.out.println("0. Back to Main Menu");
                System.out.print("Select an action: ");

                String actionChoice = scanner.nextLine().trim();

                switch (actionChoice) {
                    case "1":
                        selectedHive.setHasQueen(false);
                        System.out.println("Queen marked as missing for hive " + selectedHive.getId());
                        // Save data after modification
                        executor.submit(() -> {
                            List<BeeHive> hivesListSave = hivesManager.getAllHives();
                            List<Task> tasksList = tasksManager.getAllTasks();
                            dataManager.saveData(users, hivesListSave, tasksList, locations);
                        });
                        break;
                    case "2":
                        selectedHive.markQueenAcquired();
                        System.out.println("Queen marked as acquired for hive " + selectedHive.getId());
                        // Save data after modification
                        executor.submit(() -> {
                            List<BeeHive> hivesListSave = hivesManager.getAllHives();
                            List<Task> tasksList = tasksManager.getAllTasks();
                            dataManager.saveData(users, hivesListSave, tasksList, locations);
                        });
                        break;
                    case "3":
                        selectedHive.markQueenInstalled();
                        System.out.println("Queen marked as installed for hive " + selectedHive.getId());
                        // Save data after modification
                        executor.submit(() -> {
                            List<BeeHive> hivesListSave = hivesManager.getAllHives();
                            List<Task> tasksList = tasksManager.getAllTasks();
                            dataManager.saveData(users, hivesListSave, tasksList, locations);
                        });
                        break;
                    case "4":
                        System.out.print("Enter custom action: ");
                        String customAction = scanner.nextLine().trim();
                        if (!customAction.isEmpty()) {
                            selectedHive.addRequiredAction(customAction);
                            System.out.println("Action added to hive " + selectedHive.getId());
                            // Save data after modification
                            executor.submit(() -> {
                                List<BeeHive> hivesListSave = hivesManager.getAllHives();
                                List<Task> tasksList = tasksManager.getAllTasks();
                                dataManager.saveData(users, hivesListSave, tasksList, locations);
                            });
                        }
                        break;
                    case "0":
                        // Return to main menu
                        break;
                    default:
                        System.out.println("Invalid action selection.");
                }
            } else {
                System.out.println("Invalid hive number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format.");
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void manageHives() {
        System.out.println("--- Manage Hives ---");
        System.out.println("1. Add New Hive");
        System.out.println("2. Update Hive Status");
        System.out.println("3. Add Required Action to Hive");
        System.out.println("4. Back to Main Menu");
        System.out.println();
        System.out.print("Select an option: ");

        String choice = scanner.nextLine().trim();
        System.out.println();

        switch (choice) {
            case "1":
                addNewHive();
                break;
            case "2":
                updateHiveStatus();
                break;
            case "3":
                addRequiredActionToHive();
                break;
            case "4":
                // Return to main menu
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                System.out.println();
        }
    }

    private void addNewHive() {
        System.out.println("--- Add New Hive ---");

        try {
            System.out.print("Tag Number: ");
            int tagNumber = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Tag Color: ");
            String tagColor = scanner.nextLine().trim();

            System.out.println("Available locations:");
            for (int i = 0; i < locations.size(); i++) {
                System.out.println((i + 1) + ". " + locations.get(i));
            }
            System.out.print("Select location (or enter new location): ");
            String locationInput = scanner.nextLine().trim();

            String location;
            try {
                int locationIndex = Integer.parseInt(locationInput) - 1;
                if (locationIndex >= 0 && locationIndex < locations.size()) {
                    location = locations.get(locationIndex);
                } else {
                    location = locationInput;
                    if (!locations.contains(location)) {
                        locations.add(location);
                    }
                }
            } catch (NumberFormatException e) {
                location = locationInput;
                if (!locations.contains(location)) {
                    locations.add(location);
                }
            }

            hivesManager.addBeeHive(tagNumber, tagColor, location);

            System.out.println("Hive added successfully.");
            // Save data after modification
            executor.submit(() -> {
                List<BeeHive> hivesList = hivesManager.getAllHives();
                List<Task> tasksList = tasksManager.getAllTasks();
                dataManager.saveData(users, hivesList, tasksList, locations);
            });
        } catch (NumberFormatException e) {
            System.out.println("Invalid tag number format.");
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void updateHiveStatus() {
        System.out.println("--- Update Hive Status ---");
        List<BeeHive> hivesList = hivesManager.getAllHives();
        if (hivesList.isEmpty()) {
            System.out.println("No hives in the system.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Display hives
        for (int i = 0; i < hivesList.size(); i++) {
            BeeHive hive = hivesList.get(i);
            System.out.println((i + 1) + ". Hive " + hive.getId() +
                    " (Tag: " + hive.getTagNumber() +
                    ", Location: " + hive.getLocation() + ")");
        }

        System.out.println();
        System.out.print("Select hive number (0 to cancel): ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("0")) {
            return;
        }

        try {
            int hiveIndex = Integer.parseInt(choice) - 1;
            if (hiveIndex >= 0 && hiveIndex < hivesList.size()) {
                BeeHive selectedHive = hivesList.get(hiveIndex);

                System.out.println("Current status: " + selectedHive.getStatus());
                System.out.print("New status (healthy/needs attention/queenless/empty): ");
                String newStatus = scanner.nextLine().trim();

                if (!newStatus.isEmpty()) {
                    selectedHive.setStatus(newStatus);
                    System.out.println("Status updated successfully.");
                }

                System.out.print("Honey level (0-100, current: " + selectedHive.getHoneyLevel() + "): ");
                String honeyLevelStr = scanner.nextLine().trim();
                if (!honeyLevelStr.isEmpty()) {
                    try {
                        int honeyLevel = Integer.parseInt(honeyLevelStr);
                        selectedHive.setHoneyLevel(honeyLevel);
                        System.out.println("Honey level updated successfully.");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid honey level format.");
                    }
                }

                System.out.print("Has queen? (y/n, current: " + (selectedHive.hasQueen() ? "y" : "n") + "): ");
                String hasQueenStr = scanner.nextLine().trim().toLowerCase();
                if (!hasQueenStr.isEmpty()) {
                    selectedHive.setHasQueen(hasQueenStr.equals("y") || hasQueenStr.equals("yes"));
                    System.out.println("Queen status updated successfully.");
                }

                System.out.print("Additional notes (optional): ");
                String notes = scanner.nextLine().trim();
                if (!notes.isEmpty()) {
                    selectedHive.addNotes(notes);
                    System.out.println("Notes added successfully.");
                }

                // Save data after modification
                executor.submit(() -> {
                    List<BeeHive> hivesListSave = hivesManager.getAllHives();
                    List<Task> tasksList = tasksManager.getAllTasks();
                    dataManager.saveData(users, hivesListSave, tasksList, locations);
                });
            } else {
                System.out.println("Invalid hive number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format.");
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void addRequiredActionToHive() {
        System.out.println("--- Add Required Action to Hive ---");
        List<BeeHive> hivesList = hivesManager.getAllHives();
        if (hivesList.isEmpty()) {
            System.out.println("No hives in the system.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Display hives
        for (int i = 0; i < hivesList.size(); i++) {
            BeeHive hive = hivesList.get(i);
            System.out.println((i + 1) + ". Hive " + hive.getId() +
                    " (Tag: " + hive.getTagNumber() +
                    ", Location: " + hive.getLocation() + ")");
        }

        System.out.println();
        System.out.print("Select hive number (0 to cancel): ");
        String choice = scanner.nextLine().trim();

        if (choice.equals("0")) {
            return;
        }

        try {
            int hiveIndex = Integer.parseInt(choice) - 1;
            if (hiveIndex >= 0 && hiveIndex < hivesList.size()) {
                BeeHive selectedHive = hivesList.get(hiveIndex);

                System.out.println("Predefined actions:");
                System.out.println("1. Need to acquire queen");
                System.out.println("2. Honey harvest needed");
                System.out.println("3. Hive inspection required");
                System.out.println("4. Add supers");
                System.out.println("5. Remove supers");
                System.out.println("6. Swarm prevention measures needed");
                System.out.println("7. Disease treatment required");
                System.out.println("8. Custom action");
                System.out.println();
                System.out.print("Select action (1-8): ");

                String actionChoice = scanner.nextLine().trim();
                String action = "";

                switch (actionChoice) {
                    case "1":
                        action = "Need to acquire queen";
                        break;
                    case "2":
                        action = "Honey harvest needed";
                        break;
                    case "3":
                        action = "Hive inspection required";
                        break;
                    case "4":
                        action = "Add supers";
                        break;
                    case "5":
                        action = "Remove supers";
                        break;
                    case "6":
                        action = "Swarm prevention measures needed";
                        break;
                    case "7":
                        action = "Disease treatment required";
                        break;
                    case "8":
                        System.out.print("Enter custom action: ");
                        action = scanner.nextLine().trim();
                        break;
                    default:
                        System.out.println("Invalid action selection.");
                        System.out.println("Press Enter to continue...");
                        scanner.nextLine();
                        return;
                }

                if (!action.isEmpty()) {
                    selectedHive.addRequiredAction(action);
                    System.out.println("Action '" + action + "' added to hive " + selectedHive.getId());
                    // Save data after modification
                    executor.submit(() -> {
                        List<BeeHive> hivesListSave = hivesManager.getAllHives();
                        List<Task> tasksList = tasksManager.getAllTasks();
                        dataManager.saveData(users, hivesListSave, tasksList, locations);
                    });
                }
            } else {
                System.out.println("Invalid hive number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input format.");
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void manageTasks() {
        System.out.println("--- Manage Tasks ---");
        System.out.println("1. Create New Task");
        System.out.println("2. View All Tasks");
        System.out.println("3. Back to Main Menu");
        System.out.println();
        System.out.print("Select an option: ");

        String choice = scanner.nextLine().trim();
        System.out.println();

        switch (choice) {
            case "1":
                createNewTask();
                break;
            case "2":
                viewAllTasks();
                break;
            case "3":
                // Return to main menu
                break;
            default:
                System.out.println("Invalid option. Please try again.");
                System.out.println();
        }
    }

    private void createNewTask() {
        System.out.println("--- Create New Task ---");

        System.out.print("Task name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Task name cannot be empty.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        System.out.print("Description: ");
        String description = scanner.nextLine().trim();

        System.out.println("Available locations:");
        for (int i = 0; i < locations.size(); i++) {
            System.out.println((i + 1) + ". " + locations.get(i));
        }
        System.out.print("Select location (or enter new location): ");
        String locationInput = scanner.nextLine().trim();

        String location;
        try {
            int locationIndex = Integer.parseInt(locationInput) - 1;
            if (locationIndex >= 0 && locationIndex < locations.size()) {
                location = locations.get(locationIndex);
            } else {
                location = locationInput;
                if (!locations.contains(location)) {
                    locations.add(location);
                }
            }
        } catch (NumberFormatException e) {
            location = locationInput;
            if (!locations.contains(location)) {
                locations.add(location);
            }
        }

        tasksManager.addTask(name, description, location);

        System.out.println("Task created successfully.");
        // Save data after modification
        executor.submit(() -> {
            List<BeeHive> hivesList = hivesManager.getAllHives();
            List<Task> tasksList = tasksManager.getAllTasks();
            dataManager.saveData(users, hivesList, tasksList, locations);
        });
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void viewAllTasks() {
        System.out.println("--- All Tasks ---");
        List<Task> allTasks = tasksManager.getAllTasks();

        if (allTasks.isEmpty()) {
            System.out.println("No tasks in the system.");
        } else {
            for (int i = 0; i < allTasks.size(); i++) {
                Task task = allTasks.get(i);
                System.out.println((i + 1) + ". " + task.getName());
                System.out.println("   Description: " + task.getDescription());
                System.out.println("   Location: " + task.getLocation());
                System.out.println("   Status: " + task.getStatus());
                System.out.println();
            }
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void completeTask() {
        System.out.println("--- Complete Task ---");
        // For simplicity, we'll let employees complete any task
        // In a real system, you would filter by assigned tasks
        List<Task> allTasks = tasksManager.getAllTasks();

        if (allTasks.isEmpty()) {
            System.out.println("No tasks in the system.");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        // Display tasks
        for (int i = 0; i < allTasks.size(); i++) {
            Task task = allTasks.get(i);
            System.out.println((i + 1) + ". " + task.getName() +
                    " (Location: " + task.getLocation() +
                    ", Status: " + task.getStatus() + ")");
        }

        System.out.println();
        System.out.print("Select task number to complete (0 to cancel): ");
        String taskChoice = scanner.nextLine().trim();

        if (taskChoice.equals("0")) {
            return;
        }

        try {
            int taskIndex = Integer.parseInt(taskChoice) - 1;
            if (taskIndex >= 0 && taskIndex < allTasks.size()) {
                Task selectedTask = allTasks.get(taskIndex);
                selectedTask.complete();
                System.out.println("Task '" + selectedTask.getName() + "' marked as completed.");
                // Save data after modification
                executor.submit(() -> {
                    List<BeeHive> hivesList = hivesManager.getAllHives();
                    List<Task> tasksList = tasksManager.getAllTasks();
                    dataManager.saveData(users, hivesList, tasksList, locations);
                });
            } else {
                System.out.println("Invalid task number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid task number format.");
        }

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void viewStatistics() {
        System.out.println("--- System Statistics ---");
        System.out.println("Users: " + users.size());
        System.out.println("Hives: " + hivesManager.getHiveCount());
        System.out.println("Tasks: " + tasksManager.getTaskCount());
        System.out.println("Locations: " + locations.size());
        System.out.println();

        // Hive statistics
        System.out.println("--- Hive Statistics ---");
        System.out.println("Total Hives: " + hivesManager.getHiveCount());

        // Count hives by location
        System.out.println("Location Distribution:");
        for (String location : locations) {
            int count = hivesManager.getHiveCountByLocation(location);
            System.out.println("  " + location + ": " + count);
        }

        // Task statistics
        System.out.println();
        System.out.println(tasksManager.getTaskStatistics());

        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    private void logout() {
        System.out.println("Goodbye, " + currentUser.getName() + "!");
        currentUser = null;
        System.out.println();
    }

    private void checkForDataUpdates() {
        // Check for data file modifications every few operations
        if (dataManager.isDataFileModified()) {
            System.out.println("Data updated by another user. Reloading...");
            dataManager.loadData(users, hivesManager.getAllHives(), tasksManager.getAllTasks(), locations);
        }
    }

    // Existing methods (keeping the original methods)
    private void gettingStarted(){
        System.out.println("Getting started with Beekeeping Management System");
    }

    private boolean getCorrectAnswer(int from,int to){
        System.out.println("Getting correct answer in range " + from + " to " + to);
        return true;
    }
}