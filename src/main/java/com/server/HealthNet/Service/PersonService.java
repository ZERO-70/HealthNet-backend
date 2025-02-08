package com.server.HealthNet.Service;

import com.server.HealthNet.Model.Person;
import com.server.HealthNet.Repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Person getPersonById(Long id) {
        return personRepository.findById(id);
    }

    public int createPerson(Person person) {
        return personRepository.save(person);
    }

    public int updatePerson(Person person) {
        return personRepository.update(person);
    }

    public int deletePerson(Long id) {
        return personRepository.deleteById(id);
    }
}
