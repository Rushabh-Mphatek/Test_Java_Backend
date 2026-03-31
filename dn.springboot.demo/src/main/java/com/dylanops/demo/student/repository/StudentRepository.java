package com.dylanops.demo.student.repository;

import com.dylanops.demo.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}
