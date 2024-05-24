package org.example.models;

public class Student {
    private String student_id;
    private String name;
    private String lastName;
    private int course;

    public Student(String name, String lastName, int course) {
        this.name = name;
        this.lastName = lastName;
        this.course = course;
    }

    public Student(String id, String name, String lastName, int course) {
        this.student_id = id;
        this.name = name;
        this.lastName = lastName;
        this.course = course;
    }

    public Student() {
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getCourse() {
        return course;
    }

    public void setCourse(int course) {
        this.course = course;
    }
}
