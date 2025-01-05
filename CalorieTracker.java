import java.sql.*;
import java.util.Scanner;

public class CalorieTracker {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/caloriedb"; // Change your DB URL
    private static final String DB_USER = "root"; // Change your DB username
    private static final String DB_PASS = "password"; // Change your DB password

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            System.out.println("Welcome to the Calorie Tracker!");

            while (true) {
                System.out.println("\n1. Register");
                System.out.println("2. Login");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        registerUser(scanner, connection);
                        break;

                    case 2:
                        loginUser(scanner, connection);
                        break;

                    default:
                        System.out.println("Invalid choice. Try again.");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database.");
            e.printStackTrace();
        }
    }

    // Register a new user
    private static void registerUser(Scanner scanner, Connection connection) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.executeUpdate();
            System.out.println("Registration successful!");
        } catch (SQLException e) {
            System.out.println("Error registering user.");
            e.printStackTrace();
        }
    }

    // Login the user
    private static void loginUser(Scanner scanner, Connection connection) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.println("Login successful!");
                int userId = resultSet.getInt("id");
                userMenu(scanner, connection, userId);
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException e) {
            System.out.println("Error logging in.");
            e.printStackTrace();
        }
    }

    // User's menu after login
    private static void userMenu(Scanner scanner, Connection connection, int userId) {
        while (true) {
            System.out.println("\n1. Add Meal");
            System.out.println("2. View Meals");
            System.out.println("3. Set Calorie Goal");
            System.out.println("4. View Calorie Goal");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addMeal(scanner, connection, userId);
                    break;
                case 2:
                    viewMeals(connection, userId);
                    break;
                case 3:
                    setCalorieGoal(scanner, connection, userId);
                    break;
                case 4:
                    viewCalorieGoal(connection, userId);
                    break;
                case 5:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Add meal to the database
    private static void addMeal(Scanner scanner, Connection connection, int userId) {
        System.out.print("Enter meal name: ");
        String mealName = scanner.nextLine();
        System.out.print("Enter meal category (e.g., Breakfast, Lunch, Dinner): ");
        String category = scanner.nextLine();
        System.out.print("Enter calories: ");
        int calories = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            String query = "INSERT INTO meals (user_id, meal_name, category, calories, time) VALUES (?, ?, ?, ?, NOW())";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setString(2, mealName);
            statement.setString(3, category);
            statement.setInt(4, calories);
            statement.executeUpdate();
            System.out.println("Meal added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding meal.");
            e.printStackTrace();
        }
    }

    // View all meals for the user
    private static void viewMeals(Connection connection, int userId) {
        try {
            String query = "SELECT * FROM meals WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            System.out.println("Meals:");
            while (resultSet.next()) {
                String mealName = resultSet.getString("meal_name");
                String category = resultSet.getString("category");
                int calories = resultSet.getInt("calories");
                String time = resultSet.getString("time");
                System.out.println(mealName + " (" + category + ") - " + calories + " calories, Time: " + time);
            }
        } catch (SQLException e) {
            System.out.println("Error viewing meals.");
            e.printStackTrace();
        }
    }

    // Set calorie goal for the user
    private static void setCalorieGoal(Scanner scanner, Connection connection, int userId) {
        System.out.print("Enter your daily calorie goal: ");
        int goal = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        try {
            String query = "INSERT INTO calorie_goals (user_id, goal) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setInt(2, goal);
            statement.executeUpdate();
            System.out.println("Calorie goal set successfully!");
        } catch (SQLException e) {
            System.out.println("Error setting calorie goal.");
            e.printStackTrace();
        }
    }

    // View calorie goal for the user
    private static void viewCalorieGoal(Connection connection, int userId) {
        try {
            String query = "SELECT * FROM calorie_goals WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int goal = resultSet.getInt("goal");
                System.out.println("Your daily calorie goal: " + goal);
            } else {
                System.out.println("No calorie goal set.");
            }
        } catch (SQLException e) {
            System.out.println("Error viewing calorie goal.");
            e.printStackTrace();
        }
    }
}
