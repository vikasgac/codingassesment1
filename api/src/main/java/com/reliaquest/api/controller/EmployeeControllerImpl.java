package com.reliaquest.api.controller;

import com.reliaquest.api.Entity.CreateMockEmployeeInput;
import com.reliaquest.api.Entity.Employee;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empapi/v1/employee")
public class EmployeeControllerImpl implements IEmployeeController <Employee,CreateMockEmployeeInput>{
    private static final Logger logger = LoggerFactory.getLogger(EmployeeControllerImpl.class);

    @Autowired
    EmployeeService employeeService;

    @Override
    @GetMapping("")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        if (employees.isEmpty()) {
            logger.warn("No employees found");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(employees);
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        logger.info("received request to search employees with searchString : {}", searchString);

        List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);

        if (employees.isEmpty()) {
            logger.warn("No employees found for searchString: {}", searchString);
            return ResponseEntity.noContent().build(); // Return 204 if no employees match
        }
        return ResponseEntity.ok(employees);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        logger.info("Fetching employee with ID: {}", id);
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            logger.error("Employee with ID {} not found", id);
            return ResponseEntity.notFound().build();  //this is for 404
        }
        return ResponseEntity.ok(employee);
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("Received request to fetch the highest salary");

        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();

        if (highestSalary == 0) {
            logger.warn("No salaries are avaialbel.");
            return ResponseEntity.noContent().build(); // Returning  204 if no data is available
        }

        return ResponseEntity.ok(highestSalary); // Returning the highest salary with 200 OK
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        logger.info("Received request to fetch the top 10 highest earning employees' names.");

        List<String> topTenNames = employeeService.getTopTenHighestEarningEmployeeNames();

        if (topTenNames.isEmpty()) {
            logger.warn("No data available for top 10 highest earners.");
            return ResponseEntity.noContent().build(); // Return 204 if no data is available
        }

        return ResponseEntity.ok(topTenNames); // Return the list with 200 OK
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@Valid @RequestBody CreateMockEmployeeInput employeeInput) {
        logger.info("Creating anew employee!");
        Employee employee = employeeService.createEmployee(employeeInput);
        if(employee==null){
            logger.info("Entering in the if block!");
            return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        try {
            String result = employeeService.deleteEmployeeById(id);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
