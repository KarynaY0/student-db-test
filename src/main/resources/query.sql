SELECT * FROM courses;

SELECT * FROM students;

SELECT * FROM students WHERE age > 20;

SELECT students.name, students.email, courses.name AS course_name
FROM students
JOIN courses ON students.course_id = courses.course_id;

SELECT courses.name AS course_name, COUNT(students.student_id) AS student_count
FROM courses
LEFT JOIN students ON students.course_id = courses.course_id
GROUP BY courses.course_id;

UPDATE students SET age = 20 WHERE name = 'Mantas Simkus';

UPDATE students SET course_id = 3 WHERE name = 'Jonas Kazlauskas';

DELETE FROM students WHERE name = 'Egle Balciunaite';

SELECT students.name, students.email, students.age, courses.name AS course_name
FROM students
JOIN courses ON students.course_id = courses.course_id;
