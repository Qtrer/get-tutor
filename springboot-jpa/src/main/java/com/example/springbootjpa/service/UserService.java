package com.example.springbootjpa.service;

import com.example.springbootjpa.entity.*;
import com.example.springbootjpa.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TutorRepository tutorRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ActionService actionService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder encoder;

    //---------"User's CURD "-----------
    public User updateUserPassword(int id, String password){
        User u = userService.getUserByID(id);
        u.setPassword(encoder.encode(password));
        userRepository.save(u);
        return u;
    }
    public User getUserByID(int id){
        return userRepository.findById(id).orElse(null);
    }
    public User getUserByNumber(int number) {
        return  userRepository.findByNumber(number).orElse(null);
    }
    public User getUserByName(String name){
        return userRepository.findByName(name).orElse(null);
    }



    //---------"Tutor's CURD "-----------
    public Tutor addTutor(Tutor tutor){
        tutorRepository.save(tutor);
        return tutor;
    }

    public void deleteTutor(int id){
        tutorRepository.deleteById(id);
    }

    public Tutor updateTutor(Tutor tutor){
        tutorRepository.save(tutor);
        return tutor;
    }

    //The User and Tutor share the same primary key
    public Tutor getTutorById(int id){
        return tutorRepository.findById(id).orElse(new Tutor());
    }
    public List<Tutor> listTutors(){
        return tutorRepository.findAll();
    }

    //---------"Student's CURD "-----------
    public Student addStudent(Student student){
        studentRepository.save(student);
        return student;
    }

    public void deleteStudent(int id){
        studentRepository.deleteById(id);
    }

    public Student updateStudent(Student student){
        studentRepository.save(student);
        return student;
    }

    public Student updateStudentWeightScore(int sid,float weightedScore){
        Student s = userService.getStudent(sid);
        s.setWeightedScore(weightedScore);
        studentRepository.save(s);
        return s;
    }

    public Student getStudentByUserNumber(int number){
        return studentRepository.getStudentsByUserNumber(number).orElse(null);
    }
    public List<Student> listStudents() {
        return studentRepository.findAll();
    }
    //The User and Student share the same primary key
    public Student getStudent(int id) {
        return studentRepository.findById(id).orElse(new Student());
    }
    public List<Student> getStudentsByTutorId(int tid){
        return studentRepository.getStudentsByTutorId(tid).orElse(List.of());
    }

    //---------"User's CURD "-----------

    public User updateUser(User user){
        userRepository.save(user);
        return user;
    }
}
