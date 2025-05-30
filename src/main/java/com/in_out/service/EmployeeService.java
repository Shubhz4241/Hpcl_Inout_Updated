package com.in_out.service;

import com.in_out.model.Employee;
import com.in_out.model.Inscan;
import com.in_out.model.Licensegate;
import com.in_out.repo.EmployeeRepository;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
	@Autowired
	private EmployeeRepository employeeRepository;

	@Autowired
	private InscanService inscanService;

	@Autowired
	private LicenseGateService licenseGateService;

	public List<Employee> addEmployee(List<Integer> employeeData) {
		List<Employee> employee = employeeData.stream().map(intValue -> new Employee()).toList();
		return this.employeeRepository.saveAll(employee);
	}

	public List<Employee> getAllEmployeeDetails() {
		return this.employeeRepository.findAll();
	}

	public Optional<Employee> getEmployeeById(Long id) {
		return this.employeeRepository.findById(id);
	}

	public Employee addEmployee(Employee employee) {
		Employee existingEmployee = this.employeeRepository.findByAadharNumber(employee.getAadharNumber());
		if (existingEmployee != null)
			throw new IllegalArgumentException("An Transportor with the same Aadhar number already exists");
		return (Employee) this.employeeRepository.save(employee);
	}

	public Employee updateEmployee(Long id, Employee updatedEmployee) {
		Optional<Employee> existingEmployee = this.employeeRepository.findById(id);
		if (existingEmployee.isPresent()) {
			Employee employeeToUpdate = existingEmployee.get();
			Employee existingEmployeeWithNewAadhar = this.employeeRepository
					.findByAadharNumber(updatedEmployee.getAadharNumber());
			if (existingEmployeeWithNewAadhar != null && !existingEmployeeWithNewAadhar.getId().equals(id))
				throw new IllegalArgumentException("An Employee with the same Aadhar number already exists");
			employeeToUpdate.setAadharNumber(updatedEmployee.getAadharNumber());
			employeeToUpdate.setFullName(updatedEmployee.getFullName());
			employeeToUpdate.setMobileNumber(updatedEmployee.getMobileNumber());
			employeeToUpdate.setAddress(updatedEmployee.getAddress());
			return (Employee) this.employeeRepository.save(employeeToUpdate);
		}
		throw new IllegalArgumentException("Employee not found");
	}

	public Employee deleteEmployeeDetails(Long id) {
		Optional<Employee> existingEmployee = this.employeeRepository.findById(id);
		if (existingEmployee.isPresent()) {
			Employee employeeToUpdate = existingEmployee.get();
			employeeToUpdate.setAadharNumber(null);
			employeeToUpdate.setFullName(null);
			employeeToUpdate.setMobileNumber(null);
			employeeToUpdate.setAddress(null);
			return (Employee) this.employeeRepository.save(employeeToUpdate);
		}
		throw new IllegalArgumentException("Employee not found");
	}

	public String processAndSaveDetails(Long employeeId) {
		if (this.employeeRepository == null || this.inscanService == null)
			throw new IllegalStateException("Employee repository or Inscan service not initialized");
		Optional<Employee> optionalEmployee = this.employeeRepository.findById(employeeId);
		if (optionalEmployee.isPresent()) {
			Employee employee = optionalEmployee.get();
			StringBuilder detailsBuilder = (new StringBuilder("EMP/HPNSK/")).append(employee.getId());
			String department = "Operation";
			String sub_department = "EMP";
			String details = "EMP/HPNSK/";
			Long ofcid = employee.getId();
			String name = employee.getFullName();
			String aadharNumber = employee.getAadharNumber();
			String mobile = employee.getMobileNumber();
			String address = employee.getAddress();
			String contractor = null;
			String firmName = null;

			Inscan inscan = this.inscanService.findByAadhar(aadharNumber);
			// Define the current day's time range
			LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
			LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

			// Count "IN" scans for the employee today
			long inScanCount = inscanService.countInScansByAadharAndDate(aadharNumber, startOfDay, endOfDay);

			if (inScanCount < 2) {
				// Less than 2 "IN" scans: save a new "IN" scan
				String mainGateStatus = inscan == null ? "Y" : "N";
				this.inscanService.saveDetailsToInscan(details, name, aadharNumber, mobile, address, ofcid, department,
						sub_department, mainGateStatus, contractor, firmName);
				return "Employee " + name + " Scan IN";
			} else {
				// At least 2 "IN" scans: check License Gate status and time limit for "OUT"
				Licensegate licensegate = this.licenseGateService.findByAadhar(aadharNumber);
				boolean isLicenseGateIn = false;
				if (licensegate == null || (licensegate != null && licensegate.getExitDateTime() != null))
					isLicenseGateIn = true;
				if (!isLicenseGateIn)
					return "Please exit from License gate";
				if (inscan != null && inscan.getExitDateTime() != null) {
					// Employee is already "OUT", save a new "IN"
					String mainGateStatus = "N";
					this.inscanService.saveDetailsToInscan(details, name, aadharNumber, mobile, address, ofcid,
							department, sub_department, mainGateStatus, contractor, firmName);
					return "Employee " + name + " Scan IN";
				} else if (inscan != null) {
					// Employee is "IN" and has 2+ "IN" scans, check time limit for "OUT"
					java.util.Date lastInDate = inscan.getEntryDateTime(); // Assuming this returns Date
					LocalDateTime lastInTime = lastInDate.toInstant().atZone(java.time.ZoneId.systemDefault())
							.toLocalDateTime();
					LocalDateTime currentTime = LocalDateTime.now();
					long hoursSinceLastIn = java.time.Duration.between(lastInTime, currentTime).toHours();
					long timeLimitHours = 8; // Define time limit (e.g., 8 hours)

					if (hoursSinceLastIn <= timeLimitHours) {
						// Within time limit, allow "OUT"
						this.inscanService.updateDetailsToInscan(inscan);
						return "Employee " + name + " Scan OUT";
					} else {
						// Exceeded time limit, force a new "IN"
						String mainGateStatus = "N";
						this.inscanService.saveDetailsToInscan(details, name, aadharNumber, mobile, address, ofcid,
								department, sub_department, mainGateStatus, contractor, firmName);
						return "Employee " + name + " Scan IN - Time limit exceeded";
					}
				} else {
					// Rare case: inScanCount >= 2 but inscan is null, force an "IN"
					this.inscanService.saveDetailsToInscan(details, name, aadharNumber, mobile, address, ofcid,
							department, sub_department, "N", contractor, firmName);
					return "Employee " + name + " Scan IN";
				}
			}
		}
		return "Employee not found";
	}

	public Employee getDetailsByAadharNumber(String aadharNumber) {
		return this.employeeRepository.findByAadharNumber(aadharNumber);
	}

	public String getFullName(Long entityId) {
		Employee employee = this.employeeRepository.findById(entityId).orElse(null);
		if (employee != null) {
			String fullName = employee.getFullName();
			return fullName;
		}
		return "Unknown Employee";
	}

	public String processAndSaveLicenseGateDetails(Long employeeId) {
		if (this.employeeRepository == null || this.licenseGateService == null)
			throw new IllegalStateException("Employee repository or LicenseGateService service not initialized");
		Optional<Employee> optionalEmployee = this.employeeRepository.findById(employeeId);
		if (optionalEmployee.isPresent()) {
			Employee employee = optionalEmployee.get();
			StringBuilder detailsBuilder = (new StringBuilder("EMP/HPNSK/")).append(employee.getId());
			String department = "Operation";
			String sub_department = "EMP";
			String details = detailsBuilder.toString();
			Long ofcid = employee.getId();
			String name = employee.getFullName();
			String aadharNumber = employee.getAadharNumber();
			String mobile = employee.getMobileNumber();
			String address = employee.getAddress();
			String contractor = null;
			String firmName = null;

			Licensegate licensegate = this.licenseGateService.findByAadhar(aadharNumber);
			if (licensegate == null) {
				this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, ofcid,
						department, sub_department, contractor, firmName);
				return "IN"; // First IN
			} else if (licensegate.getExitDateTime() == null) {
				// Already IN, force second IN or check time limit for OUT
				java.util.Date lastInDate = licensegate.getEntryDateTime();
				LocalDateTime lastInTime = lastInDate.toInstant().atZone(java.time.ZoneId.systemDefault())
						.toLocalDateTime();
				LocalDateTime currentTime = LocalDateTime.now();
				long hoursSinceLastIn = java.time.Duration.between(lastInTime, currentTime).toHours();
				long timeLimitHours = 8; // 8-hour limit
				if (hoursSinceLastIn < 1) { // Assuming second IN must happen within 1 hour of first
					this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address,
							ofcid, department, sub_department, contractor, firmName);
					return "Scan IN"; // Second IN
				} else if (hoursSinceLastIn <= timeLimitHours) {
					this.licenseGateService.updateDetailsToLicenseGate(licensegate);
					return "Scan Out"; // OUT after two INs and within time limit
				} else {
					this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address,
							ofcid, department, sub_department, contractor, firmName);
					return "Scan IN - Time limit exceeded";
				}
			} else {
				this.licenseGateService.saveDetailsToLicenseGate(details, name, aadharNumber, mobile, address, ofcid,
						department, sub_department, contractor, firmName);
				return "Scan IN"; // Reset to first IN after OUT
			}
		}
		throw new IllegalArgumentException("Employee not found");
	}
}
