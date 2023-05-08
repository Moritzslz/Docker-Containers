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

        child.setFirstName("Thomas");
        child.setLastName("Mustermann");
        child.setBirthday(LocalDate.now());
        child = personRepository.save(child);

        if(child == null || parent == null) {
            assert false;
        }

        personService.addParent(child, parent);

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
        Person nParent1 = personRepository.save(parent1);

        parent2.setFirstName("Maria");
        parent2.setLastName("Mustermann");
        parent2.setBirthday(LocalDate.now());
        Person nParent2 = personRepository.save(parent2);

        parent3.setFirstName("Thomas");
        parent3.setLastName("Müller");
        parent3.setBirthday(LocalDate.now());
        Person nParent3 = personRepository.save(parent3);

        child.setFirstName("Gustav");
        child.setLastName("Mustermann");
        child.setBirthday(LocalDate.now());
        Person nChild = personRepository.save(child);

        personService.addParent(nChild, nParent1);
        personService.addParent(nChild, nParent2);
        assertThrowsExactly(ResponseStatusException(HttpStatus.BAD_REQUEST), personService.addParent(child, parent3));



    }
}
