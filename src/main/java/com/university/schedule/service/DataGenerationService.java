package com.university.schedule.service;

import com.university.schedule.model.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@PropertySources({
    @PropertySource("classpath:sampleData.properties"), @PropertySource("classpath:application.properties")})
public class DataGenerationService {

    private final DisciplineService disciplineService;
    private final ClassTypeService classTypeService;
    private final BuildingService buildingService;
    private final ClassroomService classroomService;
    private final ClassTimeService classTimeService;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final CourseService courseService;
    private final GroupService groupService;

    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    private final UserService userService;

    private final AuthorityService authorityService;

    private final ScheduleGenerator scheduleGenerator;
    @Value("#{'${data.disciplines.names}'.split('${config.separator}')}")
    private List<String> disciplinesNames;

    @Value("#{'${data.disciplines.names.short}'.split('${config.separator}')}")
    private List<String> disciplinesShortNames;

    @Value("#{'${data.students.passwords}'.split('${config.separator}')}")
    private List<String> studentsPasswords;

    @Value("#{'${data.courses.names}'.split('${config.separator}')}")
    private List<String> disciplineCoursesNames;
    @Value("${data.courses.disciplineCourseNumber}")
    private int disciplineCoursesNumber;
    @Value("#{'${data.courses.general.names}'.split('${config.separator}')}")
    private List<String> generalCoursesNames;
    @Value("${data.email.domain}")
    private String emailDomain;
    @Value("#{'${data.students.firstNames}'.split('${config.separator}')}")
    private List<String> studentsFirstNames;
    @Value("#{'${data.students.lastNames}'.split('${config.separator}')}")
    private List<String> studentsLastNames;
    @Value("#{'${data.teachers.firstNames}'.split('${config.separator}')}")
    private List<String> teachersFirstNames;
    @Value("#{'${data.teachers.lastNames}'.split('${config.separator}')}")
    private List<String> teachersLastNames;
    @Value("#{'${data.classTypes.names}'.split('${config.separator}')}")
    private List<String> classTypeNames;
    @Value("#{'${data.buildings.names}'.split('${config.separator}')}")
    private List<String> buildingsNames;
    @Value("#{'${data.buildings.addresses}'.split('${config.separator}')}")
    private List<String> buildingsAddresses;
    @Value("#{'${data.roles.names}'.split('${config.separator}')}")
    private List<String> rolesNames;
    @Value("#{'${data.authorities}'.split('${config.separator}')}")
    private List<String> authoritiesNames;

    @Value("#{'${data.authorities.student}'.split('${config.separator}')}")
    private List<String> studentAuthorities;

    @Value("#{'${data.authorities.admin}'.split('${config.separator}')}")
    private List<String> adminAuthorities;

    @Value("#{'${data.authorities.teacher}'.split('${config.separator}')}")
    private List<String> teacherAuthorities;

    @Value("${data.generation.onStartup}")
    private boolean generationOnStartup;

    @Value("${data.user.admin.firstName}")
    private String adminFirstName;
    @Value("${data.user.admin.lastName}")
    private String adminLastName;

    @Value("${data.user.admin.password}")
    private String adminPassword;

    @Value("${data.user.admin.email}")
    private String adminEmail;

    private DataSchemaService dataSchemaService;

    @Autowired
    public void setDataSchemaService(DataSchemaService dataSchemaService) {
        this.dataSchemaService = dataSchemaService;
    }

    @PostConstruct
    private void postConstruct() {
        if (generationOnStartup) {
            // prepare database for insertion
            dataSchemaService.clean();
            // generation
            generate();
        }
    }

    public void generate() {
        // firstly persist non related entities
        persistDisciplines();
        log.info("Disciplines Persisted");

        persistAuthorities();
        log.info("Authorities Persisted");

        persistRoles();
        log.info("Roles Persisted");

        persistClassTypes();
        log.info("ClassTypes Persisted");

        persistBuildings();
        log.info("Buildings Persisted");

        persistClassrooms();
        log.info("Classrooms Persisted");

        persistClassTimes();
        log.info("ClassTimes Persisted");

        persistGroups();
        log.info("Groups Persisted");

        persistCourses();
        log.info("Groups Persisted");

        assignCoursesToGroups();
        log.info("Courses assigned to Groups");

        persistUsers();
        log.info("Users Persisted");

        assignCoursesToTeachers();
        log.info("Courses assigned to Teachers");

        generateScheduledClass();
        log.info("Schedule Generated");


    }

    private void persistUsers() {
        persistStudents();
        log.info("Students Persisted");
        persistTeachers();
        log.info("Teachers Persisted");
        persistAdmin();
        log.info("Admin Persisted");
    }

    private void persistDisciplines() {
        disciplinesNames.forEach((name) -> disciplineService.save(new Discipline(name)));
    }

    private void persistClassTypes() {
        classTypeNames.forEach((name) -> classTypeService.save(new ClassType(name)));
    }

    private void persistBuildings() {
        int size = Integer.min(buildingsNames.size(), buildingsAddresses.size());
        for (int i = 0; i < size; i++) {
            buildingService.save(new Building(buildingsNames.get(i), buildingsAddresses.get(i)));
        }
    }

    private void persistRoles() {
        persistStudentRole();
        persistAdminRole();
        persistTeacherRole();
    }

    private void persistStudentRole() {
        Set<Authority> authorities = new HashSet<>();
        for (String authorityName : studentAuthorities) {
            authorities.add(authorityService.findByName(authorityName));
        }
        Role role = new Role("Student", authorities);
        roleService.save(role);
    }

    private void persistAdminRole() {
        Set<Authority> authorities = new HashSet<>();
        for (String authorityName : adminAuthorities) {
            authorities.add(authorityService.findByName(authorityName));
        }
        Role role = new Role("Admin", authorities);
        roleService.save(role);
    }

    private void persistTeacherRole() {
        Set<Authority> authorities = new HashSet<>();
        for (String authorityName : teacherAuthorities) {
            authorities.add(authorityService.findByName(authorityName));
        }
        Role role = new Role("Teacher", authorities);
        roleService.save(role);
    }

    private void persistAuthorities() {
        for (String authorityName : authoritiesNames) {
            authorityService.save(new Authority(authorityName));
        }
    }


    private void persistClassrooms() {
        List<Building> buildings = buildingService.findAll();
        for (Building building : buildings) {
            for (int florNumber = 1; florNumber <= 3; florNumber++) {
                for (int roomNumber = 1; roomNumber <= 20; roomNumber++) {
                    classroomService.save(new Classroom(String.valueOf(florNumber * 100 + roomNumber), building));
                }
            }
        }
    }

    private void persistClassTimes() {
        Duration duration = Duration.ofMinutes(95);
        LocalTime firstClassStartTime = LocalTime.of(8, 30);

        for (int i = 1; i <= 5; i++) {
            classTimeService.save(new ClassTime(i, firstClassStartTime, duration));
            firstClassStartTime = firstClassStartTime.plusMinutes(duration.toMinutes());
        }
    }

    private void persistGroups() {
        List<Discipline> disciplines = disciplineService.findAll();
        for (int i = 1; i <= 4; i++) {
            for (int j = 0; j < 5; j++) {
                groupService.save(new Group(disciplinesShortNames.get(j) + "-" + i, disciplines.get(j)));
            }
        }
    }

    private void persistCourses() {
        disciplineCoursesNames.forEach((name) -> courseService.save(new Course(name)));
    }

    private void assignCoursesToGroups() {
        List<Course> allCourses = courseService.findAll();
        List<Group> groups;
        Set<Course> courses;
        List<Discipline> disciplines = disciplineService.findAll();
        for (int i = 0; i < disciplines.size(); i++) {
            courses = new HashSet<>();
            for (int j = i * disciplineCoursesNumber; j < i * disciplineCoursesNumber + disciplineCoursesNumber; j++) {
                courses.add(allCourses.get(j));
            }

            groups = groupService.findByDiscipline(disciplines.get(i));
            for (Group group : groups) {
                group.setCourses(courses);
                groupService.save(group);
            }
        }
    }

    private void persistTeachers() {
        int size = Integer.min(teachersFirstNames.size(), teachersLastNames.size());
        Teacher teacher;
        for (int i = 0; i < size; i++) {
            teacher = new Teacher(String.format("%s.%s.%s@%s", teachersFirstNames.get(i).toLowerCase(),
                teachersLastNames.get(i).toLowerCase(), "teacher", emailDomain),
                passwordEncoder.encode(String.format("%d%s", i, teachersFirstNames.get(i))), teachersFirstNames.get(i),
                teachersLastNames.get(i), roleService.findByName("Teacher"));
            teacherService.save(teacher);
        }
    }

    private void assignCoursesToTeachers() {
        List<Course> courses = courseService.findAll();
        List<Teacher> teachers = teacherService.findAll();
        Set<Course> coursesToAssign;
        Teacher teacher;
        for (int i = 0; i < teachers.size(); i++) {
            coursesToAssign = new HashSet<>();
            if (i < 5) {
                coursesToAssign.add(courses.get(i + 20));
            }
            coursesToAssign.add(courses.get(i));
            coursesToAssign.add(courses.get(i + 10));
            teacher = teachers.get(i);
            teacher.setCourses(coursesToAssign);
            teacherService.save(teacher);
        }
    }

    private void persistStudents() {
        int size = Integer.min(studentsFirstNames.size(), studentsLastNames.size());
        size = Integer.min(size, studentsPasswords.size());
        List<Group> groups = groupService.findAll();
        Student student;
        for (int i = 0; i < size; i++) {

            student = new Student(String.format("%s.%s%d@%s", studentsFirstNames.get(i).toLowerCase(),
                studentsLastNames.get(i).toLowerCase(), i, emailDomain),
                passwordEncoder.encode(studentsPasswords.get(i)), studentsFirstNames.get(i), studentsLastNames.get(i),
                roleService.findByName("Student"));
            student.setGroup(groups.get(i % groups.size()));
            studentService.save(student);
        }
    }

    private void generateScheduledClass() {
        List<DayScheduleItem> dayScheduleItemList = new ArrayList<>();

        List<Group> groupList = groupService.findByDiscipline(disciplineService.findAll().get(0));
        Course course = courseService.findByGroupsName(groupList.get(0).getName()).get(0);
        Teacher teacher = teacherService.findByCourses(course).get(0);
        Classroom classroom = classroomService.findByBuilding(buildingService.findAll().get(0)).get(0);
        DayScheduleItem dayScheduleItem =
            DayScheduleItem.builder().course(course).teacher(teacher).classTime(classTimeService.findByOrderNumber(1))
                .dayOfWeek(DayOfWeek.MONDAY).classType(classTypeService.findByName("Lecture"))
                .groups(new HashSet<>(groupList)).classroom(classroom).build();

        dayScheduleItemList.add(dayScheduleItem);

        dayScheduleItem =
            DayScheduleItem.builder().course(course).teacher(teacher).classTime(classTimeService.findByOrderNumber(4))
                .dayOfWeek(DayOfWeek.MONDAY).classType(classTypeService.findByName("Lecture"))
                .groups(new HashSet<>(groupList)).classroom(classroom).build();

        dayScheduleItemList.add(dayScheduleItem);

        groupList = groupService.findByDiscipline(disciplineService.findAll().get(1));
        course = courseService.findByGroupsName(groupList.get(0).getName()).get(0);
        teacher = teacherService.findByCourses(course).get(0);
        classroom = classroomService.findByBuilding(buildingService.findAll().get(1)).get(0);

        dayScheduleItem =
            DayScheduleItem.builder().course(course).teacher(teacher).classTime(classTimeService.findByOrderNumber(2))
                .dayOfWeek(DayOfWeek.WEDNESDAY).classType(classTypeService.findByName("Lecture"))
                .groups(new HashSet<>(groupList)).classroom(classroom).build();

        dayScheduleItemList.add(dayScheduleItem);

        scheduleGenerator.generate(LocalDate.of(2023, 9, 1), LocalDate.of(2023, 12, 20), dayScheduleItemList);
    }

    private void persistAdmin() {
        User admin =
            User.builder().email(adminEmail).password(passwordEncoder.encode(adminPassword)).firstName(adminFirstName)
                .lastName(adminLastName).role(roleService.findByName("Admin")).isEnable(true).build();
        userService.save(admin);
    }
}
