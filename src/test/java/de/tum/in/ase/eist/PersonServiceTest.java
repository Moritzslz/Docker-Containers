package de.tum.in.ase.eist;

import de.tum.in.ase.eist.model.Person;
import de.tum.in.ase.eist.repository.PersonRepository;
import de.tum.in.ase.eist.service.PersonService;
import liquibase.pro.packaged.P;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class PersonServiceTest {
    @Autowired
    private PersonService personService;
    @Autowired
    private PersonRepository personRepository;

    @Test
    void testAddPerson() {
        var person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setBirthday(LocalDate.now());

        personService.save(person);

        assertEquals(1, personRepository.findAll().size());
    }

    @Test
    void testDeletePerson() {
        var person = new Person();
        person.setFirstName("Max");
        person.setLastName("Mustermann");
        person.setBirthday(LocalDate.now());

        person = personRepository.save(person);

        personService.delete(person);

        assertTrue(personRepository.findAll().isEmpty());
    }

    @Test
    void testAddParent() {
        Person parent = new Person();
        Person child = new Person();


        parent.setFirstName("Max");
        parent.setLastName("Mustermann");
        parent.setBirthday(LocalDate.now());
        parent = personRepository.save(parent);
        assertEquals(1, personRepository.findAll().size());

        child.setFirstName("Thomas");
        child.setLastName("Mustermann");
        child.setBirthday(LocalDate.now());
        child = personRepository.save(child);
        assertEquals(2, personRepository.findAll().size());

        personService.addParent(child, parent);

        assertEquals(1, child.getParents().size());
        assertTrue(child.getParents().contains(parent));
    }

    @Test
    void testAddThreeParents() {
        Person parent1 = new Person();
        Person parent2 = new Person();
        Person parent3 = new Person();
        Person child = new Person();

        parent1.setFirstName("Max");
        parent1.setLastName("Mustermann");
        parent1.setBirthday(LocalDate.now());
        parent1 = personRepository.save(parent1);
        assertEquals(1, personRepository.findAll().size());

        parent2.setFirstName("Maria");
        parent2.setLastName("Mustermann");
        parent2.setBirthday(LocalDate.now());
        parent2 = personRepository.save(parent2);
        assertEquals(2, personRepository.findAll().size());

        parent3.setFirstName("Thomas");
        parent3.setLastName("Müller");
        parent3.setBirthday(LocalDate.now());
        parent3 = personRepository.save(parent3);
        assertEquals(3, personRepository.findAll().size());

        child.setFirstName("Gustav");
        child.setLastName("Mustermann");
        child.setBirthday(LocalDate.now());
        child = personRepository.save(child);
        assertEquals(4, personRepository.findAll().size());

        Person child2 = personService.addParent(child, parent1);
        assertEquals(1, child.getParents().size());
        assertTrue(child.getParents().contains(parent1));

        Person child3 = personService.addParent(child2, parent2);
        assertEquals(2, child2.getParents().size());
        assertTrue(child2.getParents().contains(parent2));

        Person finalParent3 = parent3;
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            personService.addParent(child3, finalParent3);
        });

        assertEquals(exception.getStatusCode(), HttpStatus.BAD_REQUEST);
    }
}
