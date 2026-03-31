package com.dylanops.demo.student.service;

import com.dylanops.demo.student.entity.Student;
import com.dylanops.demo.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getStudents() {
        return studentRepository.findAll();
    }

    public Student save(Student student) {
       return studentRepository.save(student);
    }

    public Optional<Student> get(Integer id) {
        return studentRepository.findById(id);
    }

    public void delete(Integer id) {
        studentRepository.deleteById(id);
    }
}
