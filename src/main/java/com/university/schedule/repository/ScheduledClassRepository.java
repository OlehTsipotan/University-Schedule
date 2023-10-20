package com.university.schedule.repository;

import com.university.schedule.model.ClassTime;
import com.university.schedule.model.Group;
import com.university.schedule.model.ScheduledClass;
import com.university.schedule.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduledClassRepository extends JpaRepository<ScheduledClass, Long> {

	Optional<ScheduledClass> findByDateAndClassTimeAndTeacher(LocalDate date, ClassTime classTime, Teacher teacher);

	List<ScheduledClass> findByDateBetweenAndGroups(LocalDate startDate, LocalDate endDate, Group group);

	@Query("SELECT s FROM ScheduledClass s WHERE s.date BETWEEN ?1 AND ?2 " +
	       "AND (?3 IS NULL OR s.classType.id = ?3) " + "AND (?4 IS NULL OR s.teacher.id = ?4) " +
	       "AND (EXISTS (SELECT 1 FROM s.groups g WHERE g.id IN ?5)) ORDER BY s.date")
	List<ScheduledClass> findAllFiltered(LocalDate startDate, LocalDate endDate, Long classTypeId, Long teacherId,
	                                     List<Long> groupIds);

	@Query("SELECT s FROM ScheduledClass s WHERE s.date BETWEEN ?1 AND ?2 " +
	       "AND (?3 IS NULL OR s.classType.id = ?3) " + "AND (?4 IS NULL OR s.teacher.id = ?4) ORDER BY s.date")
	List<ScheduledClass> findAllFiltered(LocalDate startDate, LocalDate endDate, Long classTypeId, Long teacherId);


}
