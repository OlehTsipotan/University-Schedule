package com.university.schedule.repository;

import com.university.schedule.model.Group;
import com.university.schedule.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmailAndPassword(String email, String password);

    @Query("SELECT s FROM Student s JOIN s.group g WHERE g.name = :groupName")
    List<Student> findByGroupsName(@Param("groupName") String groupName);

    Page<Student> findByGroup(Group group, Pageable pageable);

}
