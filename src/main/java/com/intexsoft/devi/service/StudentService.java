package com.intexsoft.devi.service;

import com.intexsoft.devi.entity.Student;
import com.intexsoft.devi.repository.StudentsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * @author DEVIAPHAN
 * Business Logic Service Class
 */
@Service
public class StudentService {
    private static final String ENTITY_NOT_FOUND_EXCEPTION = "EntityNotFoundException";
    private static final String STUDENT = "student";
    private static final String STUDENTS = "students";
    private static final String UPDATE_STUDENT_BY_ID = "Update student by id";
    private static final String UPDATE_BY_ID = "updateById";
    private static final String DELETED_BY_ID = "deletedById";
    private static final String ADD = "add";
    private static final String GET_STUDENT_BY_ID = "Get student by id";
    private static final String GET_BY_ID = "getById";
    private static final String GET_ALL = "getAll";
    @Autowired
    StudentsRepository studentsRepository;

    @Autowired
    GroupService groupService;

    @Autowired
    MessageSource messageSource;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(StudentService.class);

    /**
     * @param locale of messages
     * @return getAll student entities in the database.
     */
    public List<Student> getAll(Locale locale) {
        LOGGER.info(messageSource.getMessage(GET_ALL, new Object[]{STUDENTS}, locale));
        return studentsRepository.findAll();
    }

    /**
     * @param id     of student
     * @param locale of messages
     * @return student entity by ID in the database.
     */
    public Student getById(Long id, Locale locale) throws EntityNotFoundException {
        Optional<Student> studentsOptional = studentsRepository.findById(id);
        if (studentsOptional.isPresent()) {
            LOGGER.info(messageSource.getMessage(GET_BY_ID, new Object[]{STUDENT, id}, locale));
            return studentsOptional.get();
        }
        LOGGER.error(messageSource.getMessage(ENTITY_NOT_FOUND_EXCEPTION, new Object[]{GET_STUDENT_BY_ID, id}, locale));
        throw new EntityNotFoundException();
    }

    /**
     * @param student entity
     * @param groupId of group
     * @param locale  of messages
     * @return added student entity in the database.
     */
    @Transactional
    public Student save(Student student, Long groupId, Locale locale) {
        student.setGroup(groupService.getById(groupId, locale));
        LOGGER.info(messageSource.getMessage(ADD, new Object[]{STUDENT}, locale));
        return studentsRepository.save(student);
    }

    /**
     * @param student   entity
     * @param studentId of student
     * @param groupId   of group
     * @param locale    of messages
     * @return updated student entity in the database.
     * @throws EntityNotFoundException if there is no value
     */
    @Transactional
    public Student updateById(Student student, Long studentId, Long groupId, Locale locale) throws EntityNotFoundException {
        Optional<Student> studentsOptional = studentsRepository.findById(studentId);
        if (!studentsOptional.isPresent()) {
            LOGGER.error(messageSource.getMessage(ENTITY_NOT_FOUND_EXCEPTION, new Object[]{UPDATE_STUDENT_BY_ID, studentId}, locale));
            throw new EntityNotFoundException();
        }

        Student currentStudent = studentsOptional.get();
        currentStudent.setFirstName(student.getFirstName());
        currentStudent.setLastName(student.getLastName());
        currentStudent.setGroup(groupService.getById(groupId, locale));
        LOGGER.info(messageSource.getMessage(UPDATE_BY_ID, new Object[]{STUDENT, studentId}, locale));
        return studentsRepository.save(currentStudent);
    }

    /**
     * @param locale of messages
     * @param id     the student entity to be removed from the database
     */
    public void deleteById(Long id, Locale locale) {
        LOGGER.info(messageSource.getMessage(DELETED_BY_ID, new Object[]{STUDENT, id}, locale));
        studentsRepository.deleteById(id);
    }
}
