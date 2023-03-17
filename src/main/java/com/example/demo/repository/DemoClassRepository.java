package com.example.demo.repository;

import com.example.demo.model.DemoClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoClassRepository extends JpaRepository<DemoClass,Integer> {
}
