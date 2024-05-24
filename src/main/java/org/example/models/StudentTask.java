package org.example.models;

public class StudentTask {
    private String studentTask_id;
    private String taskName;
    private boolean done;
    private int point;

    public StudentTask(String taskName, boolean done, int point) {
        this.taskName = taskName;
        this.done = done;
        this.point = point;
    }

    public StudentTask(String studentTask_id, String taskName, boolean done, int point) {
        this.studentTask_id = studentTask_id;
        this.taskName = taskName;
        this.done = done;
        this.point = point;
    }

    public String getStudentTask_id() {
        return studentTask_id;
    }

    public void setStudentTask_id(String studentTask_id) {
        this.studentTask_id = studentTask_id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
