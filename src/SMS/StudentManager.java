package SMS;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentManager {
    private static final String DATA_FILE = "students.dat";
    private ArrayList<Student> students = new ArrayList<>();

    public void addStudent(Student s) {
        students.add(s);
        saveToFile();
    }

    public boolean removeStudent(int id) {
        boolean removed = students.removeIf(s -> s.getId() == id);
        if (removed) saveToFile();
        return removed;
    }

    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(students);
    }

    public Student searchById(int id) {
        for (Student s : students)
            if (s.getId() == id) return s;
        return null;
    }

    public List<Student> searchByName(String name) {
        List<Student> result = new ArrayList<>();
        String query = name.toLowerCase();
        for (Student s : students)
            if (s.getName().toLowerCase().contains(query))
                result.add(s);
        return result;
    }

    public int getTotalStudents() {
        return students.size();
    }

    public double getClassAvgGPA() {
        if (students.isEmpty()) return 0.0;
        double sum = 0;
        for (Student s : students) sum += s.getGPA();
        return Math.round((sum / students.size()) * 100.0) / 100.0;
    }

    public void saveToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(students);
        } catch (IOException e) {
            System.err.println("Failed to save student data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            Object data = in.readObject();
            if (data instanceof ArrayList<?>) {
                students = (ArrayList<Student>) data;
                int maxId = 0;
                for (Student s : students) {
                    if (s.getId() > maxId) maxId = s.getId();
                }
                Student.setNextId(maxId + 1);
            }
        } catch (IOException e) {
            // Missing/corrupt file should not crash app; start with empty list.
            students = new ArrayList<>();
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load student data class: " + e.getMessage());
            students = new ArrayList<>();
        }
    }

    public void exportToCsv(String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(filePath))) {
            writer.println("ID,Name,Course,Age,GPA");
            for (Student s : students) {
                writer.printf(
                    "%d,%s,%s,%d,%.2f%n",
                    s.getId(),
                    escapeCsv(s.getName()),
                    escapeCsv(s.getCourse()),
                    s.getAge(),
                    s.getGPA()
                );
            }
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n") || escaped.contains("\r")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}