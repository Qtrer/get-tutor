package com.example.springbootjpa.controller;

import com.example.springbootjpa.component.RequestComponent;
import com.example.springbootjpa.entity.*;
import com.example.springbootjpa.service.ActionService;
import com.example.springbootjpa.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/student/")
public class StudentController {
    @Autowired
    private UserService userService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private RequestComponent requestComponent;

    /**
     * @describtion: Student level 1 page -> welcome
     * 1. Display welcome student information
     * 2. Display all mentors
     * 3. Front-end rendering optional or not optional (Tutor)
     * @param:
     * @return:
     */
    @GetMapping("index")
    public Map getIndex(){
        Student student = userService.getStudent(requestComponent.getUid());
        List<Tutor> tutors = userService.listTutors();
        return Map.of(
                "student",student,
                "tutors",tutors
        );
    }

    /**
     * @describtion: Student level 2 page -> Specific tutor course information
     * 1. All subjects offered by the teacher
     * 2. The lowest score in the subject The teacher set
     * 3. Students' scores for each subject
     * 4. Student's overall score ranking
     * 5. Front-end rendering standard and substandard (grade)
     * @param:
     * @return:
     */
    @GetMapping("information/{tid}")
    public Map getInformation(@PathVariable int tid){
        boolean qualified = false;
        Tutor tutor = userService.getTutorById(tid);
        List<Course> courses = actionService.listCourseByTutorID(tid);
        List<Elective> electives = actionService.getElectiveByStuIdAndTurId(requestComponent.getUid(),tid);
        List<Student> students = actionService.RankStudents(tid);
        qualified = actionService.getQualification(requestComponent.getUid(),tid);
        return Map.of(
                "courses",courses,
                "electives",electives,
                "students",students,
                "qualified",qualified
        );
    }

    /**
     * @describtion: Student level 3 page ->Fill in the application form
     * 1. Fill in the application information for choosing a tutor
     * @param:
     * @return:
     */
    @GetMapping("directions")
    public Map listDirections(){
        return Map.of(
                "directions",actionService.listDirections()
        );
    }
    @GetMapping("{sid}/directions")
    public Map getDirection(@PathVariable int sid){
        return Map.of(
                "direction",actionService.getDirections(sid)
        );
    }
    @PatchMapping("directions")
    public Map updateStudentDirections(@RequestBody List<Direction> newDirections) {
        Student student = userService.getStudent(requestComponent.getUid());
        List<Direction> oldDirections = student.getDirections();
        oldDirections.forEach(direction -> {
            actionService.deleteDirection(direction.getId());
        });
        newDirections.forEach(direction -> {
            direction.setStudent(student);
            actionService.addDirection(direction);
        });
        return Map.of(
                "directions",newDirections
        );
    }

    @GetMapping("tutor/{tid}")
    public Map getTutor(@PathVariable int tid){
        String massage = "Sorry, your application failed";
        Tutor tutor = userService.getTutorById(tid);
        if(tutor.getInstructedNumber()>=tutor.getTotalNumber()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Sorry, there is no vacancy for the teacher, please contact other teachers immediately.");
        }
        log.debug("{}", actionService.checkQualification(requestComponent.getUid(),tid));

        if(actionService.checkQualification(requestComponent.getUid(),tid)){
            Student student = userService.getStudent(requestComponent.getUid());
            student.setTutor(tutor);
            int inst = userService.getStudentsByTutorId(requestComponent.getUid()).size();
            tutor.setInstructedNumber(inst+1);
            userService.updateTutor(tutor);
            userService.updateStudent(student);
            massage = "Congratulations, your application has been successful";
        }
        return Map.of(
                "massage",massage
        );

    }


}
