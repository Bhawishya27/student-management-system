package SMS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class MainGUI extends JFrame {
    private static final Color BG_APP = new Color(245, 247, 252);
    private static final Color BG_SURFACE = Color.WHITE;
    private static final Color PRIMARY = new Color(52, 120, 190);
    private static final Color PRIMARY_DARK = new Color(39, 96, 153);
    private static final Color TEXT_ON_PRIMARY = Color.WHITE;
    private static final Color TEXT_MUTED = new Color(66, 74, 87);
    private static final Color BTN_NEUTRAL = new Color(108, 117, 125);
    private static final Color BTN_ACCENT = new Color(88, 101, 242);
    private static final Color BTN_SUCCESS = new Color(46, 166, 112);
    private static final Color BTN_WARNING = new Color(230, 150, 40);
    private static final Color BTN_DANGER = new Color(211, 70, 70);

    private static final int COL_ID = 0;

    private final StudentManager manager = new StudentManager();
    private DefaultTableModel tableModel;
    private JTable studentTable;
    private JLabel statsLabel;

    public MainGUI() {
        setTitle("🎓 Student Management System");
        setSize(850, 580);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BG_APP);

        add(buildHeader(),     BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildFormPanel(),  BorderLayout.WEST);
        add(buildStatsPanel(), BorderLayout.SOUTH);

        manager.loadFromFile();
        refreshUi();

        setVisible(true);
    }

    // ── HEADER ──────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel panel = new JPanel();
        panel.setBackground(PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        JLabel label = new JLabel("🎓 Student Management System");
        label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        label.setForeground(TEXT_ON_PRIMARY);
        panel.add(label);
        return panel;
    }

    // ── TABLE ────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 226, 236), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(BG_SURFACE);

        // Search bar
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchPanel.setBackground(BG_SURFACE);
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(220, 32));
        JButton searchBtn = new JButton("🔍 Search");
        JButton showAllBtn = new JButton("Show All");
        JButton sortNameBtn = new JButton("Sort Name ↑");
        JButton sortGpaBtn = new JButton("Sort GPA ↓");
        JButton exportCsvBtn = new JButton("Export CSV");
        styleButton(searchBtn, PRIMARY);
        styleButton(showAllBtn, BTN_NEUTRAL);
        styleButton(sortNameBtn, BTN_ACCENT);
        styleButton(sortGpaBtn, BTN_ACCENT);
        styleButton(exportCsvBtn, BTN_SUCCESS);

        searchBtn.addActionListener(e -> runSearch(searchField.getText()));

        showAllBtn.addActionListener(e -> refreshTable());
        sortNameBtn.addActionListener(e -> displaySortedByName());
        sortGpaBtn.addActionListener(e -> displaySortedByGpaDescending());
        exportCsvBtn.addActionListener(e -> exportStudentsToCsv());

        JLabel searchLabel = new JLabel("Search (name/ID):");
        searchLabel.setForeground(TEXT_MUTED);
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(showAllBtn);
        searchPanel.add(sortNameBtn);
        searchPanel.add(sortGpaBtn);
        searchPanel.add(exportCsvBtn);
        panel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Name", "Course", "Age", "GPA"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.setRowHeight(26);
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentTable.getTableHeader().setBackground(PRIMARY);
        studentTable.getTableHeader().setForeground(TEXT_ON_PRIMARY);
        studentTable.setSelectionBackground(new Color(173, 216, 230));

        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Action buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        btnPanel.setBackground(BG_SURFACE);
        JButton removeBtn = new JButton("🗑 Remove Selected");
        JButton gradeBtn  = new JButton("📊 Manage Grades");
        JButton editBtn   = new JButton("Edit Student");
        styleButton(removeBtn, BTN_DANGER);
        styleButton(gradeBtn, BTN_SUCCESS);
        styleButton(editBtn, BTN_WARNING);

        removeBtn.addActionListener(e -> {
            Student selected = getSelectedStudent();
            if (selected == null) return;
            manager.removeStudent(selected.getId());
            refreshUi();
        });

        gradeBtn.addActionListener(e -> {
            Student selected = getSelectedStudent();
            if (selected == null) return;
            new GradePanel(this, selected);
            manager.saveToFile();
            refreshUi();
        });

        editBtn.addActionListener(e -> {
            Student selected = getSelectedStudent();
            if (selected == null) return;
            openEditStudentDialog(selected);
        });

        btnPanel.add(gradeBtn);
        btnPanel.add(editBtn);
        btnPanel.add(removeBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ── FORM ─────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY, 2),
            "➕ Add Student", 0, 0,
            new Font("Segoe UI", Font.BOLD, 13), PRIMARY));
        panel.setBackground(BG_SURFACE);
        panel.setPreferredSize(new Dimension(220, 0));

        JTextField nameField   = new JTextField();
        JTextField courseField = new JTextField();
        JTextField ageField    = new JTextField();
        Dimension fieldSize = new Dimension(180, 30);
        nameField.setMaximumSize(fieldSize);
        courseField.setMaximumSize(fieldSize);
        ageField.setMaximumSize(fieldSize);
        nameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        courseField.setAlignmentX(Component.LEFT_ALIGNMENT);
        ageField.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(Box.createVerticalStrut(10));
        panel.add(formLabel("Name:"));
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(8));
        panel.add(formLabel("Course:"));
        panel.add(courseField);
        panel.add(Box.createVerticalStrut(8));
        panel.add(formLabel("Age:"));
        panel.add(ageField);
        panel.add(Box.createVerticalStrut(15));

        JButton addBtn = new JButton("Add Student");
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleButton(addBtn, PRIMARY);

        addBtn.addActionListener(e -> {
            try {
                String name   = nameField.getText().trim();
                String course = courseField.getText().trim();
                int age       = Integer.parseInt(ageField.getText().trim());
                String error = validateStudentInput(name, course, age);
                if (error != null) {
                    showValidationError(error);
                    return;
                }

                manager.addStudent(new Student(name, course, age));
                refreshUi();
                nameField.setText("");
                courseField.setText("");
                ageField.setText("");
            } catch (NumberFormatException ex) {
                showValidationError("Age must be a valid whole number.");
            }
        });

        panel.add(addBtn);
        return panel;
    }

    // ── STATS ─────────────────────────────────────────────────
    private JPanel buildStatsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PRIMARY_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        statsLabel = new JLabel("Total Students: 0  |  Class Avg GPA: 0.0");
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statsLabel.setForeground(TEXT_ON_PRIMARY);
        panel.add(statsLabel);
        return panel;
    }

    // ── HELPERS ───────────────────────────────────────────────
    private void runSearch(String rawQuery) {
        String query = rawQuery.trim();
        if (query.isEmpty()) {
            return;
        }
        try {
            int id = Integer.parseInt(query);
            Student match = manager.searchById(id);
            if (match != null) {
                List<Student> one = new ArrayList<>(1);
                one.add(match);
                displayStudents(one);
            } else {
                displayStudents(Collections.emptyList());
            }
        } catch (NumberFormatException ex) {
            displayStudents(manager.searchByName(query));
        }
    }

    private void displaySortedByName() {
        List<Student> sorted = new ArrayList<>(manager.getAllStudents());
        sorted.sort(Comparator.comparing(s -> s.getName().toLowerCase(Locale.ROOT)));
        displayStudents(sorted);
    }

    private void displaySortedByGpaDescending() {
        List<Student> sorted = new ArrayList<>(manager.getAllStudents());
        sorted.sort(Comparator.comparingDouble(Student::getGPA).reversed());
        displayStudents(sorted);
    }

    private void refreshTable() {
        displayStudents(manager.getAllStudents());
    }

    private void displayStudents(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) addRowToTable(s);
    }

    private void addRowToTable(Student s) {
        tableModel.addRow(new Object[]{s.getId(), s.getName(), s.getCourse(), s.getAge(), s.getGPA()});
    }

    private void openEditStudentDialog(Student student) {
        JTextField nameField = new JTextField(student.getName(), 18);
        JTextField courseField = new JTextField(student.getCourse(), 18);
        JTextField ageField = new JTextField(String.valueOf(student.getAge()), 18);

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Course:"));
        form.add(courseField);
        form.add(new JLabel("Age:"));
        form.add(ageField);

        int result = JOptionPane.showConfirmDialog(
            this,
            form,
            "Edit Student",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return;

        try {
            String name = nameField.getText().trim();
            String course = courseField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String error = validateStudentInput(name, course, age);
            if (error != null) {
                showValidationError(error);
                return;
            }

            student.setName(name);
            student.setCourse(course);
            student.setAge(age);
            manager.saveToFile();
            refreshUi();
        } catch (NumberFormatException ex) {
            showValidationError("Age must be a valid whole number.");
        }
    }

    private Student getSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a student from the table first.",
                "No Student Selected",
                JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        Object idValue = tableModel.getValueAt(row, COL_ID);
        if (!(idValue instanceof Number)) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid row data (ID).",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        int id = ((Number) idValue).intValue();
        Student student = manager.searchById(id);
        if (student == null) {
            JOptionPane.showMessageDialog(
                this,
                "Could not find the selected student.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
        return student;
    }

    private String validateStudentInput(String name, String course, int age) {
        if (name.isEmpty()) return "Name cannot be empty.";
        if (course.isEmpty()) return "Course cannot be empty.";
        if (age <= 0) return "Age must be a positive number.";
        return null;
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Input", JOptionPane.ERROR_MESSAGE);
    }

    private void exportStudentsToCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Students to CSV");
        fileChooser.setSelectedFile(new File("students.csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        try {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
            manager.exportToCsv(filePath);
            JOptionPane.showMessageDialog(this, "Student data exported successfully.", "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to export CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStats() {
        statsLabel.setText("Total Students: " + manager.getTotalStudents() +
                           "  |  Class Avg GPA: " + manager.getClassAvgGPA());
    }

    private void refreshUi() {
        refreshTable();
        updateStats();
    }

    private void styleButton(JButton btn, Color bg) {
        Color hover = bg.brighter();
        btn.setBackground(bg);
        btn.setForeground(TEXT_ON_PRIMARY);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        btn.setOpaque(true);
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
    }

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginScreen::new);
    }
}