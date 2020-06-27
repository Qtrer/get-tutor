package com.example.springbootjpa.controller;

import com.example.springbootjpa.component.EncryptComponent;
import com.example.springbootjpa.component.RequestComponent;
import com.example.springbootjpa.entity.*;
import com.example.springbootjpa.service.ActionService;
import com.example.springbootjpa.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/tutor/")
public class TutorController {
    @Autowired
    private UserService userService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private RequestComponent requestComponent;
    @Autowired
    private PasswordEncoder encoder;

    /**
     * @describtion: Tutor level 1 page -> welcome
     * 1. Display welcome tutor information
     * 2. Display all the selected students
     * @param:
     * @return:
     */
    @GetMapping("index")
    public Map getIndex(){
        Tutor tutor = userService.getTutorById(requestComponent.getUid());
        List<Student> students = userService.getStudentsByTutorId(requestComponent.getUid());
        return Map.of(
                "tutor",tutor,
                "students",students
        );
    }

    //Student Information
    @PostMapping("student")
    public  Map addStudent(@Valid @RequestBody Student student){
        Tutor tutor = userService.getTutorById(requestComponent.getUid());
        if(userService.getUserByNumber(student.getUser().getNumber()) !=null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You have already added the student and cannot repeat it!");
        }
        User u = new User();
        u.setName(student.getUser().getName());
        u.setNumber(student.getUser().getNumber());
        u.setRole(User.Role.STUDENT);
        u.setPassword(encoder.encode(String.valueOf(student.getUser().getNumber())));
        Student s = new Student();
        s.setUser(u);
        userService.addStudent(s);
        List<Student> students = userService.listStudents();
        return Map.of(
                "students",students
        );

    }

    @DeleteMapping("student/{sid}")
    public Map deleteStudent(@PathVariable int sid) {
        userService.deleteStudent(sid);
        List<Student> students = userService.listStudents();
        return Map.of(
                "students",students
        );
    }

    @PatchMapping("student")
    public Map updateStudent(@Valid @RequestBody Student student) {
        Tutor tutor = userService.getTutorById(requestComponent.getUid());
        Student s = userService.getStudent(student.getId());
        if(s ==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The course you want to change doesn't exist");
        }
        User u = userService.getUserByID(student.getId());
        u.setNumber(student.getUser().getNumber());
        u.setName(student.getUser().getName());
        userService.updateUser(u);
        List<Student> students = userService.listStudents();
        return Map.of(
                "students",students
        );
    }

    @GetMapping("students")
    public Map getStudents(){
        List<Student> students = userService.listStudents();
        return Map.of(
                "students",students
        );
    }



    //Course Information
    @PostMapping("course")
    public Map addCourse(@Valid @RequestBody Course course) {
        Tutor tutor = userService.getTutorById(requestComponent.getUid());
        List<Course> courses = actionService.listCourseByTutorID(requestComponent.getUid());
        courses.forEach(c -> {
            if(c.getName().equals(course.getName())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "You have already added the course and cannot repeat it!");

            }
        });
        course.setTutor(tutor);
        actionService.addCourse(course);
        List<Course> courses1 = actionService.listCourseByTutorID(requestComponent.getUid());
        return Map.of(
                "courses",courses1
        );
    }

    @DeleteMapping("courses/{cid}")
    public Map deleteCourses(@PathVariable int cid) {
        actionService.deleteCourse(cid);
        List<Course> courses = actionService.listCourseByTutorID(requestComponent.getUid());
        return Map.of(
                "courses",courses,
                "massage","successful delete!"
        );
    }

    //Course weight Information
    @PatchMapping("course")
    public Map updateCourse(@Valid @RequestBody Course course) {
        Tutor tutor = userService.getTutorById(requestComponent.getUid());
        Course c = actionService.getCourse(course.getId());
        log.debug("{}", course.getId());
        if(c == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The course you want to change doesn't exist");
        }
        c.setWeight(course.getWeight());
        c.setLowestScore(course.getLowestScore());
        c.setName(course.getName());
        actionService.updateCourse(c);
        List<Course> courses = actionService.listCourseByTutorID(requestComponent.getUid());
        return Map.of(
                "courses",courses
        );
    }

    //Course Information Set to Default
    @PatchMapping("courses")
    public Map updateCourse() {
        Tutor tutor = userService.getTutorById(requestComponent.getUid());
        List<Course> courses = actionService.listCourseByTutorID(tutor.getId());
        courses.forEach(c->{
            Course course = actionService.getCourse(c.getId());
            course.setWeight(0.5);
            course.setLowestScore(60);
            actionService.updateCourse(course);
        });
        courses = actionService.listCourseByTutorID(tutor.getId());
        return Map.of(
                "courses",courses
        );
    }

    @GetMapping("courses")
    public Map getCourses(){
        List<Course> courses = actionService.listCourseByTutorID(requestComponent.getUid());
        return Map.of(
                "courses",courses
        );
    }


    //Electives Information
    @PostMapping("electives")
    public Map addElectives(@Valid @RequestBody List<Elective> electives){
        log.debug("asdadasd");
        log.debug("{}", requestComponent.getUid());
        Tutor tutor = userService.getTutorById(requestComponent.getUid());
        electives.forEach(elective -> {
            log.debug("{}", elective.getScore());
            Student student = elective.getStudent();
            Course course = elective.getCourse();
            if(student == null || course == null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Students and classes cannot be empty");
            }
            Student s = userService.getStudentByUserNumber(elective.getStudent().getUser().getNumber());
            Course c = actionService.getCourse(course.getName(), tutor.getId());
            //添加选课记录前，课程与学生都存在，还能查询选课记录是否存在
            if(c!=null && s!=null){
                Elective e = actionService.getElectiveByStudentIdAndCourseId(s.getId(), c.getId());
                log.debug("{}", e);
                //选课记录存在时，相当于选课记录中的成绩更新
                if(e !=null){
                    e.setScore(elective.getScore());
                    actionService.updateElective(e);
                }
                //选课记录不存在时，直接添加新的选课记录
                else {
                    Elective e2 = new Elective();
                    e2.setScore(elective.getScore());
                    e2.setStudent(userService.getStudent(s.getId()));
                    e2.setCourse(actionService.getCourse(course.getName(), tutor.getId()));
                    actionService.addElective(e2);
                }
            }
            //添加选课记录前，课程与学生不存在
            else {
                //学生不存在，添加学生
                if(s == null){
                    User u = new User();
                    u.setName(student.getUser().getName());
                    u.setNumber(student.getUser().getNumber());
                    u.setRole(User.Role.STUDENT);
                    u.setPassword(encoder.encode(String.valueOf(student.getUser().getNumber())));
                    s = new Student();
                    s.setUser(u);
                    userService.addStudent(s);
                }
                //课程不存在，添加课程，且设置默认权重为0.5，默认最低分为60
                if(c == null){
                    c = new Course();
                    c.setName(course.getName());
                    c.setTutor(tutor);
                    c.setLowestScore(60);
                    c.setWeight(0.5);
                    actionService.addCourse(c);
                }
                //选课记录必然不存在，添加选课记录
                Elective e1 = new Elective();
                e1.setScore(elective.getScore());
                e1.setStudent(userService.getStudent(s.getId()));
                e1.setCourse(actionService.getCourse(course.getName(), tutor.getId()));
                actionService.addElective(e1);
            }
        });
        return Map.of(
                "electives",electives
        );
    }

    //Directions Information
    @PostMapping("direction")
    public Map addDirection(@Valid @RequestBody Direction direction){
        List<Direction> directions = actionService.listDirections();
        directions.forEach(d -> {
            if(d.getName() == direction.getName()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "This direction already exists!");

            }
        });
        actionService.addDirection(direction);
        return Map.of(
                "direction",direction
        );
    }

    @DeleteMapping("direction")
    public Map deleteDirection(@PathVariable int did){
        actionService.deleteDirection(did);
        return Map.of(
                "massage","successful delete!"
        );
    }

    @PatchMapping("direction")
    public Map updateDirection(@Valid @RequestBody Direction direction){
        Direction d = actionService.getDirection(direction.getId());
        if(d==null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The direction you want to change does not exist");

        }
        d.setName(direction.getName());
        actionService.updateDirection(d);
        return Map.of(
                "direction",d
        );
    }

    @GetMapping("directions")
    public Map getDirections(){
        List<Direction> directions = actionService.listDirections();
        return Map.of(
                "directions",directions
        );
    }

    //Tutor Information
    @PatchMapping("information/total/{total}/reservedRange/{reservedRange}")
    public Map updateTutor(@PathVariable int total, @PathVariable int reservedRange){
        Tutor tutor = userService.getTutorById(requestComponent.getUid());
        tutor.setTotalNumber(total);
        tutor.setReservedRange(reservedRange);
        userService.updateTutor(tutor);
        return Map.of(
                "tutor",tutor
        );
    }

    //Student Ranking
    @GetMapping("ranking")
    public Map getRankingStudents(){
        log.debug("{}", "sadasdas");
        List<Student> students = actionService.totalStudents(requestComponent.getUid());
        students.forEach(s->{
            log.debug("{}", s.getId());
            log.debug("{}", s.getUser().getName());
            log.debug("{}", s.getWeightedScore());
            log.debug("{}", s.getUser().getNumber());
                }
        );
        return Map.of(
                "students",students
        );
    }

    //Elected in advance
    @PostMapping("advance")
    public Map electStudentAdvance(@Valid @RequestBody Student student){
        Tutor tutor = userService.getTutorById(requestComponent.getUid());
        int inst = userService.getStudentsByTutorId(requestComponent.getUid()).size();
        int toal = tutor.getTotalNumber();
        if(inst >= toal){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The number of students instructed has reached the upper limit！");
        }
        log.debug("{}", student.getUser().getNumber());
        if(userService.getUserByNumber(student.getUser().getNumber())==null){
            User u = new User();
            u.setName(student.getUser().getName());
            u.setNumber(student.getUser().getNumber());
            u.setRole(User.Role.STUDENT);
            u.setPassword(encoder.encode(String.valueOf(student.getUser().getNumber())));
            Student s = new Student();
            s.setUser(u);
            s.setTutor(tutor);
            userService.addStudent(s);
        }
        else {
            Student student1 = userService.getStudentByUserNumber(student.getUser().getNumber());
            if(student1.getTutor()!=null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The student has already chosen a tutor");
            }
            student1.setTutor(tutor);
            userService.updateStudent(student1);
        }
        tutor.setInstructedNumber(inst+1);
        userService.updateTutor(tutor);
        return Map.of(
                "student",student
        );
    }
}