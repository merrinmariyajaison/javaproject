import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ScoreboardGUI extends JFrame {
    private Scoreboard scoreboard;

    private JTextField playerNameField;
    private JTextField pointsField;
    private JTextField deleteField; // Declare deleteField here

    private JTextArea scoreboardTextArea;

    public ScoreboardGUI() {
        scoreboard = new Scoreboard();

        setTitle("Gaming Scoreboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(4, 2));

        JLabel playerNameLabel = new JLabel("Player Name:");
        playerNameField = new JTextField();
        JLabel pointsLabel = new JLabel("Points:");
        pointsField = new JTextField();
        JButton addButton = new JButton("Add Player/Points");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String playerName = playerNameField.getText();
                int points = Integer.parseInt(pointsField.getText());
                scoreboard.addPlayer(playerName);
                scoreboard.updateScore(playerName, points);
                updateScoreboardTextArea();
            }
        });

        JLabel deleteLabel = new JLabel("Delete Player:");
        deleteField = new JTextField(); // Initialize deleteField here
        JButton deleteButton = new JButton("Delete Player");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String playerName = deleteField.getText(); // Declare deleteField as final here
                scoreboard.deletePlayer(playerName);
                updateScoreboardTextArea();
            }
        });

        inputPanel.add(playerNameLabel);
        inputPanel.add(playerNameField);
        inputPanel.add(pointsLabel);
        inputPanel.add(pointsField);
        inputPanel.add(deleteLabel);
        inputPanel.add(deleteField);
        inputPanel.add(addButton);
        inputPanel.add(deleteButton);

        add(inputPanel, BorderLayout.NORTH);

        scoreboardTextArea = new JTextArea();
        scoreboardTextArea.setEditable(false);
        add(new JScrollPane(scoreboardTextArea), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh Scoreboard");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateScoreboardTextArea();
            }
        });
        add(refreshButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void updateScoreboardTextArea() {
        StringBuilder sb = new StringBuilder();
        sb.append("Scoreboard:\n");
        sb.append(scoreboard.getScoreboardAsString());
        scoreboardTextArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ScoreboardGUI();
            }
        });
    }
}

class Scoreboard {
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

    public void deletePlayer(String playerName) {
        if (!isPlayerExists(playerName)) {
            System.out.println("Player does not exist. Please enter a valid player name.");
            return;
        }

        try {
            Statement statement = connection.createStatement();
            String sql = "DELETE FROM players WHERE name = '" + playerName + "'";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getScoreboardAsString() {
        StringBuilder sb = new StringBuilder();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM players ORDER BY score DESC");

            int rank = 1;
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int score = resultSet.getInt("score");
                sb.append(rank + ". " + name + ": " + score + "\n");
                rank++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
