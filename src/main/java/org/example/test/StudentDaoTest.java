package org.example.test;
import org.example.dao.StudentDao;
import org.example.models.Student;
import org.example.models.StudentTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"jdbcUrl=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1"})//указание св-ва ссылки на базу данных + чтобы предыдущая база данных не закрылась
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentDaoTest{
    private StudentDao studentDao;
    @Autowired
    public StudentDaoTest(StudentDao studentDao) {
        this.studentDao = studentDao;
    }
    @BeforeEach
    public void beforeEach() {
        studentDao.deleteAllData();
    }
    @Test
    void createStudentTest() {
        Student student = studentDao.createStudent(new Student("Harry", "Potter", 3));
        assertThat(student.getStudent_id()).isNotBlank();
        assertThat(studentDao.getAllStudents()).extracting("student_id").containsExactly(student.getStudent_id());
    }
    @Test
    void getAllStudent() {
        assertThat(studentDao.getAllStudents()).isEmpty();
        studentDao.createStudent(new Student("Harry", "Potter", 4));
        assertThat(studentDao.getAllStudents()).isNotEmpty();
    }
    @Test
    void getAllTasks() {
        studentDao.createTask("Основы архитектурного мышления");
        assertThat(studentDao.getAllTasks()).isNotEmpty();
        studentDao.deleteAllTasks();
        assertThat(studentDao.getAllTasks()).isEmpty();
    }
    @Test
    void deleteTask() {
        studentDao.createTask("Основы архитектурного мышления");
        studentDao.deleteTask("Основы архитектурного мышления");
        assertThat(studentDao.getAllTasks()).isEmpty();
    }
    @Test
    void deleteTaskIfNotExist() {
        studentDao.createTask("Основы архитектурного мышления");
        assertThatThrownBy(() -> studentDao.deleteTask("Основы архитектурного мышления33")).isInstanceOf(RuntimeException.class).
                hasMessageContaining("Task with name: Основы архитектурного мышления33 not found");
    }
    @Test
    void deleteAllStudent() {
        studentDao.createStudent(new Student("Harry", "Potter", 4));
        assertThat(studentDao.getAllStudents()).isNotEmpty();
        studentDao.deleteAllData();
        assertThat(studentDao.getAllStudents()).isEmpty();
    }
    @Test
    void updateTask() {
        studentDao.createTask("Основы архитектурного мышления");
        assertThat(studentDao.getAllTasks()).containsExactly("Основы архитектурного мышления");
        studentDao.updateTask("Основы архитектурного мышления", "Happy words=)");
        assertThat(studentDao.getAllTasks()).containsExactly("Happy words=)");
    }
    @Test
    void deleteStudent() {
        Student student = new Student("Harry", "Potter", 4);
        studentDao.createStudent(student);
        assertThat(studentDao.getStudentById(student.getStudent_id()).getStudent_id()).contains(student.getStudent_id());
        studentDao.deleteStudent(student);
        assertThatThrownBy(() -> studentDao.getStudentById(student.getStudent_id())).isInstanceOf(RuntimeException.class);
    }
    @Test
    void findStudent() {
        Student student = new Student("Harry", "Potter", 4);
        studentDao.createStudent(student);
        assertThat(studentDao.getStudentById(student.getStudent_id()).getStudent_id()).isEqualTo(student.getStudent_id());
    }
    @Test
    void updateStudent() {
        Student student = new Student("Harry", "Potter", 4);
        studentDao.createStudent(student);
        assertThat(studentDao.getStudentById(student.getStudent_id()).getStudent_id()).contains(student.getStudent_id());
        student.setName("Wolandemort!");
        studentDao.updateStudent(student);
        assertThat(studentDao.getStudentById(student.getStudent_id()).getName()).isEqualTo("Wolandemort!");
    }
    @Test
    void addAllTasksForStudent() {
        Student student = new Student("Harry", "Potter", 4);
        studentDao.createStudent(student);
        studentDao.createTask("Основы архитектурного мышления");
        studentDao.createTask("Основы программирования");
        studentDao.addAllTaskForStudent(student);

        Map<Student, List<StudentTask>> tasksForStudent = studentDao.getAllTaskForStudent(student);
        assertThat(tasksForStudent).containsKey(student);
        assertThat(tasksForStudent.get(student)).hasSize(2); // Убедитесь, что у студента две задачи
        assertThat(tasksForStudent.get(student)).extracting("taskName").contains("Основы архитектурного мышления", "Основы программирования");
    }
    @Test
    void changePointAndDoneForStudent() {
        Student student = new Student("Harry", "Potter", 4);
        student = studentDao.createStudent(student);
        studentDao.createTask("Основы программирования");
        studentDao.addAllTaskForStudent(student);

        StudentTask studentTask = studentDao.getAllTaskForStudent(student).get(student).get(0);
        studentTask.setDone(true);
        studentTask.setPoint(10);

        Map<Student, List<StudentTask>> result = studentDao.changePointAndDoneForStudent(studentTask);
        final String studentId = student.getStudent_id();
        assertTrue(result.keySet().stream().anyMatch(s -> s.getStudent_id().equals(studentId)));// Проверяем, что существует студент с заданным student_id

        // Находим ключ-студента по student_id
        Student resultStudent = result.keySet().stream()
                .filter(s -> s.getStudent_id().equals(studentId))
                .findFirst()
                .orElse(null);
        assertNotNull(resultStudent);

        assertNotNull(resultStudent);  // Проверяем, что студент найден

        // Получаем задачи для найденного студента
        List<StudentTask> updatedTasks = result.get(resultStudent);

        assertNotNull(updatedTasks);
        assertEquals(1, updatedTasks.size());

        StudentTask updatedTask = updatedTasks.get(0);
        assertEquals(studentTask.getStudentTask_id(), updatedTask.getStudentTask_id());
        assertTrue(updatedTask.isDone());
        assertEquals(10, updatedTask.getPoint());
    }
    @Test
    void getAllData(){
        Student student1 = new Student("Harry", "Potter", 4);
        Student student2 = new Student("Hermione", "Granger", 4);
        studentDao.createStudent(student1);
        studentDao.createStudent(student2);
        studentDao.createTask("Основы программирования");
        studentDao.createTask("Магическая история");
        studentDao.addAllTaskForStudent(student1);
        studentDao.addAllTaskForStudent(student2);

        Map<Student, List<StudentTask>> allData = studentDao.getAllData();

        assertNotNull(allData);
        assertEquals(2, allData.size());

        for (Map.Entry<Student, List<StudentTask>> entry : allData.entrySet()) {
            Student student = entry.getKey();
            List<StudentTask> tasks = entry.getValue();

            assertNotNull(student);
            assertNotNull(tasks);
            assertEquals(2, tasks.size());

            for (StudentTask task : tasks) {
                assertNotNull(task.getStudentTask_id());
                assertNotNull(task.getTaskName());
                assertFalse(task.isDone());
                assertEquals(0, task.getPoint());
            }
        }
    }
}
