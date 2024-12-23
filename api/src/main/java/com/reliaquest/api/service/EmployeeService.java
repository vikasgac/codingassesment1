package com.reliaquest.api.service;

import com.reliaquest.api.Entity.CreateMockEmployeeInput;
import com.reliaquest.api.Entity.Employee;
import com.reliaquest.api.Entity.EmployeeApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import static org.springframework.http.HttpMethod.*;

@Service
public class EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    @Value("${mockapp.external-url}")
    private String mockAppUrl;
    @Autowired
    RestTemplate restTemplate;

    //Get All Employees
    public List<Employee> getAllEmployees() {
        logger.info("The mock app URL value is: {}", mockAppUrl);
        try {
            ResponseEntity<EmployeeApiResponse<List<Employee>>> response = restTemplate.exchange(
                    mockAppUrl,
                    GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            return response.getBody().getData();
            //return (List<Employee>) response;
        } catch (Exception e) {
            logger.error("Error fetching employees: {}", e.getMessage());
            return List.of(); // returnn empty list in case of error
        }
    }

    //Get Employee bY id
    public Employee getEmployeeById(String id) {
        //String url = mockAppUrl + "/" + id;     //normal scenario
        String url =  "http://localhost:8112/api/v1/employee"+ "/" + id;   //change made for unit testing .as it was not picking value.
        logger.info("The url value for getEmployeeById is: {}", url);
        try{
            ResponseEntity<EmployeeApiResponse<Employee>> response = restTemplate.exchange(
                    url,
                    GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getBody() != null ? response.getBody().getData() : null;
        }
        catch (Exception e){
            logger.error("Employee ID not found {}", e.getMessage());
            return null;
        }

    }

    //Get Employees by name search
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        try {
            logger.info("Fetching employees with name containing: {}", searchString);
            ResponseEntity<EmployeeApiResponse<List<Employee>>> response = restTemplate.exchange(
                    mockAppUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            if (response.getStatusCode().is2xxSuccessful() && ( response.getBody() != null)) {
                return response.getBody().getData().stream()
                        .filter(employee -> employee.getEmployee_name().toLowerCase().contains(searchString.toLowerCase()))
                        .collect(Collectors.toList());
            }

            logger.warn("No employees found or invalid response for searchString: {}", searchString);
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("An error occurred while searching name: {}", e.getMessage());
            return Collections.emptyList();
        }

    }

    //Get highest salary
    public Integer getHighestSalaryOfEmployees() {
        try {
            logger.info("Fetching all employees information.");
            ResponseEntity<EmployeeApiResponse<List<Employee>>> response = restTemplate.exchange(
                    mockAppUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Extract the highest salary
                return response.getBody().getData().stream()
                        .map(Employee::getEmployee_salary)
                        .max(Comparator.naturalOrder())
                        .orElse(0);
            }
            logger.warn("No employees found or invalid response from the API.");
            return 0;
        } catch (Exception e) {
            logger.error("An error occurred while fetching the highest salary: {}", e.getMessage());
            return 0; // Return 0 in case of an error as well.
        }
    }

    //Get top 10 highest earning employees names
    public List<String> getTopTenHighestEarningEmployeeNames() {
        try {
            logger.info("Fetching all employees to determine the top 10 highest earners.");
            ResponseEntity<EmployeeApiResponse<List<Employee>>> response = restTemplate.exchange(
                    mockAppUrl,
                     GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getData().stream()
                        .sorted((o1, o2) -> o2.getEmployee_salary()- o1.getEmployee_salary())
                        .limit(10)
                        .map(e->e.getEmployee_name())
                        .collect(Collectors.toList());
            }
            logger.warn("No employees found so returning empty list!");
            return Collections.emptyList();
        } catch (Exception e) {
            logger.error("error while fetching top 10 salaried employees: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    ///Creating an employee
    public Employee createEmployee(CreateMockEmployeeInput employeeInput) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            //// Wrapping here input data in HttpEntity to include headers
            HttpEntity<CreateMockEmployeeInput> request = new HttpEntity<>(employeeInput, headers);

            ResponseEntity<EmployeeApiResponse<Employee>> response = restTemplate.exchange(
                    mockAppUrl,
                    POST,
                    request,
                    new ParameterizedTypeReference<>() {}
            );
            // here checking if its success or failure
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getData();
            } else {
                throw new RuntimeException("Failed to create employee.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while calling the mock server: " + e.getMessage(), e);
        }

    }

    //Deleting an employee /
    //since mock url doesnt support DELETE by id directly , fetech employee by id ->extract name -> then pass to mock DELETE endpoint.
    public String deleteEmployeeById(String id) {
        String getEmployeeUrl = mockAppUrl + "/" + id;
        String deleteEmployeeUrl = "http://localhost:8112/api/v1/employee";
        try {
            ResponseEntity<EmployeeApiResponse<Employee>> response = restTemplate.exchange(
                    getEmployeeUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );

            Employee employee = response.getBody().getData();
            if (employee == null || employee.getEmployee_name() == null) {
                throw new RuntimeException("Employee not found with ID: " + id);
            }
            //here creating the requestBody for Delete by Name Endpoint from mock server.
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", employee.getEmployee_name());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> deleteRequest = new HttpEntity<>(requestBody, headers);

            restTemplate.exchange(deleteEmployeeUrl, HttpMethod.DELETE, deleteRequest, Void.class);

            return "Employee " + employee.getEmployee_name() + " deleted successfully.";
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Employee not found with ID: " + id, e);
        } catch (Exception e) {
            throw new RuntimeException("Error while deleting employee", e);
        }
    }
}
