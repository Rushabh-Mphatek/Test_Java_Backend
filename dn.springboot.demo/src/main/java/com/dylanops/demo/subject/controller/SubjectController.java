package com.dylanops.demo.subject.controller;

import com.dylanops.demo.student.entity.Student;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subject")
public class SubjectController {

    Logger logger = LogManager.getLogger(SubjectController.class);

    @GetMapping("/all")
    public String getAll() {
        logger.warn("logger testing.");
        return "subject all";
    }

    @GetMapping("/fatal")
    public String getFatal() {
        logger.info("demo Fatal.");
        try {
            var a = 0;
            var b = 1;
            var test = b / a;
        } catch (Exception exception) {
            logger.fatal("Error " + exception.getMessage());
        }

        var student = new Student();
        var result = student.getHeight() / 2;


        return "subject all";
    }
}
