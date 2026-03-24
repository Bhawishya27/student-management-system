package SMS;

import java.io.Serializable;
import java.util.HashMap;

public class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int idCounter = 1;
    private int id;
    private String name;
    private String course;
    private int age;
    private HashMap<String, Double> grades;

    public Student(String name, String course, int age) {
        this.id = idCounter++;
        this.name = name;
        this.course = course;
        this.age = age;
        this.grades = new HashMap<>();
    }

    // Getters
    public int getId()       { return id; }
    public String getName()  { return name; }
    public String getCourse(){ return course; }
    public int getAge()      { return age; }
    public HashMap<String, Double> getGrades() { return grades; }

    static void setNextId(int nextId) {
        idCounter = Math.max(1, nextId);
    }

    // Setters
    public void setName(String name)    { this.name = name; }
    public void setCourse(String course){ this.course = course; }
    public void setAge(int age)         { this.age = age; }

    // Grade methods
    public void addGrade(String subject, double grade) {
        grades.put(subject, grade);
    }

    public double getGPA() {
        if (grades.isEmpty()) return 0.0;
        double sum = 0;
        for (double g : grades.values()) sum += g;
        return Math.round((sum / grades.size()) * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "ID: " + id + " | " + name + " | " + course + " | Age: " + age + " | GPA: " + getGPA();
    }
}