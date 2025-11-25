import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserManager class handles user authentication and management
 * It stores all users and provides methods for authentication and user operations
 */
public class UserManager {
    private Map<String, User> users;

    /**
     * Constructor for UserManager
     * Initializes the user storage
     */
    public UserManager() {
        users = new HashMap<>();
    }

    /**
     * Add a new user to the system
     * @param user The user to add
     */
    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    /**
     * Remove a user from the system
     * @param username The username of the user to remove
     */
    public void removeUser(String username) {
        users.remove(username);
    }

    /**
     * Get a user by username
     * @param username The username to search for
     * @return The user if found, null otherwise
     */
    public User getUser(String username) {
        return users.get(username);
    }

    /**
     * Get all users in the system
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Authenticate a user with username and password
     * @param username The username to authenticate
     * @param password The password to authenticate
     * @return The authenticated user if successful, null otherwise
     */
    public User authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    /**
     * Check if a username already exists
     * @param username The username to check
     * @return true if the username exists, false otherwise
     */
    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    /**
     * Get all users with a specific role
     * @param role The role to filter by
     * @return A list of users with the specified role
     */
    public List<User> getUsersByRole(String role) {
        List<User> result = new ArrayList<>();
        for (User user : users.values()) {
            if (user.getRole().equals(role)) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * Get the number of users in the system
     * @return The number of users
     */
    public int getUserCount() {
        return users.size();
    }
}