package com.dylanops.demo.student.controller;

import com.dylanops.demo.student.entity.Student;
import com.dylanops.demo.student.service.StudentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {

    Logger logger = LogManager.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @GetMapping("/all")
    public List<Student> getStudents() {
        return studentService.getStudents();
    }

    @PostMapping("/add")
    public Student add(@RequestBody @Validated Student student) throws JsonProcessingException {
        String jsonStudent = new ObjectMapper().writeValueAsString(student);
        logger.info("Add student " + jsonStudent);
        return studentService.save(student);
    }

    @GetMapping("/get/{id}")
    public Student get(@PathVariable Integer id) {
        logger.info("Get by Id " + id);
        return studentService.get(id).get();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        studentService.delete(id);
        return "done";
    }
}
