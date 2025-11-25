import java.io.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;

/**
 * DataManager class handles reading and writing data to DataFile.txt
 * It manages persistence for users, hives, tasks, and locations
 */
public class DataManager {
    private static final String DATA_FILE = "DataFile.txt";
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private long lastModified = 0;

    /**
     * Save all data to the data file
     * @param users List of users
     * @param hives List of hives
     * @param tasks List of tasks
     * @param locations List of locations
     */
    public void saveData(List<User> users, List<BeeHive> hives, List<Task> tasks, List<String> locations) {
        lock.writeLock().lock();
        try (FileWriter fileWriter = new FileWriter(DATA_FILE);
             FileChannel channel = FileChannel.open(new File(DATA_FILE).toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE);
             FileLock lock = channel.lock()) {

            PrintWriter writer = new PrintWriter(fileWriter);

            // Write users
            writer.println("===USERS===");
            for (User user : users) {
                writer.println(user.getUsername() + "|" + user.getPassword() + "|" +
                        user.getName() + "|" + user.getRole());
            }

            // Write locations
            writer.println("===LOCATIONS===");
            for (String location : locations) {
                writer.println(location);
            }

            // Write hives
            writer.println("===HIVES===");
            // Save the next ID for hives
            writer.println("NEXT_HIVE_ID:" + BeeHive.getNextId());
            for (BeeHive hive : hives) {
                writer.println(hive.getId() + "|" + hive.getTagNumber() + "|" +
                        hive.getTagColor() + "|" + hive.getLocation() + "|" +
                        hive.getStatus() + "|" + hive.getHoneyLevel() + "|" +
                        hive.hasQueen() + "|" + hive.getNotes());

                // Write required actions for this hive
                List<String> actions = hive.getRequiredActions();
                if (!actions.isEmpty()) {
                    writer.println("ACTIONS:" + hive.getId());
                    for (String action : actions) {
                        writer.println(action);
                    }
                    writer.println("ENDACTIONS:" + hive.getId());
                }
            }

            // Write tasks
            writer.println("===TASKS===");
            // Save the next ID for tasks
            writer.println("NEXT_TASK_ID:" + Task.getNextId());
            for (Task task : tasks) {
                writer.println(task.getId() + "|" + task.getName() + "|" +
                        task.getDescription() + "|" + task.getLocation() + "|" +
                        task.getStatus() + "|" + task.getNotes());
            }

            writer.flush();
            System.out.println("Data saved successfully to " + DATA_FILE);
            // Update the last modified time
            lastModified = new File(DATA_FILE).lastModified();
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }


    public boolean isDataFileModified() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            long currentModified = file.lastModified();
            if (currentModified > lastModified) {
                lastModified = currentModified;
                return true;
            }
        }
        return false;
    }

    public void loadData(List<User> users, List<BeeHive> hives, List<Task> tasks, List<String> locations) {
        lock.readLock().lock();
        try (FileReader fileReader = new FileReader(DATA_FILE);
             BufferedReader reader = new BufferedReader(fileReader)) {

            // Clear existing data
            users.clear();
            // Clear the passed lists
            hives.clear();
            tasks.clear();
            locations.clear();

            String line;
            String section = "";

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("===")) {
                    section = line;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    continue;
                }

                switch (section) {
                    case "===USERS===":
                        parseUser(line, users);
                        break;
                    case "===LOCATIONS===":
                        locations.add(line);
                        break;
                    case "===HIVES===":
                        if (line.startsWith("NEXT_HIVE_ID:")) {
                            try {
                                int nextId = Integer.parseInt(line.substring(13));
                                BeeHive.setNextId(nextId);
                            } catch (NumberFormatException e) {
                                System.err.println("Error parsing next hive ID: " + line);
                            }
                        } else {
                            parseHive(line, hives, reader);
                        }
                        break;
                    case "===TASKS===":
                        if (line.startsWith("NEXT_TASK_ID:")) {
                            try {
                                int nextId = Integer.parseInt(line.substring(13));
                                Task.setNextId(nextId);
                            } catch (NumberFormatException e) {
                                System.err.println("Error parsing next task ID: " + line);
                            }
                        } else {
                            parseTask(line, tasks);
                        }
                        break;
                }
            }

            System.out.println("Data loaded successfully from " + DATA_FILE);
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
            // Initialize with default data if file doesn't exist
            initializeDefaultData(users, hives, tasks, locations);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void parseUser(String line, List<User> users) {
        String[] parts = line.split("\\|");
        if (parts.length == 4) {
            users.add(new User(parts[0], parts[1], parts[2], parts[3]));
        }
    }

    private void parseHive(String line, List<BeeHive> hives, BufferedReader reader) throws IOException {
        String[] parts = line.split("\\|");
        if (parts.length == 8) {
            try {
                int id = Integer.parseInt(parts[0]);
                int tagNumber = Integer.parseInt(parts[1]);
                String tagColor = parts[2];
                String location = parts[3];
                String status = parts[4];
                int honeyLevel = Integer.parseInt(parts[5]);
                boolean hasQueen = Boolean.parseBoolean(parts[6]);
                String notes = parts[7];

                BeeHive hive = new BeeHive(tagNumber, tagColor, location);
                // Set the ID to match the saved ID
                hive.setId(id);
                hive.setLocation(location);
                hive.setStatus(status);
                hive.setHoneyLevel(honeyLevel);
                hive.setHasQueen(hasQueen);
                hive.setNotes(notes);

                // Check for actions
                String nextLine = reader.readLine();
                if (nextLine != null && nextLine.startsWith("ACTIONS:" + id)) {
                    List<String> actions = new ArrayList<>();
                    while ((nextLine = reader.readLine()) != null &&
                            !nextLine.startsWith("ENDACTIONS:" + id)) {
                        actions.add(nextLine);
                    }
                    // Add actions to hive
                    for (String action : actions) {
                        hive.addRequiredAction(action);
                    }
                }

                hives.add(hive);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing hive data: " + line);
            }
        }
    }

    private void parseTask(String line, List<Task> tasks) {
        String[] parts = line.split("\\|");
        if (parts.length == 6) {
            try {
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String description = parts[2];
                String location = parts[3];
                String status = parts[4];
                String notes = parts[5];

                Task task = new Task(name, description, location);
                task.setId(id);
                task.setStatus(status);
                task.setNotes(notes);

                tasks.add(task);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing task data: " + line);
            }
        }
    }

    private void initializeDefaultData(List<User> users, List<BeeHive> hives, List<Task> tasks, List<String> locations) {
        System.out.println("Initializing with default data...");

        // Add sample users
        users.add(new User("pavel", "password123", "Pavel", "admin"));
        users.add(new User("worker1", "worker123", "Worker One", "employee"));
        users.add(new User("worker2", "worker123", "Worker Two", "employee"));

        // Add sample locations
        locations.add("Forest Location");
        locations.add("Meadow Location");
        locations.add("Garden Location");

        // Add sample hives
        hives.add(new BeeHive(101, "Yellow", "Forest Location"));
        hives.add(new BeeHive(102, "Blue", "Forest Location"));
        hives.add(new BeeHive(201, "Red", "Meadow Location"));
        hives.add(new BeeHive(202, "Green", "Meadow Location"));
        hives.add(new BeeHive(301, "Orange", "Garden Location"));

        // Add sample tasks
        Task task1 = new Task("Check Hive 101", "Inspect hive 101 for queen presence and honey levels", "Forest Location");
        tasks.add(task1);

        Task task2 = new Task("Harvest Honey", "Collect honey from hives in Meadow Location", "Meadow Location");
        tasks.add(task2);

        Task task3 = new Task("Replace Queen", "Queen missing in Hive 201, install new queen", "Meadow Location");
        tasks.add(task3);
    }
}