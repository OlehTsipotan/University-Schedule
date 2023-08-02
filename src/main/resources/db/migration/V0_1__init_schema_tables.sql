CREATE TABLE users (
    user_id INT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL
);

CREATE TABLE groups(
    group_id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE courses(
    course_id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE groups_courses(
    group_id INT NOT NULL REFERENCES groups(group_id) ON DELETE CASCADE,
    course_id INT NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE(group_id, course_id)
);

CREATE TABLE students(
    student_id INT PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE,
    group_id INT REFERENCES groups(group_id) ON DELETE SET NULL
);

CREATE TABLE teachers(
     teacher_id INT PRIMARY KEY REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE teachers_courses(
    teacher_id INT NOT NULL REFERENCES teachers(teacher_id) ON DELETE CASCADE,
    course_id INT NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
    UNIQUE(teacher_id, course_id)
);

CREATE TABLE class_times(
    class_time_id INT PRIMARY KEY,
    order_number INT NOT NULL UNIQUE,
    start_time TIME NOT NULL,
    duration_minutes INT NOT NUll
);

CREATE TABLE buildings(
    building_id INT PRIMARY KEY,
    name VARCHAR(255) NOT NUll UNIQUE,
    address VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE classrooms(
    classroom_id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    building_id INT NOT NULL REFERENCES buildings(building_id) ON DELETE CASCADE,
    UNIQUE(name, building_id)
);

CREATE TABLE class_types(
    class_type_id INT PRIMARY KEY,
    name VARCHAR(255) NOT NUll UNIQUE
);

CREATE TABLE scheduled_classes(
    scheduled_class_id INT PRIMARY KEY,
    course_id INT NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
    teacher_id INT NOT NULL REFERENCES teachers(teacher_id) ON DELETE CASCADE,
    classroom_id INT REFERENCES classrooms(classroom_id) ON DELETE SET NULL,
    class_time_id INT NOT NULL REFERENCES class_times(class_time_id) ON DELETE CASCADE,
    class_date DATE NOT NULL,
    type_id INT REFERENCES class_types(class_type_id) ON DELETE SET NULL,
    UNIQUE(class_date, class_time_id, teacher_id)
);

CREATE TABLE scheduled_classes_groups(
    scheduled_class_id INT REFERENCES scheduled_classes(scheduled_class_id) ON DELETE CASCADE,
    group_id INT REFERENCES groups(group_id) ON DELETE CASCADE,
    UNIQUE(scheduled_class_id, group_id)
);