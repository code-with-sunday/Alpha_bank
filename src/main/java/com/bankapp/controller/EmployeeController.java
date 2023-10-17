package com.bankapp.controller;

import com.bankapp.dto.request.EmployeeRequest;
import com.bankapp.dto.response.BankResponse;
import com.bankapp.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/")
public class EmployeeController {
    final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/employee")
    public BankResponse createEmployee(@RequestBody EmployeeRequest employeeRequest){
        return employeeService.createEmployee(employeeRequest);
    }

    @GetMapping("/allEmployees")
    public List<EmployeeRequest> getAllEmployee(){
        return employeeService.getAllEmployee();
    }
}
