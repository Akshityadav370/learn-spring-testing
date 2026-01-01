package com.testing.learn_testing.controllers;

import com.testing.learn_testing.TestContainerConfiguration;
import com.testing.learn_testing.dto.EmployeeDto;
import com.testing.learn_testing.entities.Employee;
import com.testing.learn_testing.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainerConfiguration.class)
class EmployeeControllerTestIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    Employee testEmployee = Employee.builder()
            .email("anuj@gmail.com")
            .name("Anuj")
            .salary(200L)
            .build();
    EmployeeDto testEmployeeDto = EmployeeDto.builder()
            .email("anuj@gmail.com")
            .name("Anuj")
            .salary(200L)
            .build();

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    void testGetEmployeeById_success() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        ResponseEntity<EmployeeDto> response =
                restTemplate.getForEntity(
                        "/employees/{id}",
                        EmployeeDto.class,
                        savedEmployee.getId()
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(savedEmployee.getId());
        assertThat(response.getBody().getEmail()).isEqualTo(savedEmployee.getEmail());
    }

    @Test
    void testGetEmployeeById_Failure() {
        ResponseEntity<String> response =
                restTemplate.getForEntity("/employees/1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testCreateNewEmployee_whenEmployeeAlreadyExists_thenThrowException() {
        employeeRepository.save(testEmployee);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "/employees",
                        testEmployeeDto,
                        String.class
                );

        assertThat(response.getStatusCode().is5xxServerError()).isTrue();
    }

    @Test
    void testCreateNewEmployee_whenEmployeeDoesNotExists_thenCreateEmployee() {
        ResponseEntity<EmployeeDto> response =
                restTemplate.postForEntity(
                        "/employees",
                        testEmployeeDto,
                        EmployeeDto.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo(testEmployeeDto.getEmail());
        assertThat(response.getBody().getName()).isEqualTo(testEmployeeDto.getName());
    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        HttpEntity<EmployeeDto> request = new HttpEntity<>(testEmployeeDto);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        "/employees/999",
                        HttpMethod.PUT,
                        request,
                        String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateTheEmail_thenThrowException() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        testEmployeeDto.setName("Random Name");
        testEmployeeDto.setEmail("random@gmail.com");

        HttpEntity<EmployeeDto> request = new HttpEntity<>(testEmployeeDto);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        "/employees/{id}",
                        HttpMethod.PUT,
                        request,
                        String.class,
                        savedEmployee.getId()
                );

        assertThat(response.getStatusCode().is5xxServerError()).isTrue();
    }

    @Test
    void testUpdateEmployee_whenEmployeeIsValid_thenUpdateEmployee() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        testEmployeeDto.setName("Random Name");
        testEmployeeDto.setSalary(250L);

        HttpEntity<EmployeeDto> request = new HttpEntity<>(testEmployeeDto);

        ResponseEntity<EmployeeDto> response =
                restTemplate.exchange(
                        "/employees/{id}",
                        HttpMethod.PUT,
                        request,
                        EmployeeDto.class,
                        savedEmployee.getId()
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("Random Name");
        assertThat(response.getBody().getSalary()).isEqualTo(250L);
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException() {
        ResponseEntity<Void> response =
                restTemplate.exchange(
                        "/employees/1",
                        HttpMethod.DELETE,
                        null,
                        Void.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void testDeleteEmployee_whenEmployeeExists_thenDeleteEmployee() {
        Employee savedEmployee = employeeRepository.save(testEmployee);

        ResponseEntity<Void> response =
                restTemplate.exchange(
                        "/employees/{id}",
                        HttpMethod.DELETE,
                        null,
                        Void.class,
                        savedEmployee.getId()
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<Void> secondTry =
                restTemplate.exchange(
                        "/employees/{id}",
                        HttpMethod.DELETE,
                        null,
                        Void.class,
                        savedEmployee.getId()
                );

        assertThat(secondTry.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}