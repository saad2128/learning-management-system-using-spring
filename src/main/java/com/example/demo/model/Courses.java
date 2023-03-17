package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name="test_courses")
public class Courses {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO,generator = "native")
  @GenericGenerator(name="native",strategy = "native")
  private int courseId;

  private String name;

  private String fees;

  @ManyToMany(mappedBy = "courses",fetch = FetchType.EAGER,cascade = CascadeType.PERSIST)
  private Set<Person> persons = new HashSet<>();
}
