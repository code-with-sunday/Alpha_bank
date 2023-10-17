package com.bankapp.service.serviceImpl;

import com.bankapp.dto.request.EmailDetails;
import com.bankapp.dto.request.EmployeeRequest;
import com.bankapp.dto.response.BankResponse;
import com.bankapp.entity.Employee;
import com.bankapp.repository.EmployeeRepository;
import com.bankapp.service.EmailService;
import com.bankapp.service.EmployeeService;
import com.bankapp.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImple implements EmployeeService {
    final EmployeeRepository employeeRepository;
    final EmailService emailService;

    @Autowired
    public EmployeeServiceImple(EmployeeRepository employeeRepository, EmailService emailService) {
        this.employeeRepository = employeeRepository;
        this.emailService = emailService;
    }


    @Override
    public BankResponse createEmployee(EmployeeRequest employeeRequest) {
        if (employeeRepository.existsByEmail(employeeRequest.getEmail())){
            BankResponse response = BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXITS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXITS_MESSAGE)
                    .accountInfo(null)
                    .build();

            return response;
        }

        Employee newEmployee = Employee.builder()
                .firstName(employeeRequest.getFirstName())
                .lastName(employeeRequest.getLastName())
                .email(employeeRequest.getEmail())
                .build();

        Employee savedEmployee = employeeRepository.save(newEmployee);

        EmailDetails emailDetails =EmailDetails.builder()
                .recipient(savedEmployee.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations, your account have been successfully created. \n your account details  : \n" +
                        savedEmployee.getFirstName() +" " + savedEmployee.getLastName() )
                .build();
        emailService.sendEmailAlert(emailDetails);


        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(null)
                .build();
    }

    @Override
    public List<EmployeeRequest> getAllEmployee() {
        List<Employee> employeesEntities = employeeRepository.findAll();

        List<EmployeeRequest> employeeRequests = employeesEntities
                .stream()
                .map(emp -> new EmployeeRequest(emp.getId(),
                        emp.getFirstName(), emp.getLastName(), emp.getEmail(),
                        emp.getCreatedAt(),emp.getModifiedAt()))
                .collect(Collectors.toList());

        return employeeRequests;
    }
}
