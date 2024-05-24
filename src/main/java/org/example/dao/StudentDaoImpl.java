package org.example.dao;

import org.example.models.Student;
import org.example.models.StudentTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class StudentDaoImpl implements StudentDao {
    private final DataSource dataSource;
    @Autowired
    public StudentDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Student createStudent(Student student) {
        String sqlCreateStudent = "insert into student (name, lastName, course) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateStudent, PreparedStatement.RETURN_GENERATED_KEYS)){//
            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getLastName());
            preparedStatement.setInt(3, student.getCourse());
            preparedStatement.executeUpdate();
            try(ResultSet rs = preparedStatement.getGeneratedKeys()) {
                rs.next();
                student.setStudent_id(rs.getString(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return student;
    }

    @Override
    public Student updateStudent(Student student) {
       String sqlForUpdate = "UPDATE student SET name= ?, lastName = ?, course = ? WHERE student_id = ?";
       try(Connection connection = dataSource.getConnection();
       PreparedStatement preparedStatement = connection.prepareStatement(sqlForUpdate)) {
           preparedStatement.setString(1, student.getName());
           preparedStatement.setString(2, student.getLastName());
           preparedStatement.setInt(3, student.getCourse());
           preparedStatement.setString(4, student.getStudent_id());
           int rowsUpdateStudent = preparedStatement.executeUpdate();
           if (rowsUpdateStudent==0) {
               throw new RuntimeException(String.format("Student with id: %s wasn't to find", student.getStudent_id()));
           }
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
       return student;
    }

    @Override
    public Student getStudentById(String id) {
        String sqlDeleteStudent = "SELECT student_id, name, lastName, course from student WHERE student_id = ?";
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteStudent)) {
            preparedStatement.setString(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (!rs.next()) {
                throw new RuntimeException(String.format("Student with id: %s wasn't find", id));
            }
            return new Student(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Student> getAllStudents() {
        List<Student> studentList = new ArrayList<>();
        String sqlGetAllStudents = "SELECT student_id, name, lastName, course from student";
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlGetAllStudents)){
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Student student = new Student(rs.getString(1), rs.getString(2), rs.getString(3), rs.getInt(4));
                studentList.add(student);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return studentList;
    }

    @Override
    public void deleteStudent(Student student) {
        String sqlDeleteAllForStudentTasks = "DELETE FROM student_task WHERE student_id = ?";
    String sqlDeleteStudent = "DELETE from student WHERE student_id = ?";
    try(Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatementDeleteStudentFromStudentTasks = connection.prepareStatement(sqlDeleteAllForStudentTasks);
    PreparedStatement preparedStatement = connection.prepareStatement(sqlDeleteStudent)) {
        preparedStatement.setString(1, student.getStudent_id());
        preparedStatementDeleteStudentFromStudentTasks.setString(1, student.getStudent_id());
        preparedStatementDeleteStudentFromStudentTasks.executeUpdate();
        int rowsDeleteStudents = preparedStatement.executeUpdate();
        if (rowsDeleteStudents==0) {
            throw new RuntimeException(String.format("Student: %s wasn't find", student));
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
    }

    @Override
    public List<Student> deleteAllStudent() {
        List <Student> studentWhichDelete = getAllStudents();
        String sqlDeleteAllForStudentTasks = "DELETE FROM student_task";
        String sqlForDeleteAllData="DELETE FROM student";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlForDeleteAllData);
             PreparedStatement preparedStatementDeleteFromStudentTask = connection.prepareStatement(sqlDeleteAllForStudentTasks)) {
            preparedStatement.executeUpdate();
            preparedStatementDeleteFromStudentTask.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return studentWhichDelete;
    }

    @Override
    public String createTask(String task) {
        String sqlCreateTask = "insert into task (name) VALUES (?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlCreateTask)){//
            preparedStatement.setString(1, task);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return task;
    }

    @Override
    public String updateTask(String oldTask, String newTask) {
        String sqlUpdateTask = "UPDATE task SET name=? WHERE name=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlUpdateTask)){//
            preparedStatement.setString(2, oldTask);
            preparedStatement.setString(1, newTask);
            int rowsUpdate = preparedStatement.executeUpdate();
            if (rowsUpdate==0) {
                throw new RuntimeException(String.format("Task with name: %s not found", oldTask));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return String.format("Task with name: %s was change on: %s", oldTask, newTask);
    }

    @Override
    public String deleteTask(String task) {
        String sqlForDeleteTaskFromStudentTask = "DELETE from student_task WHERE task_name = ?";
        String sqlForDeleteTask = "DELETE from task WHERE name = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatementDelete = connection.prepareStatement(sqlForDeleteTask);
             PreparedStatement preparedStatementDeleteFromStudentTask = connection.prepareStatement(sqlForDeleteTaskFromStudentTask)) {
            preparedStatementDeleteFromStudentTask.setString(1, task);
            preparedStatementDelete.setString(1, task);
            preparedStatementDeleteFromStudentTask.executeUpdate();
            int rowsAffected = preparedStatementDelete.executeUpdate();
            if (rowsAffected == 0) { // Проверяем, затронута ли хотя бы одна строка
                throw new RuntimeException(String.format("Task with name: %s not found", task));
            }
            return String.format("Task with name: %s was deleted", task);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getAllTasks() {
        List<String> allTasks = new ArrayList<>();
        String sqlGetAllTasks = "SELECT name from task";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlGetAllTasks)){//
            try(ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    String task = rs.getString(1);
                    allTasks.add(task);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return allTasks;
    }

    @Override
    public List<String> deleteAllTasks() {
        List <String> taskWhichDelete = new ArrayList<>();
        taskWhichDelete = getAllTasks();
        String sqlForDeleteAllData="DELETE FROM task";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlForDeleteAllData)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return taskWhichDelete;
    }
    @Override
    public void deleteAllData() {
        String sqlForDeleteAllData="DELETE FROM student_task";
        try (Connection connection=dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlForDeleteAllData)) {
            preparedStatement.executeUpdate();
            deleteAllStudent();
            deleteAllTasks();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Student, List<StudentTask>> changePointAndDoneForStudent(StudentTask studentTask) {
        String sqlForUpdateStudentTask = "UPDATE student_task SET done = ?, point = ? WHERE studentTask_id = ?";
        String sqlGetUpdatedTask = "SELECT student_id, student_name, student_lastName, student_course, task_name, done, point FROM student_task WHERE studentTask_id = ?";
        Student student = null;
        List<StudentTask> updatedTasks = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(sqlForUpdateStudentTask);
             PreparedStatement selectStatement = connection.prepareStatement(sqlGetUpdatedTask)) {

            // Update the task
            updateStatement.setBoolean(1, studentTask.isDone());
            updateStatement.setInt(2, studentTask.getPoint());
            updateStatement.setString(3, studentTask.getStudentTask_id());
            int rowsUpdate = updateStatement.executeUpdate();
            if (rowsUpdate == 0) {
                throw new RuntimeException(String.format("Task with id: %s wasn't found", studentTask.getStudentTask_id()));
            }

            // Retrieve the updated task
            selectStatement.setString(1, studentTask.getStudentTask_id());
            try (ResultSet rs = selectStatement.executeQuery()) {
                if (rs.next()) {
                    student = new Student(rs.getString("student_id"), rs.getString("student_name"), rs.getString("student_lastName"), rs.getInt("student_course"));
                    StudentTask updatedTask = new StudentTask(
                            studentTask.getStudentTask_id(),
                            rs.getString("task_name"),
                            rs.getBoolean("done"),
                            rs.getInt("point")
                    );
                    updatedTasks.add(updatedTask);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (student == null) {
            throw new RuntimeException("Student not found for the given task.");
        }

        return Map.of(student, updatedTasks);
    }

    @Override
    public Map<Student, List<StudentTask>> getAllData() {
      List<Student> allStudent = getAllStudents();
      Map<Student, List<StudentTask>> allTasksForEveryStudent = new HashMap<>();
      for(Student student: allStudent) {
          allTasksForEveryStudent.putAll(getAllTaskForStudent(student));
      }
        return allTasksForEveryStudent;
    }

    @Override
    public Map<Student, List<StudentTask>> addAllTaskForStudent(Student student) {
        List<String> allAddedTasks = getAllTasks();
        String sqlAddStudentTasks = "INSERT INTO student_task (student_id, student_name, student_lastName, " +
                "student_course, task_name, done, point) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sqlAddStudentTasks)) {
                for(String taskName: allAddedTasks) {
                    preparedStatement.setString(1, student.getStudent_id());
                    preparedStatement.setString(2, student.getName());
                    preparedStatement.setString(3, student.getLastName());
                    preparedStatement.setInt(4, student.getCourse());
                    preparedStatement.setString(5, taskName);
                    preparedStatement.setBoolean(6, false);
                    preparedStatement.setInt(7, 0);
                    preparedStatement.executeUpdate(); // Выполнить вставку
                }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return getAllTaskForStudent(student);
    }

    @Override
    public Map<Student, List<StudentTask>> getAllTaskForStudent(Student student) {
        String sqlGetStudentTasks = "SELECT studentTask_id, task_name, done, point FROM student_task WHERE student_id = ?";
        List<StudentTask> studentTasks = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlGetStudentTasks)) {
            preparedStatement.setString(1, student.getStudent_id());
            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    StudentTask studentTask = new StudentTask(
                            rs.getString("studentTask_id"),
                            rs.getString("task_name"),
                            rs.getBoolean("done"),
                            rs.getInt("point")
                    );
                    studentTasks.add(studentTask);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Map.of(student, studentTasks);
    }
}
