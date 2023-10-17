package com.bankapp.service;

import com.bankapp.dto.request.EmployeeRequest;
import com.bankapp.dto.response.BankResponse;

import java.util.List;

public interface EmployeeService {

    BankResponse createEmployee (EmployeeRequest employeeRequest);

    List<EmployeeRequest> getAllEmployee();
}
