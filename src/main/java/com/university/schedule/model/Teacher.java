package com.university.schedule.model;

import com.university.schedule.visitor.UserPageableCourseVisitor;
import com.university.schedule.visitor.UserPageableStudentVisitor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "teachers")
@PrimaryKeyJoinColumn(name = "teacher_id")
@NoArgsConstructor
public class Teacher extends User {

    @ManyToMany
    @JoinTable(name = "teachers_courses", joinColumns = @JoinColumn(name = "teacher_id"),
               inverseJoinColumns = @JoinColumn(name = "course_id"))
    private Set<Course> courses = new HashSet<>();

    public Teacher(User user) {
        super(user.getId(), user.getEmail(), user.getPassword(), user.getFirstName(), user.getLastName(),
            user.isEnable(), user.getRole());
    }

    public Teacher(String email, String password, String firstName, String lastName, Role role) {
        super(email, password, firstName, lastName, role);
    }

    public Teacher(String email, String password, String firstName, String lastName) {
        super(email, password, firstName, lastName);
    }

    @Override
    public List<Course> accept(UserPageableCourseVisitor visitor, Pageable pageable) {
        return visitor.performActionForTeacher(this, pageable);
    }

    public List<Student> accept(UserPageableStudentVisitor visitor, Pageable pageable) {
        return visitor.performActionForTeacher(pageable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Teacher teacher = (Teacher) o;
        return getId() != null && Objects.equals(getId(), teacher.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + "id = " + getId() + ", " + "email = " + getEmail() + ", " +
            "firstName = " + getFirstName() + ", " + "lastName = " + getLastName() + ", " + "role = " + getRole() +
            ", " + "isEnable = " + isEnable() + ")";
    }
}
