package com.in_out.repo;


import com.in_out.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  Employee findByAadharNumber(String paramString);
}
