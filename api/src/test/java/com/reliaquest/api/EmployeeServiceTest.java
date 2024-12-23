package com.reliaquest.api;


import com.reliaquest.api.Entity.Employee;
import com.reliaquest.api.Entity.EmployeeApiResponse;
import com.reliaquest.api.service.EmployeeService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    RestTemplate restTemplate;
    @InjectMocks
    EmployeeService employeeService;

    private static String mockAppUrl;

    @BeforeAll
    static void setUp() {
        mockAppUrl = "http://localhost:8112/api/v1/employee/";
    }

    @AfterAll
    static void cleanup() {
        mockAppUrl = null;
    }

    @Test
    public void test_EmployeeByIdSuccess(){
        String id = "1234";
        String url = mockAppUrl + id;

        Employee mockEmployee = new Employee();
        mockEmployee.setId(id);
        mockEmployee.setEmployee_name("John Wick");
        mockEmployee.setEmployee_age(32);
        mockEmployee.setEmployee_email("JohnWick@companny.com");
        mockEmployee.setEmployee_salary(498111);
        mockEmployee.setEmployee_title("Ethical Hacker");

        EmployeeApiResponse<Employee> mockResponse = new EmployeeApiResponse<>(mockEmployee, "Success!");

        Mockito.when(restTemplate.exchange(eq(url),eq(GET) ,eq(null),any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        Employee result = employeeService.getEmployeeById(id);
        Assertions.assertNotNull(result);
        Assertions.assertEquals("John Wick", result.getEmployee_name());
        Assertions.assertEquals(32, result.getEmployee_age());
        Assertions.assertEquals("JohnWick@companny.com", result.getEmployee_email());
        Assertions.assertEquals(498111, result.getEmployee_salary());
        Assertions.assertEquals("Ethical Hacker", result.getEmployee_title());

    }
    @Test
    public void testEmployeeById_Failure() {
        String id = "5678";
        String url = mockAppUrl + id;

        EmployeeApiResponse<Employee> mockResponse = new EmployeeApiResponse<>(null, "Failure!");
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity(mockResponse,HttpStatus.NOT_FOUND));
                //.thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        Employee result = employeeService.getEmployeeById(id);
        System.out.println(" result :"  + result);

        Assertions.assertNull(result);
    }

    //search by name:
    @Test
    public void testEmployeesByNameSearch_Success() {
        String searchString = "xyz";

        List<Employee> mockEmployees = Arrays.asList(
                new Employee("1", "xyz", 50000, 30, "Software Engineer", "xyz@example.com"),
                new Employee("2", "abc", 60000, 35, "Manager", "abc.doe@example.com")
        );

        EmployeeApiResponse<List<Employee>> mockResponse = new EmployeeApiResponse<>(mockEmployees, "Successfully processed request.");

        when(restTemplate.exchange(eq(mockAppUrl), eq(HttpMethod.GET), isNull(), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        List<Employee> result = employeeService.getEmployeesByNameSearch(searchString);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("xyz", result.get(0).getEmployee_name());
    }

}
