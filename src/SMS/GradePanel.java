package SMS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Locale;

public class GradePanel extends JDialog {
    public GradePanel(JFrame parent, Student student) {
        super(parent, "Grades - " + student.getName(), true);
        setSize(450, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 250));

        // Title
        JLabel title = new JLabel("📊 Grades for " + student.getName(), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        add(title, BorderLayout.NORTH);

        // Table
        String[] cols = {"Subject", "Grade"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (var entry : student.getGrades().entrySet())
            model.addRow(new Object[]{entry.getKey(), entry.getValue()});
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Add grade panel
        JPanel addPanel = new JPanel(new FlowLayout());
        addPanel.setBackground(new Color(245, 245, 250));
        JTextField subjectField = new JTextField(10);
        JTextField gradeField = new JTextField(5);
        JButton addBtn = new JButton("Add Grade");
        JLabel gpaLabel = new JLabel(formatGpaLabel(student.getGPA()), SwingConstants.CENTER);
        gpaLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gpaLabel.setForeground(new Color(70, 130, 180));
        addBtn.setBackground(new Color(70, 130, 180));
        addBtn.setForeground(Color.WHITE);

        addBtn.addActionListener(e -> {
            String subject = subjectField.getText().trim();
            if (subject.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Subject cannot be empty.");
                return;
            }

            try {
                double grade = Double.parseDouble(gradeField.getText().trim());
                if (grade < 0 || grade > 100) {
                    JOptionPane.showMessageDialog(this, "Grade must be between 0 and 100!");
                    return;
                }
                student.addGrade(subject, grade);
                model.addRow(new Object[]{subject, grade});
                gpaLabel.setText(formatGpaLabel(student.getGPA()));
                gpaLabel.repaint();
                subjectField.setText("");
                gradeField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Grade must be a valid number (0-100).");
            }
        });

        addPanel.add(new JLabel("Subject:"));
        addPanel.add(subjectField);
        addPanel.add(new JLabel("Grade:"));
        addPanel.add(gradeField);
        addPanel.add(addBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 245, 250));
        bottomPanel.add(addPanel, BorderLayout.CENTER);
        bottomPanel.add(gpaLabel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private static String formatGpaLabel(double gpa) {
        return String.format(Locale.US, "GPA: %.2f", gpa);
    }
}