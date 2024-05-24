import java.sql.*;
import java.util.*;

public class Scoreboard {
    private Connection connection;

    public Scoreboard() {
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/java?characterEncoding=utf8", "root", "");
        } catch (ClassNotFoundException e) {
             e.printStackTrace();
        } catch (SQLException e) {
             e.printStackTrace();
     }
    }

    public boolean isPlayerExists(String playerName) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM players WHERE name = '" + playerName + "'");
            resultSet.next();
            int count = resultSet.getInt(1);
            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addPlayer(String name) {
        if (isPlayerExists(name)) {
            System.out.println("Player with the same name already exists. Skipping adding player.");
            return;
        }

        try {
            Statement statement = connection.createStatement();
            String sql = "INSERT INTO players (name, score) VALUES ('" + name + "', 0)";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePlayer(String playerName) {
        if (!isPlayerExists(playerName)) {
            System.out.println("Player does not exist. Please enter a valid player name.");
            return;
        }

        try {
            Statement statement = connection.createStatement();
            String sql = "DELETE FROM players WHERE name = '" + playerName + "'";
            statement.executeUpdate(sql);
            System.out.println("Player '" + playerName + "' deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateScore(String playerName, int points) {
        if (!isPlayerExists(playerName)) {
            System.out.println("Player does not exist. Please enter a valid player name.");
            return;
        }

        try {
            Statement statement = connection.createStatement();
            String sql = "UPDATE players SET score =" + points + " WHERE name = '" + playerName + "'";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void decreaseScore(String playerName, int points) {
        if (!isPlayerExists(playerName)) {
            System.out.println("Player does not exist. Please enter a valid player name.");
            return;
        }

        try {
            Statement statement = connection.createStatement();
            String sql = "UPDATE players SET score = score - " + points + " WHERE name = '" + playerName + "'";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayScoreboard() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM players ORDER BY score DESC");

            System.out.println("Scoreboard:");
            int rank = 1;
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int score = resultSet.getInt("score");
                System.out.println(rank + ". " + name + ": " + score);
                rank++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Scoreboard scoreboard = new Scoreboard();

        System.out.println("Welcome to the Gaming Scoreboard System!");

        while (true) {
            System.out.println("\nMenu:");
            System.out.println("1. Add Player/points");
            System.out.println("2. Update Score ");
            System.out.println("3. Decrease Score");
            System.out.println("4. View Scoreboard");
            System.out.println("5. Delete Player");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter player name: ");
                    String playerName = scanner.nextLine();
                    scoreboard.addPlayer(playerName);
                    if (scoreboard.isPlayerExists(playerName)) {
                        System.out.print("Enter points scored: ");
                        int points = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        scoreboard.updateScore(playerName, points);
                    } else {
                        System.out.println("Player does not exist. Please add the player first.");
                    }
                    break;
                case 2:
                    System.out.print("Enter player name: ");
                    playerName = scanner.nextLine();
                    if (scoreboard.isPlayerExists(playerName)) {
                        System.out.print("Enter updated point of the player: ");
                        int points = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        scoreboard.updateScore(playerName, points);
                    } else {
                        System.out.println("Player does not exist. Please add the player first.");
                    }
                    break;
                case 3:
                    System.out.print("Enter player name: ");
                    playerName = scanner.nextLine();
                    if (scoreboard.isPlayerExists(playerName)) {
                        System.out.print("Enter points to decrease: ");
                        int points = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        scoreboard.decreaseScore(playerName, points);
                    } else {
                        System.out.println("Player does not exist. Please add the player first.");
                    }
                    break;
                case 4:
                    scoreboard.displayScoreboard();
                    break;
                case 5:
                    System.out.print("Enter player name to delete: ");
                    playerName = scanner.nextLine();
                    scoreboard.deletePlayer(playerName);
                    break;
                case 6:
                    System.out.println("Thank you for using the Gaming Scoreboard System!");
                    scanner.close();
                    try {
                        scoreboard.connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
            }
        }
    }
}
