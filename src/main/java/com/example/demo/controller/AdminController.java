package com.example.demo.controller;

import com.example.demo.model.Courses;
import com.example.demo.model.DemoClass;
import com.example.demo.model.Person;
import com.example.demo.repository.CoursesRepository;
import com.example.demo.repository.DemoClassRepository;
import com.example.demo.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.jws.WebParam;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Controller
@RequestMapping("admin")
public class AdminController {
    @Autowired
    DemoClassRepository demoClassRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    CoursesRepository coursesRepository;

    @RequestMapping("/displayClasses")

    public ModelAndView displayClasses(Model model) {
        List<DemoClass> demoClasses = demoClassRepository.findAll();
        ModelAndView modelAndView = new ModelAndView("classes.html");
        modelAndView.addObject("demoClasses", demoClasses);
        modelAndView.addObject("demoClass", new DemoClass());
        return modelAndView;
    }

    @PostMapping("/addNewClass")
    public ModelAndView addNewClass(Model model, @ModelAttribute("demoClass") DemoClass demoClass) {
        demoClassRepository.save(demoClass);
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/displayClasses");
        return modelAndView;
    }

    @RequestMapping("/deleteClass")
    public ModelAndView deleteClass(Model model, @RequestParam int id) {
        Optional<DemoClass> demoClass = demoClassRepository.findById(id);
        for (Person person : demoClass.get().getPersons()) {
            person.setDemoClass(null);
            personRepository.save(person);
        }
        demoClassRepository.deleteById(id);
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/displayClasses");
        return modelAndView;
    }

    @GetMapping("/displayStudents")
    public ModelAndView displayStudents(Model model, @RequestParam int classId, HttpSession session,
                                        @RequestParam(value = "error", required = false) String error) {

        String errorMessage = null;
        ModelAndView modelAndView = new ModelAndView("students.html");
        Optional<DemoClass> demoClass = demoClassRepository.findById(classId);
        modelAndView.addObject("demoClass", demoClass.get());
        modelAndView.addObject("person", new Person());
        session.setAttribute("demoClass", demoClass.get());
        if (error != null) {
            errorMessage = "Invalid Error Message!";
            modelAndView.addObject("errorMessage", errorMessage);
        }
        return modelAndView;
    }

    @PostMapping("/addStudent")
    public ModelAndView addStudent(Model model, @ModelAttribute("person") Person person, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        DemoClass demoClass = (DemoClass) session.getAttribute("demoClass");
        Person personEntity = personRepository.readByEmail(person.getEmail());
        if (personEntity == null || !(personEntity.getPersonId() > 0)) {
            modelAndView.setViewName("redirect:/admin/displayStudents?classId=" + demoClass.getClassId()
                    + "&error=true");
            return modelAndView;
        }
        personEntity.setDemoClass(demoClass);
        personRepository.save(personEntity);
        demoClass.getPersons().add(personEntity);
        demoClassRepository.save(demoClass);
        modelAndView.setViewName("redirect:/admin/displayStudents?classId=" + demoClass.getClassId());
        return modelAndView;
    }

    @GetMapping("/deleteStudent")
    public ModelAndView deleteStudent(Model model, @RequestParam int personId, HttpSession session) {
        DemoClass demoClass = (DemoClass) session.getAttribute("demoClass");
        Optional<Person> person = personRepository.findById(personId);
        person.get().setDemoClass(null);
        demoClass.getPersons().remove(person.get());
        DemoClass demoClassSaved = demoClassRepository.save(demoClass);
        session.setAttribute("demoClass", demoClassSaved);
        ModelAndView modelAndView = new ModelAndView("redirect:/admin/displayStudents?classId=" + demoClass.getClassId());
        return modelAndView;
    }

    @GetMapping("/displayCourses")
    public ModelAndView displayCourses(Model model) {
        //List<Courses> courses = coursesRepository.findByOrderByName();
        //dynamic sorting
        List<Courses> courses=coursesRepository.findAll(Sort.by("name").descending());
        ModelAndView modelAndView = new ModelAndView("courses_secure.html");
        modelAndView.addObject("courses", courses);
        modelAndView.addObject("course", new Courses());
        return modelAndView;
    }

    @PostMapping("/addNewCourse")
    public ModelAndView addNewCourse(Model model, @ModelAttribute("course") Courses course) {
        ModelAndView modelAndView = new ModelAndView();
        coursesRepository.save(course);
        modelAndView.setViewName("redirect:/admin/displayCourses");
        return modelAndView;
    }

    @GetMapping("/viewStudents")
    public ModelAndView viewStudents(Model model, @RequestParam int id, HttpSession session,
                                     @RequestParam(required = false) String error) {
        String errorMessage = null;
        ModelAndView modelAndView = new ModelAndView("course_students.html");
        Optional<Courses> courses = coursesRepository.findById(id);
        modelAndView.addObject("courses", courses.get());
        modelAndView.addObject("person", new Person());
        session.setAttribute("courses", courses.get());
        if (error != null) {
            errorMessage = "Invalid Email";
            modelAndView.addObject("errorMessage", errorMessage);

        }
        return modelAndView;
    }

    @PostMapping("/addStudentToCourse")
    public ModelAndView addStudentToCourse(Model model, @ModelAttribute("person") Person person, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();
        Courses courses = (Courses) session.getAttribute("courses");

        Person personEntity = personRepository.readByEmail(person.getEmail());

        if (personEntity == null || !(personEntity.getPersonId() > 0)) {
            modelAndView.setViewName("redirect:/admin/viewStudents?id=" + courses.getCourseId() + "&error=true");
            return modelAndView;
        }
        personEntity.getCourses().add(courses);
        courses.getPersons().add(personEntity);
        personRepository.save(personEntity);
        session.setAttribute("courses", courses);
        modelAndView.setViewName("redirect:/admin/viewStudents?id=" + courses.getCourseId());
        return modelAndView;
    }
//courses.getPersons().stream().filter(el -> el.getPersonId() != personId).findAny()
    @GetMapping("/deleteStudentFromCourse")
    public ModelAndView deleteStudentFromCourse(Model model, @RequestParam int personId,
                                                HttpSession session) {
        Courses courses = (Courses) session.getAttribute("courses");
        Optional<Person> person = personRepository.findById(personId);
//        person.get().getCourses().remove(courses.getCourseId());

        Set<Courses> updatedCourses = new HashSet<Courses>();
        for (Courses course : person.get().getCourses()) {
            if(!(course.getCourseId()==courses.getCourseId())){
                updatedCourses.add(course);

            }
        }
        person.get().setCourses(updatedCourses);

        courses.getPersons().remove(person);
        personRepository.save(person.get());
        session.setAttribute("courses",courses);
        ModelAndView modelAndView = new
                ModelAndView("redirect:/admin/viewStudents?id="+courses.getCourseId());
        return modelAndView;
    }
}

