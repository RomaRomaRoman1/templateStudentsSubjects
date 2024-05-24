package org.example.dao;

import org.example.models.Student;
import org.example.models.StudentTask;

import java.util.List;
import java.util.Map;

public interface StudentDao {

    Student createStudent(Student student);//add new student
    Student updateStudent(Student student);//and update in List students
    Student getStudentById(String id);
    List<Student> getAllStudents();//here need to add, delete and update
    void deleteStudent(Student student);//and delete from List students
    List <Student> deleteAllStudent();


    String createTask(String task);//add new task as String and add in List Tasks
    String updateTask(String oldTask, String newTask);//and update task in List getAllTasks
    String deleteTask(String task);//and delete Task in getAllTasks
    List<String> getAllTasks();//here need to add task from tableSQL
    List <String> deleteAllTasks();

    void deleteAllData();//clean all date

    Map<Student, List<StudentTask>> getAllData();//enter data in List from StudentTask
    Map<Student, List<StudentTask>> changePointAndDoneForStudent(StudentTask studentTask);
    Map<Student, List<StudentTask>> addAllTaskForStudent(Student student);
    Map<Student, List<StudentTask>> getAllTaskForStudent(Student student);


}
