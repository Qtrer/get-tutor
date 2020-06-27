package com.example.springbootjpa.service;

import com.example.springbootjpa.controller.vo.CourseScoreVO;
import com.example.springbootjpa.entity.*;
import com.example.springbootjpa.repository.CourseRepository;
import com.example.springbootjpa.repository.DirectionRepository;
import com.example.springbootjpa.repository.ElectiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ActionService {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ElectiveRepository electiveRepository;
    @Autowired
    private DirectionRepository directionRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ActionService actionService;

    //---------"Course's CURD "-----------
    public Course addCourse(Course course){
        courseRepository.save(course);
        return course;
    }
    public Course addCourseWithTutor(Course course,int id){
        Tutor tutor = userService.getTutorById(id);
        course.setTutor(tutor);
        courseRepository.save(course);
        return course;
    }

    public void deleteCourse(int id){
        courseRepository.deleteById(id);
    }

    public Course updateCourse(Course course){
        courseRepository.save(course);
        return course;
    }

    public Course getCourse(String name,int tid){
        List<Course> courses = actionService.listCourseByTutorID(tid);
        Course course = null;
        for (Course c : courses) {
            if (c.getName().equals(name)) {
                course = c;
            }
        }
        return course;

    }
    public Course getCourse(int id){
        return courseRepository.findById(id).orElse(null);
    }
    public Course getCourse(String name){
        return courseRepository.findByName(name).orElse(null);
    }
    public List<Course> listCourses(){
        return courseRepository.findAll();
    }
    public List<Course> listCourseByTutorID(int id){
        return courseRepository.getCourseByTutor(id).orElse(List.of());
    }


    //---------"Elective's CURD "-----------

    public Elective addElective(Elective elective) {
        electiveRepository.save(elective);
        return elective;
    }

    public void deleteElective(int id) {
        electiveRepository.deleteById(id);
    }

    public Elective getElective(int id) {
        return electiveRepository.findById(id)
                .orElse(null);
    }

    public List<Elective> getElectiveByCourseId(int id) {
        return electiveRepository.getElectivesCourseId(id).orElse(List.of());
    }

    public List<Elective> getElectiveByStudentId(int id) {
        return electiveRepository.getElectivesStudentId(id)
                .orElse(List.of());
    }

    public Elective getElectiveByStudentIdAndCourseId(int sid,int cid) {
        return electiveRepository.getElectivesByStudentIdAndCourseId(sid, cid)
                .orElse(null);
    }

    public List<Elective> getElectiveByStudentIdAndTutorId(int sid,int tid) {
        List<Elective> electives = new ArrayList<>();
        List<Course> courses = actionService.listCourseByTutorID(tid);
        for(Course c:courses){
            Elective elective = actionService.getElectiveByStudentIdAndCourseId(sid, c.getId());
            if(elective!=null){
                electives.add(elective);
            }
        }
        return electives;
    }

    public Elective updateElective(Elective elective) {
        electiveRepository.save(elective);
        return elective;
    }


    //---------"Direction's CURD "-----------
    public Direction addDirection(Direction direction) {
        directionRepository.save(direction);
        return direction;
    }

    public Direction updateDirection(Direction direction) {
        directionRepository.save(direction);
        return direction;
    }

    public void deleteDirection(int did) {
        directionRepository.deleteById(did);
    }

    public List<Direction> listDirections() {
        return directionRepository.findAll();
    }

    public Direction getDirection(int id){
        return directionRepository.findById(id).orElse(null);
    }


    public List<Direction> getDirections(int sid){
        return directionRepository.getDirectionByStudentId(sid).orElse(List.of());
    }


    //---------"Calculate the overall Grade ranking"-----------
    public boolean checkSettings(int tid){
        List<Course> courses = actionService.listCourseByTutorID(tid);
        if (courses == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You haven't added any courses！");
        }
        courses.forEach(c->{
            if (c.getWeight()==0){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"There is a course that hasn't yet been weighted!");
            }
        });
        return true;
    }
    public float calculateWeightedScore(int sid,int tid){
        checkSettings(tid);
        float WeightedScore = 0;
        List<Elective> electives = actionService.getElectiveByStudentIdAndTutorId(sid, tid);
        for(Elective ele :electives) {
            WeightedScore += ele.getScore() * ele.getCourse().getWeight();
        }
        return WeightedScore;
    }

    public List<Student> totalStudents(int tid){
        List<Student> students = userService.listStudents();
        Map<Student, Float> studentScoreMap = new HashMap<>();
        students.forEach(s ->{
            float WeightedGrade = calculateWeightedScore(s.getId(), tid);
            studentScoreMap.put(s, WeightedGrade);
            userService.updateStudentWeightScore(s.getId(),WeightedGrade);

        });
        return studentScoreMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Student> SelectStudents(int tid){
        Tutor tutor = userService.getTutorById(tid);
        List<Student> students = totalStudents(tid);
        int ran = students.size() < tutor.getReservedRange() ? students.size() : tutor.getReservedRange();
        return students.subList(0, ran);

    }
    public boolean getQualification(int sid,int tid){
        boolean qualification = true;
        Student student = userService.getStudent(sid);
        List<Elective> electives = actionService.getElectiveByStudentId(sid);
        Tutor tutor =  student.getTutor();
        // Determine if each course is above the LowestMark
        for (Elective elective : electives) {
            Course course = elective.getCourse();
            if (elective.getScore() < course.getLowestScore()) {
                qualification = false;
            }
        }

        //Judge whether the students' comprehensive grades reach the standard
        List<Student> students = actionService.SelectStudents(tid);
        if (!students.contains(student)) {
            qualification = false;
        }

        if(tutor!=null){
            qualification = false;
        }
        return qualification;
    }

    public boolean checkQualification(int sid,int tid){
        Student student = userService.getStudent(sid);
        List<Elective> electives = actionService.getElectiveByStudentId(sid);
        Tutor tutor =  student.getTutor();
        // Determine if each course is above the LowestMark
        for (Elective elective : electives) {
            Course course = elective.getCourse();
            if (elective.getScore() < course.getLowestScore()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Your " + course.getName() + " course score is not up to the tutor's standard, " +
                                "the tutor's standard score is " + course.getLowestScore() +
                                ". Please contact other teachers as soon as possible");
            }
        }

        //Judge whether the students' comprehensive grades reach the standard
        List<Student> students = actionService.SelectStudents(tid);
        if (!students.contains(student)) {
            throw new  ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Your comprehensive score isn't up to the tutor's standard. Please contact other teachers as soon as possible");
        }

        if(tutor!=null){
            throw new  ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "You've already chosen tutor " + tutor.getUser().getName() + ".");
        }

        return true;
    }

    //the grade of a student's assigned course set
    public List<CourseScoreVO> listGradeByCourses(List<Course> courses, int sid) {
        List<CourseScoreVO> courseScoreVOS = new ArrayList<>();
        courses.forEach(c -> {
            CourseScoreVO courseScoreVO = new CourseScoreVO();
            Elective e = actionService.getElectiveByStudentIdAndCourseId(sid, c.getId());
            if(e !=null){
                courseScoreVO.setScore(e.getScore());
            }
            else {
                courseScoreVO.setScore(0);
            }
            courseScoreVO.setCourse(c);
            courseScoreVO.setSid(sid);
            courseScoreVOS.add(courseScoreVO);
                }
        );
        return courseScoreVOS;
    }

    public int getRankingIndex(List<Student> students,int sid){
        int rankIndex = 0;
        for (int i = 0; i < students.size(); i++) {
            if(students.get(i).getId() == sid)
                rankIndex = i+1;
        }
        return rankIndex;
    }
}
