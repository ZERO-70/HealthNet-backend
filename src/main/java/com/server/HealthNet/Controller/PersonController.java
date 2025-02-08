package com.server.HealthNet.Controller;

import com.server.HealthNet.Model.Patient;
import com.server.HealthNet.Model.Person;
import com.server.HealthNet.Model.UserAuthentication;
import com.server.HealthNet.Service.PersonService;
import com.server.HealthNet.Service.UserAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/persons")
@CrossOrigin(origins = "*")
public class PersonController {
    private final PersonService personService;

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Person> getAllPersons() {
        return personService.getAllPersons();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        Person person = personService.getPersonById(id);
        return person != null ? ResponseEntity.ok(person) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createPerson(@RequestBody Person person) {
        System.out.println(person);
        return personService.createPerson(person) > 0
                ? new ResponseEntity<>("Person Inserted successfully", HttpStatus.OK)
                : new ResponseEntity<>("Person Insertion failed", HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updatePerson(@PathVariable Long id, @RequestBody Person person) {
        person.setId(id);
        return personService.updatePerson(person) > 0
                ? new ResponseEntity<>("Person updated successfully", HttpStatus.OK)
                : new ResponseEntity<>("Person Update failed", HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePerson(@PathVariable Long id) {
        return personService.deletePerson(id) > 0
                ? new ResponseEntity<>("Person deleted successfully", HttpStatus.OK)
                : new ResponseEntity<>("Person Deletion failed", HttpStatus.NOT_FOUND);
    }

    @GetMapping("/getmine")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Person> getmyperson() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuthentication = userAuthenticationService.getUserByUsername(username);
        if (userAuthentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Person person = personService.getPersonById(userAuthentication.getPersonId());

        if (person != null) {
            return new ResponseEntity<>(person, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(person, HttpStatus.NOT_FOUND);
        }
    }
}
