CREATE TABLE IF NOT EXISTS student (
student_id SERIAL PRIMARY KEY,
name VARCHAR(255),
lastName VARCHAR(255),
course INT
);

CREATE TABLE IF NOT EXISTS task (
name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS student_task (
student_id VARCHAR(255),
FOREIGN KEY (student_id) REFERENCES student(student_id),
studentTask_id SERIAL PRIMARY KEY,
student_name VARCHAR(255),
student_lastName VARCHAR(255),
student_course INT,
task_name VARCHAR(255),
done BOOLEAN,
point INT
);