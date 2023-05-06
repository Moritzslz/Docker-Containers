package de.tum.in.ase.eist.service;

import de.tum.in.ase.eist.model.Person;
import de.tum.in.ase.eist.repository.PersonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Person save(Person person) {
        if (person.getBirthday().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Birthday may not be in the future");
        }
        return personRepository.save(person);
    }

    public void delete(Person person) {
        personRepository.delete(person);
    }

    public Optional<Person> getById(Long id) {
        return personRepository.findWithParentsAndChildrenById(id);
    }

    public List<Person> getAll() {
        return personRepository.findAll();
    }

    public Person addParent(Person person, Person parent) {
        Set parents = person.getParents();
        if (parents.size() < 2) {
            parents.add(parent);
            person.setParents(parents);
            return save(person);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public Person addChild(Person person, Person child) {
        Set childParents  = child.getParents();
        if (childParents.size() < 2 || childParents.contains(person)) {
            Set children = person.getChildren();
            children.add(child);
            person.setChildren(children);
            return save(person);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public Person removeParent(Person person, Person parent) {
        Set personParents = person.getParents();
        if (personParents.size() > 1) {
            personParents.remove(parent);
            person.setParents(personParents);
            return save(person);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public Person removeChild(Person person, Person child) {
        Set childParents = child.getParents();
        if (childParents.size() > 1) {
            Set children = person.getChildren();
            children.remove(child);
            person.setChildren(children);
            return save(person);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }
}