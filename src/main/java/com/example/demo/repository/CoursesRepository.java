package com.example.demo.repository;

import com.example.demo.model.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoursesRepository extends JpaRepository<Courses,Integer> {

    //static sorting
    List<Courses> findByOrderByNameDesc();

    List<Courses> findByOrderByName();
}
