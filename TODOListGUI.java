import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TODOListGUI extends JFrame {
    private Connection con;

    private JTextField taskField;
    private JTextArea taskArea;

    public TODOListGUI() {
        super("TODO List");

        // Initialize components
        taskField = new JTextField(20);
        taskArea = new JTextArea(10, 20);
        taskArea.setEditable(false);

        JButton addButton = new JButton("Add Task");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addTask(taskField.getText());
                displayTasks();
                taskField.setText("");
            }
        });

        JButton removeButton = new JButton("Remove Task");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeTask();
                displayTasks();
            }
        });

        // Layout
        JPanel inputPanel = new JPanel();
        inputPanel.add(taskField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);

        JScrollPane scrollPane = new JScrollPane(taskArea);

        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Connect to database and create table
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/todo_db?characterEncoding=utf8", "root", "");
            createTable();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error connecting to database.");
        }

        // Set JFrame properties
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void createTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS tasks (id INT AUTO_INCREMENT PRIMARY KEY, task TEXT)";
        Statement st = con.createStatement();
        st.execute(query);
    }

    private void addTask(String task) {
        if (task.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Task cannot be empty.");
            return;
        }
        String query = "INSERT INTO tasks (task) VALUES (?)";
        try {
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, task);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error adding task.");
        }
    }

    private void displayTasks() {
        taskArea.setText("");
        String query = "SELECT * FROM tasks";
        try {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(query);
            while (rs.next()) {
                taskArea.append(rs.getInt("id") + ". " + rs.getString("task") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error displaying tasks.");
        }
    }

    private void removeTask() {
        String input = JOptionPane.showInputDialog("Enter the task ID to remove:");
        if (input == null) // User clicked cancel
            return;
        try {
            int taskId = Integer.parseInt(input);
            String query = "DELETE FROM tasks WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, taskId);
            ps.executeUpdate();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Invalid task ID.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error removing task.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TODOListGUI();
            }
        });
    }
}
