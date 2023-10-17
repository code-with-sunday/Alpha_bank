package com.bankapp.repository;

import com.bankapp.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Boolean existsByEmail(String email);
}
