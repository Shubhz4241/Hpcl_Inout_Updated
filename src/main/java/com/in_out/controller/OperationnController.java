package com.in_out.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.in_out.model.AadharNumberDetails;
import com.in_out.model.Contractor;
import com.in_out.model.Contractorworkman;
import com.in_out.model.Employee;
import com.in_out.model.Feg;
import com.in_out.model.Gat;
import com.in_out.model.Inscan;
import com.in_out.model.License;
import com.in_out.model.Mathadi;
import com.in_out.model.Officer;
import com.in_out.model.Sec;
import com.in_out.model.Tat;
import com.in_out.model.User;
import com.in_out.repo.UserRepository;
import com.in_out.service.AadharNumberDetailsService;
import com.in_out.service.ContractorService;
import com.in_out.service.ContractorworkmanService;
import com.in_out.service.DriverService;
import com.in_out.service.EmployeeService;
import com.in_out.service.FegService;
import com.in_out.service.GatService;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;
import com.in_out.service.MathadiService;
import com.in_out.service.OfficerService;
import com.in_out.service.OperationService;
import com.in_out.service.ProjectService;
import com.in_out.service.SecService;
import com.in_out.service.TatService;
import com.in_out.service.VisitorService;
import jakarta.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OperationnController {
	@Autowired
	private final OfficerService officerService;

	@Autowired
	private final EmployeeService employeeService;

	@Autowired
	private final ContractorService contractorService;

	@Autowired
	private final ContractorworkmanService contractorworkmanService;

	@Autowired
	private final MathadiService mathadiService;

	@Autowired
	private final GatService gatService;

	@Autowired
	private final TatService tatService;

	@Autowired
	private final FegService fegService;

	@Autowired
	private final SecService secService;

	@Autowired
	private OperationService operationService;

	@Autowired
	private DriverService driverService;

	@Autowired
	private AadharNumberDetailsService adharNumberDetailsService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private InscanService inscanService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private LicenseGateService LicensegateService;

	private static final Logger log = LoggerFactory.getLogger(com.in_out.controller.OperationnController.class);

	@Autowired
	private VisitorService visitorService;

	@Autowired
	public OperationnController(OfficerService officerService, EmployeeService employeeService,
			ContractorService contractorService, ContractorworkmanService contractorworkmanService,
			MathadiService mathadiService, GatService gatService, TatService tatService, FegService fegService,
			SecService secService) {
		this.officerService = officerService;
		this.employeeService = employeeService;
		this.contractorService = contractorService;
		this.contractorworkmanService = contractorworkmanService;
		this.mathadiService = mathadiService;
		this.gatService = gatService;
		this.tatService = tatService;
		this.fegService = fegService;
		this.secService = secService;
	}

	private void addUsernameAndRoleToModel(Model model, Principal principal) {
		String username = principal.getName();
		User user = this.userRepository.findByUserName(username);
		if (user != null) {
			String role = user.getRole();
			model.addAttribute("username", username);
			model.addAttribute("userRole", role);
		}
	}

	private void addLicenseInfoToModel(Model model) {
		User admin = (User) this.userRepository.getReferenceById(Integer.valueOf(1));
		License license = admin.getLicense();
		long remainingdays = ChronoUnit.DAYS.between(LocalDate.now(), license.getExpirydate());
		if (remainingdays == 0L) {
			model.addAttribute("remainingdays", Boolean.valueOf(false));
		} else if (remainingdays == 1L) {
			model.addAttribute("onedayremain", Boolean.valueOf(true));
		} else {
			model.addAttribute("remainingdays", Long.valueOf(remainingdays));
		}
	}

	@GetMapping({ "/" })
	public String dshbord(Model model) {
		log.info("In First Method");
		Long totalInscanRecordCount = this.inscanService.getCountOfInscanDetailsForCurrentDay();
		model.addAttribute("totalInscanRecordCount", totalInscanRecordCount);
		log.info("totalInscanRecordCount:" + totalInscanRecordCount);
		Long totalInscanRecordCountForOperation = this.inscanService.countByEntryDateTimeBetweenForOperation();
		model.addAttribute("totalInscanRecordCountForOperation", totalInscanRecordCountForOperation);
		Long totalInscanRecordCountForProject = this.inscanService.countByEntryDateTimeBetweenForProject();
		model.addAttribute("totalInscanRecordCountForProject", totalInscanRecordCountForProject);
		Long totalInscanRecordCountForVisitor = this.inscanService.countByEntryDateTimeBetweenForVisitor();
		model.addAttribute("totalInscanRecordCountForVisitor", totalInscanRecordCountForVisitor);
		Long totalLicenseGateRecordCountForOperation = this.LicensegateService
				.countByEntryDateTimeBetweenForOperationLicenseGate();
		model.addAttribute("totalLicenseGateRecordCountForOperation", totalLicenseGateRecordCountForOperation);
		Long totalLicenseGateRecordCount = this.LicensegateService.getCountOfLicensegateDetailsForCurrentDay();
		model.addAttribute("totalLicenseGateRecordCount", totalLicenseGateRecordCount);
		Long totalLicenseGateRecordCountForProject = this.LicensegateService
				.countByEntryDateTimeBetweenForProjectLicensegate();
		model.addAttribute("totalLicenseGateRecordCountForProject", totalLicenseGateRecordCountForProject);
		Long totalLicenseGateRecordCountForVisitor = this.LicensegateService
				.countByEntryDateTimeBetweenForVisitorLicensegate();
		model.addAttribute("totalLicenseGateRecordCountForVisitor", totalLicenseGateRecordCountForVisitor);
		Long totalOperationRecordCount = this.operationService.getOperationTotalRecordCount();
		model.addAttribute("totalOperationRecordCount", totalOperationRecordCount);
		Long totalDriverRecordCount = this.driverService.getDriverTotalRecordCount();
		model.addAttribute("totalDriverRecordCount", totalDriverRecordCount);
		Long totalProjectRecordCount = this.projectService.getProjectTotalRecordCount();
		model.addAttribute("totalProjectRecordCount", totalProjectRecordCount);
		Long totalVisitorRecordCount = Long.valueOf(this.visitorService.countVisitorsWithFullNameNotNull());
		model.addAttribute("totalVisitorRecordCount", totalVisitorRecordCount);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		addLicenseInfoToModel(model);
		return "dashbord";
	}

	@GetMapping({ "/officer" })
	public String getOperatorDetails(Model model) {
		List<Officer> operatorDetails = this.officerService.getAllOfficerDetails();
		model.addAttribute("operatorDetails", operatorDetails);
		List<Inscan> inscanDetailsForOperation = this.inscanService
				.findByEntryDateTimeBetweenOrderByDetailsForOperation();
		model.addAttribute("inscanDetailsForOperation", inscanDetailsForOperation);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		addLicenseInfoToModel(model);
		return "officer";
	}

	@GetMapping({ "/operatorDetails" })
	public String operatorDetails(@RequestParam("productId") Long productId, @RequestParam("action") String action,
			Model model) {
		Optional<Officer> officer = this.officerService.getOfficerById(productId);
		if (officer.isPresent()) {
			model.addAttribute("selectedProduct", officer.get());
			addLicenseInfoToModel(model);
			addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
			return "officerDetails";
		}
		return "operatorNotFound";
	}

	@PostMapping({ "/save" })
	public String saveOperator(@ModelAttribute Officer officer, RedirectAttributes redirectAttributes) {
		try {
			if (officer.getId() == null) {
				Officer createdOperator = this.officerService.addOfficer(officer);
				if (createdOperator != null) {
					redirectAttributes.addFlashAttribute("ScanSuccessful", "Officer Information Save successfully");

				} else {
					redirectAttributes.addFlashAttribute("UnknownEntityType",
							"An Officer with the same token number already exists.");
				}
			} else {
				Optional<Officer> officer_old_opt = this.officerService.getOfficerById(officer.getId());
				Officer officer_old = null;
				if (officer_old_opt.isPresent())
					officer_old = officer_old_opt.get();
				if (officer_old.getAadharNumber() == null || "".equals(officer_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(officer.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("UnknownEntityType", "Aadhar Already Exists.");
						System.out.println("Aadhar Already Exists: " + officer.getAadharNumber());
					} else {
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(officer.getAadharNumber());
						aadharNumberDetails2.setEntity("Officer " + officer.getId());
						aadharNumberDetails2.setFullName(officer.getFullName());
						aadharNumberDetails2.setMobileNumber(officer.getMobileNumber());
						aadharNumberDetails2.setAddress(officer.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Officer updatedOperator = this.officerService.updateOfficer(officer.getId(), officer);
						if (updatedOperator != null) {
							redirectAttributes.addFlashAttribute("ScanSuccessful",
									"Officer information update successfully");
						} else {
							redirectAttributes.addFlashAttribute("UnknownEntityType", "Failed to update the Officer.");
						}
					}
				} else if (!officer.getAadharNumber().equalsIgnoreCase(officer_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(officer.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("UnknownEntityType",
								"Aadhar already used by someone else..Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
								.getAadharNumberDetailsByAadharNumber(officer_old.getAadharNumber());
						this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(officer.getAadharNumber());
						aadharNumberDetails2.setEntity("Officer " + officer.getId());
						aadharNumberDetails2.setFullName(officer.getFullName());
						aadharNumberDetails2.setMobileNumber(officer.getMobileNumber());
						aadharNumberDetails2.setAddress(officer.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Officer updatedOperator = this.officerService.updateOfficer(officer.getId(), officer);
						if (updatedOperator != null) {
							redirectAttributes.addFlashAttribute("ScanSuccessful", "Officer Save successfully.");
						} else {
							redirectAttributes.addFlashAttribute("UnknownEntityType", "Failed to update the operator.");
						}
					}
				} else {
					Officer updatedOperator = this.officerService.updateOfficer(officer.getId(), officer);
					if (updatedOperator != null) {
						redirectAttributes.addFlashAttribute("ScanSuccessful", "Officer Save successfully.");
					} else {
						redirectAttributes.addFlashAttribute("UnknownEntityType", "Failed to update the operator.");
					}
				}
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("UnknownEntityType", "Error: " + e.getMessage());
		}
		return "redirect:/officer";
	}

	@GetMapping({ "/selectedProduct" })
	public String productview(@RequestParam("productId") long productId, Model model) {
		Optional<Officer> officer = this.officerService.getOfficerById(Long.valueOf(productId));
		if (officer.isPresent())
			model.addAttribute("selectedProduct", officer.get());
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "officer_id";
	}

	@GetMapping({ "/delete-Officer-details" })
	public String deleteOfficerDetails(@RequestParam("productId") Long productId,
			RedirectAttributes redirectAttributes) {
		try {
			Optional<Officer> ofccopy = this.officerService.getOfficerById(productId);
			String aadharno = null;
			if (ofccopy.isPresent())
				aadharno = ((Officer) ofccopy.get()).getAadharNumber();
			Officer updateofficer = this.officerService.deleteOfficerDetails(productId);
			if (updateofficer != null) {
				System.out.println("aadhar to delte " + aadharno);
				AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
						.getAadharNumberDetailsByAadharNumber(aadharno);
				this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());

			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete Officer details.");
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/officer";
	}

	@GetMapping({ "/generateQRCode" })
	public void generateQRCode(HttpServletResponse response, @RequestParam("data") String data) {
		int width = 300;
		int height = 300;
		String format = "png";
		Map<EncodeHintType, Object> hints = new HashMap<>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		try {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);
			int qrCodeWidth = bitMatrix.getWidth();
			int qrCodeHeight = bitMatrix.getHeight();
			BufferedImage qrCodeImage = new BufferedImage(qrCodeWidth, qrCodeHeight, 1);
			for (int x = 0; x < qrCodeWidth; x++) {
				for (int y = 0; y < qrCodeHeight; y++)
					qrCodeImage.setRGB(x, y, bitMatrix.get(x, y) ? -16777216 : -1);
			}
			response.setContentType("image/png");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(qrCodeImage, format, baos);
			response.getOutputStream().write(baos.toByteArray());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (WriterException | java.io.IOException e) {
			e.printStackTrace();
		}
	}

	@GetMapping({ "/employeecreate" })
	public String getEmployeeDetails(Model model) {
		List<Employee> employeeDetails = this.employeeService.getAllEmployeeDetails();
		model.addAttribute("employeeDetails", employeeDetails);
		List<Inscan> inscanDetailsForOperation = this.inscanService
				.findByEntryDateTimeBetweenOrderByDetailsForOperation();
		model.addAttribute("inscanDetailsForOperation", inscanDetailsForOperation);
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "employeecreate";
	}

	@GetMapping({ "/employeeDetails" })
	public String employeeDetails(@RequestParam("productId") Long productId, @RequestParam("action") String action,
			Model model) {
		Optional<Employee> employee = this.employeeService.getEmployeeById(productId);
		if (employee.isPresent()) {
			model.addAttribute("selectedProduct", employee.get());
			addLicenseInfoToModel(model);
			addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
			return "employeeDetails";
		}
		return "employeeNotFound";
	}

	@PostMapping({ "/employeesave" })
	public String saveEmployee(@ModelAttribute Employee employee, RedirectAttributes redirectAttributes) {
		try {
			if (employee.getId() == null) {
				Employee createdEmployee = this.employeeService.addEmployee(employee);
				if (createdEmployee != null) {
					redirectAttributes.addFlashAttribute("ScanSuccessful", "Employee Information Save successfully");
				} else {
					redirectAttributes.addFlashAttribute("UnknownEntityType",
							"An Employee with the same token number already exists.");
				}
			} else {
				Optional<Employee> employee_old_opt = this.employeeService.getEmployeeById(employee.getId());
				Employee employee_old = null;
				if (employee_old_opt.isPresent())
					employee_old = employee_old_opt.get();
				if (employee_old.getAadharNumber() == null || "".equals(employee_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(employee.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("UnknownEntityType", "Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(employee.getAadharNumber());
						aadharNumberDetails2.setEntity("Employee " + employee.getId());
						aadharNumberDetails2.setFullName(employee.getFullName());
						aadharNumberDetails2.setMobileNumber(employee.getMobileNumber());
						aadharNumberDetails2.setAddress(employee.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Employee updatedEmployee = this.employeeService.updateEmployee(employee.getId(), employee);
						if (updatedEmployee != null) {
							redirectAttributes.addFlashAttribute("ScanSuccessful",
									"Employee information update successfully"); // Updated
						} else {
							redirectAttributes.addFlashAttribute("UnknownEntityType", "Failed to update the Employee.");
						}
					}
				} else if (!employee.getAadharNumber().equalsIgnoreCase(employee_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(employee.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("UnknownEntityType",
								"Aadhar already used by someone else..Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
								.getAadharNumberDetailsByAadharNumber(employee_old.getAadharNumber());
						this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(employee.getAadharNumber());
						aadharNumberDetails2.setEntity("Employee " + employee.getId());
						aadharNumberDetails2.setFullName(employee.getFullName());
						aadharNumberDetails2.setMobileNumber(employee.getMobileNumber());
						aadharNumberDetails2.setAddress(employee.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Employee updatedEmployee = this.employeeService.updateEmployee(employee.getId(), employee);
						if (updatedEmployee != null) {
							redirectAttributes.addFlashAttribute("ScanSuccessful",
									"Employee information update successfully"); // Updated
						} else {
							redirectAttributes.addFlashAttribute("UnknownEntityType", "Failed to update the Employee.");
						}
					}
				} else {
					Employee updatedEmployee = this.employeeService.updateEmployee(employee.getId(), employee);
					if (updatedEmployee != null) {
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Employee information update successfully"); // Updated
					} else {
						redirectAttributes.addFlashAttribute("UnknownEntityType", "Failed to update the Employee.");
					}
				}
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("UnknownEntityType", "Error: " + e.getMessage());
		}
		return "redirect:/employeecreate";
	}

	@GetMapping({ "/selectedEmployee" })
	public String employeeview(@RequestParam("productId") long productId, Model model) {
		Optional<Employee> employee = this.employeeService.getEmployeeById(Long.valueOf(productId));
		if (employee.isPresent())
			model.addAttribute("selectedProduct", employee.get());
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "employee_id";
	}

	@GetMapping({ "/delete-Employee-details" })
	public String deleteEmployeeDetails(@RequestParam("productId") Long productId,
			RedirectAttributes redirectAttributes) {
		try {
			Optional<Employee> empcopy = this.employeeService.getEmployeeById(productId);
			String aadharno = null;
			if (empcopy.isPresent())
				aadharno = ((Employee) empcopy.get()).getAadharNumber();
			Employee updateemployee = this.employeeService.deleteEmployeeDetails(productId);
			if (updateemployee != null) {
				System.out.println("aadhar to delte " + aadharno);
				AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
						.getAadharNumberDetailsByAadharNumber(aadharno);
				this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
				// Removed: redirectAttributes.addFlashAttribute("successMessage", "Amc details
				// deleted successfully.");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete Amc details.");
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/employeecreate";
	}

	@GetMapping({ "/contractor" })
	public String getContractorDetails(Model model) {
		List<Contractor> contractorDetails = this.contractorService.getAllContractorDetails();
		model.addAttribute("contractorDetails", contractorDetails);
		List<Inscan> inscanDetailsForOperation = this.inscanService
				.findByEntryDateTimeBetweenOrderByDetailsForOperation();
		model.addAttribute("inscanDetailsForOperation", inscanDetailsForOperation);
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "contractor";
	}

	@GetMapping({ "/contractorDetails" })
	public String contractorDetails(@RequestParam("productId") Long productId, @RequestParam("action") String action,
			Model model) {
		Optional<Contractor> contractor = this.contractorService.getContractorById(productId);
		if (contractor.isPresent()) {
			model.addAttribute("selectedProduct", contractor.get());
			addLicenseInfoToModel(model);
			addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
			return "contractorDetails";
		}
		return "contractorNotFound";
	}

	@PostMapping({ "/contractorsave" })
	public String contractorsave(@ModelAttribute Contractor contractor, RedirectAttributes redirectAttributes) {
		try {
			if (contractor.getId() == null) {
				Contractor createdContractor = this.contractorService.addContractor(contractor);
				if (createdContractor != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Contractor saved successfully.");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage",
							"An Contractor with the same token number already exists.");
				}
			} else {
				Optional<Contractor> contractor_old_opt = this.contractorService.getContractorById(contractor.getId());
				Contractor contractor_old = null;
				if (contractor_old_opt.isPresent())
					contractor_old = contractor_old_opt.get();
				if (contractor_old.getAadharNumber() == null || "".equals(contractor_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(contractor.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("errorMessage", "Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(contractor.getAadharNumber());
						aadharNumberDetails2.setEntity("Contractor " + contractor.getId());
						aadharNumberDetails2.setFullName(contractor.getFullName());
						aadharNumberDetails2.setMobileNumber(contractor.getMobileNumber());
						aadharNumberDetails2.setAddress(contractor.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Contractor updatedContractor = this.contractorService.updateContractor(contractor.getId(),
								contractor);
						if (updatedContractor != null) {
							redirectAttributes.addFlashAttribute("successMessage", "Contractor Save successfully.");
						} else {
							redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Contractor.");
						}
					}
				} else if (!contractor.getAadharNumber().equalsIgnoreCase(contractor_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(contractor.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("errorMessage",
								"Aadhar already used by someone else..Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
								.getAadharNumberDetailsByAadharNumber(contractor_old.getAadharNumber());
						this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(contractor.getAadharNumber());
						aadharNumberDetails2.setEntity("Contractor " + contractor.getId());
						aadharNumberDetails2.setFullName(contractor.getFullName());
						aadharNumberDetails2.setMobileNumber(contractor.getMobileNumber());
						aadharNumberDetails2.setAddress(contractor.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Contractor updatedContractor = this.contractorService.updateContractor(contractor.getId(),
								contractor);
						if (updatedContractor != null) {
							redirectAttributes.addFlashAttribute("successMessage", "Contractor Save successfully.");
						} else {
							redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Contractor.");
						}
					}
				} else {
					Contractor updatedContractor = this.contractorService.updateContractor(contractor.getId(),
							contractor);
					if (updatedContractor != null) {
						redirectAttributes.addFlashAttribute("successMessage", "Contractor updated successfully.");
					} else {
						redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Contractor.");
					}
				}
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/contractor";
	}

	@GetMapping({ "/selectedContractor" })
	public String contractorview(@RequestParam("productId") long productId, Model model) {
		Optional<Contractor> contractor = this.contractorService.getContractorById(Long.valueOf(productId));
		if (contractor.isPresent())
			model.addAttribute("selectedProduct", contractor.get());
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "contractor_id";
	}

	@GetMapping({ "/delete-Contractor-details" })
	public String deleteContractorDetails(@RequestParam("productId") Long productId,
			RedirectAttributes redirectAttributes) {
		try {
			Optional<Contractor> concopy = this.contractorService.getContractorById(productId);
			String aadharno = null;
			if (concopy.isPresent())
				aadharno = ((Contractor) concopy.get()).getAadharNumber();
			Contractor updatecontractor = this.contractorService.deleteContractorDetails(productId);
			if (updatecontractor != null) {
				System.out.println("aadhar to delte " + aadharno);
				AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
						.getAadharNumberDetailsByAadharNumber(aadharno);
				this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
				redirectAttributes.addFlashAttribute("successMessage", "Contractor details deleted successfully.");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete Contractor details.");
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/contractor";
	}

	@GetMapping({ "/contractorworkman" })
	public String getContractorworkmanDetails(Model model) {
		List<Contractorworkman> contractorworkmanDetails = this.contractorworkmanService
				.getAllContractorworkmanDetails();
		model.addAttribute("contractorworkmanDetails", contractorworkmanDetails);
		List<Inscan> inscanDetailsForOperation = this.inscanService
				.findByEntryDateTimeBetweenOrderByDetailsForOperation();
		model.addAttribute("inscanDetailsForOperation", inscanDetailsForOperation);
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "contractorworkman";
	}

	@GetMapping({ "/contractorworkmanDetails" })
	public String contractorworkmanDetails(@RequestParam("productId") Long productId,
			@RequestParam("action") String action, Model model) {
		List<Contractor> contractorDetails = this.contractorService.getAllContractorDetails();
		model.addAttribute("contractorDetails", contractorDetails);
		Optional<Contractorworkman> contractorworkman = this.contractorworkmanService
				.getContractorworkmanById(productId);
		if (contractorworkman.isPresent()) {
			model.addAttribute("selectedProduct", contractorworkman.get());
			addLicenseInfoToModel(model);
			addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
			return "contractorworkmanDetails";
		}
		return "contractorNotFound";
	}
/*
	@PostMapping({ "/contractorworkmansave" })
	public String contractorworkmansave(@ModelAttribute Contractorworkman contractorworkman,
			RedirectAttributes redirectAttributes) {
		try {
			if (contractorworkman.getId() == null) {
				Contractorworkman createdContractorworkman = this.contractorworkmanService
						.addContractorworkman(contractorworkman);
				if (createdContractorworkman != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Contractorworkman saved successfully.");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage",
							"An Contractorworkman with the same token number already exists.");
				}
			} else {
				Optional<Contractorworkman> contractorworkman_old_opt = this.contractorworkmanService
						.getContractorworkmanById(contractorworkman.getId());
				Contractorworkman contractorworkman_old = null;
				if (contractorworkman_old_opt.isPresent())
					contractorworkman_old = contractorworkman_old_opt.get();
				if (contractorworkman_old.getAadharNumber() == null
						|| "".equals(contractorworkman_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(contractorworkman.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("errorMessage", "Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(contractorworkman.getAadharNumber());
						aadharNumberDetails2.setEntity("Contractorworkman " + contractorworkman.getId());
						aadharNumberDetails2.setFullName(contractorworkman.getFullName());
						aadharNumberDetails2.setMobileNumber(contractorworkman.getMobileNumber());
						aadharNumberDetails2.setAddress(contractorworkman.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Contractorworkman updatedContractorworkman = this.contractorworkmanService
								.updateContractorworkman(contractorworkman.getId(), contractorworkman);
						if (updatedContractorworkman != null) {
							redirectAttributes.addFlashAttribute("successMessage",
									"Contractorworkman Save successfully.");
						} else {
							redirectAttributes.addFlashAttribute("errorMessage",
									"Failed to update the Contractorworkman.");
						}
					}
				} else if (!contractorworkman.getAadharNumber()
						.equalsIgnoreCase(contractorworkman_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(contractorworkman.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("errorMessage",
								"Aadhar already used by someone else..Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
								.getAadharNumberDetailsByAadharNumber(contractorworkman_old.getAadharNumber());
						this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(contractorworkman.getAadharNumber());
						aadharNumberDetails2.setEntity("Contractorworkman " + contractorworkman.getId());
						aadharNumberDetails2.setFullName(contractorworkman.getFullName());
						aadharNumberDetails2.setMobileNumber(contractorworkman.getMobileNumber());
						aadharNumberDetails2.setAddress(contractorworkman.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Contractorworkman updatedContractorworkman = this.contractorworkmanService
								.updateContractorworkman(contractorworkman.getId(), contractorworkman);
						if (updatedContractorworkman != null) {
							redirectAttributes.addFlashAttribute("successMessage",
									"Contractorworkman save successfully.");
						} else {
							redirectAttributes.addFlashAttribute("errorMessage",
									"Failed to update the Contractorworkman.");
						}
					}
				} else {
					Contractorworkman updatedContractorworkman = this.contractorworkmanService
							.updateContractorworkman(contractorworkman.getId(), contractorworkman);
					if (updatedContractorworkman != null) {
						redirectAttributes.addFlashAttribute("successMessage",
								"Contractorworkman updated successfully.");
					} else {
						redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Contractorworkman.");
					}
				}
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/contractorworkman";
	}
*/
	
	@PostMapping({ "/contractorworkmansave" })
	public String contractorworkmansave(@ModelAttribute Contractorworkman contractorworkman,
	        RedirectAttributes redirectAttributes) {
	    try {
	        if (contractorworkman.getId() == null) {
	            Contractorworkman createdContractorworkman = this.contractorworkmanService
	                    .addContractorworkman(contractorworkman);
	            if (createdContractorworkman != null) {
	                redirectAttributes.addFlashAttribute("successMessage", "Contractorworkman saved successfully.");
	            } else {
	                redirectAttributes.addFlashAttribute("errorMessage",
	                        "An Contractorworkman with the same token number already exists.");
	            }
	        } else {
	            Optional<Contractorworkman> contractorworkman_old_opt = this.contractorworkmanService
	                    .getContractorworkmanById(contractorworkman.getId());
	            Contractorworkman contractorworkman_old = null;
	            if (contractorworkman_old_opt.isPresent()) {
	                contractorworkman_old = contractorworkman_old_opt.get();
	            }
	            // Check if contractorworkman_old is null
	            if (contractorworkman_old == null) {
	                redirectAttributes.addFlashAttribute("errorMessage",
	                        "Contractorworkman not found for ID: " + contractorworkman.getId());
	                return "redirect:/contractorworkman";
	            }
	            if (contractorworkman_old.getAadharNumber() == null
	                    || "".equals(contractorworkman_old.getAadharNumber())) {
	                AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
	                        .getAadharNumberDetailsByAadharNumber(contractorworkman.getAadharNumber());
	                if (aadharNumberDetails != null) {
	                    redirectAttributes.addFlashAttribute("errorMessage", "Aadhar Already Exists.");
	                } else {
	                    AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
	                    aadharNumberDetails2.setAadharNumber(contractorworkman.getAadharNumber());
	                    aadharNumberDetails2.setEntity("Contractorworkman " + contractorworkman.getId());
	                    aadharNumberDetails2.setFullName(contractorworkman.getFullName());
	                    aadharNumberDetails2.setMobileNumber(contractorworkman.getMobileNumber());
	                    aadharNumberDetails2.setAddress(contractorworkman.getAddress());
	                    this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
	                    Contractorworkman updatedContractorworkman = this.contractorworkmanService
	                            .updateContractorworkman(contractorworkman.getId(), contractorworkman);
	                    if (updatedContractorworkman != null) {
	                        redirectAttributes.addFlashAttribute("successMessage",
	                                "Contractorworkman Save successfully.");
	                    } else {
	                        redirectAttributes.addFlashAttribute("errorMessage",
	                                "Failed to update the Contractorworkman.");
	                    }
	                }
	            } else if (!contractorworkman.getAadharNumber()
	                    .equalsIgnoreCase(contractorworkman_old.getAadharNumber())) {
	                AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
	                        .getAadharNumberDetailsByAadharNumber(contractorworkman.getAadharNumber());
	                if (aadharNumberDetails != null) {
	                    redirectAttributes.addFlashAttribute("errorMessage",
	                            "Aadhar already used by someone else..Aadhar Already Exists.");
	                } else {
	                    AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
	                            .getAadharNumberDetailsByAadharNumber(contractorworkman_old.getAadharNumber());
	                    this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
	                    AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
	                    aadharNumberDetails2.setAadharNumber(contractorworkman.getAadharNumber());
	                    aadharNumberDetails2.setEntity("Contractorworkman " + contractorworkman.getId());
	                    aadharNumberDetails2.setFullName(contractorworkman.getFullName());
	                    aadharNumberDetails2.setMobileNumber(contractorworkman.getMobileNumber());
	                    aadharNumberDetails2.setAddress(contractorworkman.getAddress());
	                    this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
	                    Contractorworkman updatedContractorworkman = this.contractorworkmanService
	                            .updateContractorworkman(contractorworkman.getId(), contractorworkman);
	                    if (updatedContractorworkman != null) {
	                        redirectAttributes.addFlashAttribute("successMessage",
	                                "Contractorworkman save successfully.");
	                    } else {
	                        redirectAttributes.addFlashAttribute("errorMessage",
	                                "Failed to update the Contractorworkman.");
	                    }
	                }
	            } else {
	                Contractorworkman updatedContractorworkman = this.contractorworkmanService
	                        .updateContractorworkman(contractorworkman.getId(), contractorworkman);
	                if (updatedContractorworkman != null) {
	                    redirectAttributes.addFlashAttribute("successMessage",
	                            "Contractorworkman updated successfully.");
	                } else {
	                    redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Contractorworkman.");
	                }
	            }
	        }
	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
	    }
	    return "redirect:/contractorworkman";
	}
	@GetMapping({ "/selectedContractorworkman" })
	public String contractorworkmanview(@RequestParam("productId") long productId, Model model) {
		Optional<Contractorworkman> contractorworkman = this.contractorworkmanService
				.getContractorworkmanById(Long.valueOf(productId));
		if (contractorworkman.isPresent())
			model.addAttribute("selectedProduct", contractorworkman.get());
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "contractorworkman_id";
	}

	@GetMapping({ "/delete-Contractorworkman-details" })
	public String deleteContractorworkmanDetails(@RequestParam("productId") Long productId,
			RedirectAttributes redirectAttributes) {
		try {
			Optional<Contractorworkman> conwcopy = this.contractorworkmanService.getContractorworkmanById(productId);
			String aadharno = null;
			if (conwcopy.isPresent())
				aadharno = ((Contractorworkman) conwcopy.get()).getAadharNumber();
			Contractorworkman updatecontractorworkman = this.contractorworkmanService
					.deleteContractorworkmanDetails(productId);
			if (updatecontractorworkman != null) {
				System.out.println("aadhar to delte " + aadharno);
				AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
						.getAadharNumberDetailsByAadharNumber(aadharno);
				this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
				redirectAttributes.addFlashAttribute("successMessage",
						"Contractorworkman details deleted successfully.");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete Contractorworkman details.");
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/contractorworkman";
	}

	// Mathadi
	@GetMapping({ "/mathadi" })
	public String getMathadiDetails(Model model) {

		List<Mathadi> mathadiDetails = this.mathadiService.getAllMathadiDetails();
		model.addAttribute("mathadiDetails", mathadiDetails); // Fixed typo

		List<Inscan> inscanDetailsForOperation = this.inscanService
				.findByEntryDateTimeBetweenOrderByDetailsForOperation();
		model.addAttribute("inscanDetailsForOperation", inscanDetailsForOperation);

		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());

		return "mathadi";
	}

	@GetMapping("/mathadiDetails")
	public String mathadiDetails(@RequestParam("productId") Long productId, @RequestParam("action") String action,
			Model model) {
		// Fetch all Mathadi details
		List<Mathadi> mathadiDetails = mathadiService.getAllMathadiDetails();
		model.addAttribute("mathadiDetails", mathadiDetails);

		// Fetch all Contractor details
		List<Contractor> contractorDetails = this.contractorService.getAllContractorDetails();
		model.addAttribute("contractorDetails", contractorDetails);

		// Fetch the selected Mathadi by ID
		Optional<Mathadi> mathadi = mathadiService.getMathadiById(productId);
		if (mathadi.isPresent()) {
			model.addAttribute("selectedMathadi", mathadi.get());
			model.addAttribute("action", action); // To differentiate Add/Edit

			// Add common attributes
			addLicenseInfoToModel(model);
			addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());

			return "mathadiDetails";
		}

		// Redirect to error page if Mathadi not found
		return "mathadiNotFound";
	}

	@PostMapping({ "/mathadisave" })
	public String mathadisave(@ModelAttribute Mathadi mathadi, RedirectAttributes redirectAttributes) {
		try {
			if (mathadi.getId() == null) {
				Mathadi createdMathadi = this.mathadiService.addMathadi(mathadi);
				if (createdMathadi != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Mathadi saved successfully.");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage",
							"An Mathadi with the same token number already exists.");
				}
			} else {
				Optional<Mathadi> mathadi_old_opt = this.mathadiService.getMathadiById(mathadi.getId());
				Mathadi mathadi_old = null;
				if (mathadi_old_opt.isPresent())
					mathadi_old = mathadi_old_opt.get();
				if (mathadi_old.getAadharNumber() == null || "".equals(mathadi_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(mathadi.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("errorMessage", "Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(mathadi.getAadharNumber());
						aadharNumberDetails2.setEntity("Mathadi " + mathadi.getId());
						aadharNumberDetails2.setFullName(mathadi.getFullName());
						aadharNumberDetails2.setMobileNumber(mathadi.getMobileNumber());
						aadharNumberDetails2.setAddress(mathadi.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Mathadi updatedMathadi = this.mathadiService.updateMathadi(mathadi.getId(), mathadi);
						if (updatedMathadi != null) {
							redirectAttributes.addFlashAttribute("successMessage",
									"Contractorworkman Save successfully.");
						} else {
							redirectAttributes.addFlashAttribute("errorMessage",
									"Failed to update the Contractorworkman.");
						}
					}
				} else if (!mathadi.getAadharNumber().equalsIgnoreCase(mathadi_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(mathadi.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("errorMessage",
								"Aadhar already used by someone else..Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
								.getAadharNumberDetailsByAadharNumber(mathadi_old.getAadharNumber());
						this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(mathadi.getAadharNumber());
						aadharNumberDetails2.setEntity("Mathadi " + mathadi.getId());
						aadharNumberDetails2.setFullName(mathadi.getFullName());
						aadharNumberDetails2.setMobileNumber(mathadi.getMobileNumber());
						aadharNumberDetails2.setAddress(mathadi.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Mathadi updatedMathadi = this.mathadiService.updateMathadi(mathadi.getId(), mathadi);
						if (updatedMathadi != null) {
							redirectAttributes.addFlashAttribute("successMessage", "Mathadi save successfully.");
						} else {
							redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Mathadi.");
						}
					}
				} else {
					Mathadi updatedMathadi = this.mathadiService.updateMathadi(mathadi.getId(), mathadi);
					if (updatedMathadi != null) {
						redirectAttributes.addFlashAttribute("successMessage", "Mathadi updated successfully.");
					} else {
						redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Mathadi.");
					}
				}
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/mathadi";
	}

	@GetMapping("/selectedMathadi")
	public String mathadi(@RequestParam("id") long id, Model model) {
		Optional<Mathadi> mathadi = mathadiService.getMathadiById(id);
		mathadi.ifPresent(value -> model.addAttribute("mathadi", value));

		model.addAttribute("selectedProduct", mathadi.get());

		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());

		return "mathadi_id";
	}

	@GetMapping("/delete-Mathadi-details")
	public String deleteMathadi(@RequestParam("id") Long id, RedirectAttributes redirectAttributes) {
		try {
			Optional<Mathadi> mathadiOpt = mathadiService.getMathadiById(id);
			if (mathadiOpt.isPresent()) {
				mathadiService.deleteMathadiDetails(id);
				// aadharNumberDetailsService.deleteAadharNumberDetailsByAadhar(mathadiOpt.get().getAadharNumber());
				redirectAttributes.addFlashAttribute("successMessage", "Mathadi details deleted successfully.");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Mathadi not found.");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/mathadi";
	}

	// Gate
	@GetMapping({ "/gat" })
	public String getGatDetails(Model model) {
		List<Gat> gatDetails = this.gatService.getAllGatDetails();
		model.addAttribute("gatDetails", gatDetails);
		List<Inscan> inscanDetailsForOperation = this.inscanService
				.findByEntryDateTimeBetweenOrderByDetailsForOperation();
		model.addAttribute("inscanDetailsForOperation", inscanDetailsForOperation);
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "gat";
	}

	@GetMapping({ "/gatDetails" })
	public String GatDetails(@RequestParam("productId") Long productId, @RequestParam("action") String action,
			Model model) {
		Optional<Gat> gat = this.gatService.getGatById(productId);
		if (gat.isPresent()) {
			model.addAttribute("selectedProduct", gat.get());
			addLicenseInfoToModel(model);
			addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
			return "gatDetails";
		}
		return "contractorNotFound";
	}

	@PostMapping({ "/gatsave" })
	public String gatsave(@ModelAttribute Gat gat, RedirectAttributes redirectAttributes) {
		try {
			if (gat.getId() == null) {
				Gat createdGat = this.gatService.addGat(gat);
				if (createdGat != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Gat Information Save successfully");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage",
							"An Gat with the same token number already exists.");
				}
			} else {
				Optional<Gat> gat_old_opt = this.gatService.getGatById(gat.getId());
				Gat gat_old = null;
				if (gat_old_opt.isPresent())
					gat_old = gat_old_opt.get();
				if (gat_old.getAadharNumber() == null || "".equals(gat_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(gat.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("errorMessage", "Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(gat.getAadharNumber());
						aadharNumberDetails2.setEntity("Gat " + gat.getId());
						aadharNumberDetails2.setFullName(gat.getFullName());
						aadharNumberDetails2.setMobileNumber(gat.getMobileNumber());
						aadharNumberDetails2.setAddress(gat.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Gat updatedGat = this.gatService.updateGat(gat.getId(), gat);
						if (updatedGat != null) {
							redirectAttributes.addFlashAttribute("successMessage",
									"Gat information update successfully"); // Updated message
						} else {
							redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Gat.");
						}
					}
				} else if (!gat.getAadharNumber().equalsIgnoreCase(gat_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(gat.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("errorMessage",
								"Aadhar already used by someone else..Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
								.getAadharNumberDetailsByAadharNumber(gat_old.getAadharNumber());
						this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(gat.getAadharNumber());
						aadharNumberDetails2.setEntity("Gat " + gat.getId());
						aadharNumberDetails2.setFullName(gat.getFullName());
						aadharNumberDetails2.setMobileNumber(gat.getMobileNumber());
						aadharNumberDetails2.setAddress(gat.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Gat updatedGat = this.gatService.updateGat(gat.getId(), gat);
						if (updatedGat != null) {
							redirectAttributes.addFlashAttribute("successMessage",
									"Gat information update successfully"); // Updated message
						} else {
							redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Gat.");
						}
					}
				} else {
					Gat updatedGat = this.gatService.updateGat(gat.getId(), gat);
					if (updatedGat != null) {
						redirectAttributes.addFlashAttribute("successMessage", "Gat information update successfully"); // Updated
																														// message
					} else {
						redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Gat.");
					}
				}
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/gat";
	}

	@GetMapping({ "/selectedGat" })
	public String gat(@RequestParam("productId") long productId, Model model) {
		Optional<Gat> gat = this.gatService.getGatById(Long.valueOf(productId));
		if (gat.isPresent())
			model.addAttribute("selectedProduct", gat.get());
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "gat_id";
	}

	@GetMapping({ "/delete-Gat-details" })
	public String deleteGatDetails(@RequestParam("productId") Long productId, RedirectAttributes redirectAttributes) {
		try {
			Optional<Gat> gatcopy = this.gatService.getGatById(productId);
			String aadharno = null;
			if (gatcopy.isPresent())
				aadharno = ((Gat) gatcopy.get()).getAadharNumber();
			Gat updategat = this.gatService.deleteGatDetails(productId);
			if (updategat != null) {
				System.out.println("aadhar to delte " + aadharno);
				AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
						.getAadharNumberDetailsByAadharNumber(aadharno);
				this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
				redirectAttributes.addFlashAttribute("successMessage", "Gat details deleted successfully.");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete Gat details.");
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/gat";
	}

	@GetMapping({ "/tat" })
	public String getTatDetails(Model model) {
		List<Tat> tatDetails = this.tatService.getAllTatDetails();
		model.addAttribute("tatDetails", tatDetails);
		List<Inscan> inscanDetailsForOperation = this.inscanService
				.findByEntryDateTimeBetweenOrderByDetailsForOperation();
		model.addAttribute("inscanDetailsForOperation", inscanDetailsForOperation);
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "tat";
	}

	@GetMapping({ "/tatDetails" })
	public String getGatDetails(@RequestParam("productId") Long productId, @RequestParam("action") String action,
			Model model) {
		Optional<Tat> tat = this.tatService.getTatById(productId);
		if (tat.isPresent()) {
			model.addAttribute("selectedProduct", tat.get());
			addLicenseInfoToModel(model);
			addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
			return "tatDetails";
		}
		return "contractorNotFound";
	}

	@PostMapping({ "/tatsave" })
	public String tatsave(@ModelAttribute Tat tat, RedirectAttributes redirectAttributes) {
		try {
			if (tat.getId() == null) {
				Tat createdTat = this.tatService.addTat(tat);
				if (createdTat != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Tat saved successfully.");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage",
							"An Tat with the same token number already exists.");
				}
			} else {
				Optional<Tat> tat_old_opt = this.tatService.getTatById(tat.getId());
				Tat tat_old = null;
				if (tat_old_opt.isPresent())
					tat_old = tat_old_opt.get();
				if (tat_old.getAadharNumber() == null || "".equals(tat_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(tat.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("errorMessage", "Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(tat.getAadharNumber());
						aadharNumberDetails2.setEntity("Tat " + tat.getId());
						aadharNumberDetails2.setFullName(tat.getFullName());
						aadharNumberDetails2.setMobileNumber(tat.getMobileNumber());
						aadharNumberDetails2.setAddress(tat.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Tat updatedTat = this.tatService.updateTat(tat.getId(), tat);
						if (updatedTat != null) {
							redirectAttributes.addFlashAttribute("successMessage", "Tat Save successfully.");
						} else {
							redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Tat.");
						}
					}
				} else if (!tat.getAadharNumber().equalsIgnoreCase(tat_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(tat.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("errorMessage",
								"Aadhar already used by someone else..Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
								.getAadharNumberDetailsByAadharNumber(tat_old.getAadharNumber());
						this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(tat.getAadharNumber());
						aadharNumberDetails2.setEntity("Tat " + tat.getId());
						aadharNumberDetails2.setFullName(tat.getFullName());
						aadharNumberDetails2.setMobileNumber(tat.getMobileNumber());
						aadharNumberDetails2.setAddress(tat.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Tat updatedTat = this.tatService.updateTat(tat.getId(), tat);
						if (updatedTat != null) {
							redirectAttributes.addFlashAttribute("successMessage", "Tat Save successfully.");
						} else {
							redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Tat.");
						}
					}
				} else {
					Tat updatedTat = this.tatService.updateTat(tat.getId(), tat);
					if (updatedTat != null) {
						redirectAttributes.addFlashAttribute("successMessage", "Tat updated successfully.");
					} else {
						redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Tat.");
					}
				}
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/tat";
	}

	@GetMapping({ "/selectedTat" })
	public String tat(@RequestParam("productId") long productId, Model model) {
		Optional<Tat> tat = this.tatService.getTatById(Long.valueOf(productId));
		if (tat.isPresent())
			model.addAttribute("selectedProduct", tat.get());
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "tat_id";
	}

	@GetMapping({ "/delete-Tat-details" })
	public String deleteTatDetails(@RequestParam("productId") Long productId, RedirectAttributes redirectAttributes) {
		try {
			Optional<Tat> tatcopy = this.tatService.getTatById(productId);
			String aadharno = null;
			if (tatcopy.isPresent())
				aadharno = ((Tat) tatcopy.get()).getAadharNumber();
			Tat updatetat = this.tatService.deleteTatDetails(productId);
			if (updatetat != null) {
				System.out.println("aadhar to delte " + aadharno);
				AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
						.getAadharNumberDetailsByAadharNumber(aadharno);
				this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
				redirectAttributes.addFlashAttribute("successMessage", "Tat details deleted successfully.");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete Tat details.");
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/tat";
	}

	@GetMapping({ "/feg" })
	public String getFegDetails(Model model) {
		List<Feg> fegDetails = this.fegService.getAllFegDetails();
		model.addAttribute("fegDetails", fegDetails);
		List<Inscan> inscanDetailsForOperation = this.inscanService
				.findByEntryDateTimeBetweenOrderByDetailsForOperation();
		model.addAttribute("inscanDetailsForOperation", inscanDetailsForOperation);
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "feg";
	}

	@GetMapping({ "/fegDetails" })
	public String getFegDetails(@RequestParam("productId") Long productId, @RequestParam("action") String action,
			Model model) {
		Optional<Feg> feg = this.fegService.getFegById(productId);
		if (feg.isPresent()) {
			model.addAttribute("selectedProduct", feg.get());
			addLicenseInfoToModel(model);
			addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
			return "fegDetails";
		}
		return "contractorNotFound";
	}

	@PostMapping({ "/fegsave" })
	public String fegsave(@ModelAttribute Feg feg, RedirectAttributes redirectAttributes) {
		try {
			if (feg.getId() == null) {
				Feg createdFeg = this.fegService.addFeg(feg);
				if (createdFeg != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Feg Information Save successfully"); // Fixed
																													// Defect
																													// 4
				} else {
					redirectAttributes.addFlashAttribute("errorMessage",
							"An Feg with the same token number already exists.");
				}
			} else {
				Optional<Feg> feg_old_opt = this.fegService.getFegById(feg.getId());
				Feg feg_old = null;
				if (feg_old_opt.isPresent())
					feg_old = feg_old_opt.get();
				if (feg_old.getAadharNumber() == null || "".equals(feg_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(feg.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("errorMessage", "Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(feg.getAadharNumber());
						aadharNumberDetails2.setEntity("Feg " + feg.getId());
						aadharNumberDetails2.setFullName(feg.getFullName());
						aadharNumberDetails2.setMobileNumber(feg.getMobileNumber());
						aadharNumberDetails2.setAddress(feg.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Feg updatedFeg = this.fegService.updateFeg(feg.getId(), feg);
						if (updatedFeg != null) {
							redirectAttributes.addFlashAttribute("successMessage",
									"Feg information update successfully"); // Fixed Defect 5
						} else {
							redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Feg.");
						}
					}
				} else if (!feg.getAadharNumber().equalsIgnoreCase(feg_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(feg.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("errorMessage",
								"Aadhar already used by someone else..Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
								.getAadharNumberDetailsByAadharNumber(feg_old.getAadharNumber());
						this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(feg.getAadharNumber());
						aadharNumberDetails2.setEntity("Feg " + feg.getId());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Feg updatedFeg = this.fegService.updateFeg(feg.getId(), feg);
						if (updatedFeg != null) {
							redirectAttributes.addFlashAttribute("successMessage",
									"Feg information update successfully"); // Fixed Defect 5
						} else {
							redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Feg.");
						}
					}
				} else {
					Feg updatedFeg = this.fegService.updateFeg(feg.getId(), feg);
					if (updatedFeg != null) {
						redirectAttributes.addFlashAttribute("successMessage", "Feg information update successfully"); // Fixed
																														// Defect
																														// 5
					} else {
						redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Feg.");
					}
				}
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/feg";
	}

	@GetMapping({ "/selectedFeg" })
	public String feg(@RequestParam("productId") long productId, Model model) {
		Optional<Feg> feg = this.fegService.getFegById(Long.valueOf(productId));
		if (feg.isPresent())
			model.addAttribute("selectedProduct", feg.get());
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "feg_id";
	}

	@GetMapping({ "/delete-Feg-details" })
	public String deleteFegDetails(@RequestParam("productId") Long productId, RedirectAttributes redirectAttributes) {
		try {
			Optional<Feg> fegcopy = this.fegService.getFegById(productId);
			String aadharno = null;
			if (fegcopy.isPresent())
				aadharno = ((Feg) fegcopy.get()).getAadharNumber();
			Feg updatefeg = this.fegService.deleteFegDetails(productId);
			if (updatefeg != null) {
				System.out.println("aadhar to delte " + aadharno);
				AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
						.getAadharNumberDetailsByAadharNumber(aadharno);
				this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
				redirectAttributes.addFlashAttribute("successMessage", "Feg details deleted successfully.");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete Feg details.");
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/feg";
	}

	@GetMapping({ "/sec" })
	public String getSecDetails(Model model) {
		List<Sec> secDetails = this.secService.getAllSecDetails();
		model.addAttribute("secDetails", secDetails);
		List<Inscan> inscanDetailsForOperation = this.inscanService
				.findByEntryDateTimeBetweenOrderByDetailsForOperation();
		model.addAttribute("inscanDetailsForOperation", inscanDetailsForOperation);
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "sec";
	}

	@GetMapping({ "/secDetails" })
	public String getSecDetails(@RequestParam("productId") Long productId, @RequestParam("action") String action,
			Model model) {
		Optional<Sec> sec = this.secService.getSecById(productId);
		if (sec.isPresent()) {
			 // Add contractor details to the model
	        List<Contractor> contractorDetails = this.contractorService.getAllContractorDetails(); // You'll need this service
	        model.addAttribute("contractorDetails", contractorDetails);
			model.addAttribute("selectedProduct", sec.get());
			addLicenseInfoToModel(model);
			addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
			return "secDetails";
		}
		return "SecNotFound";
	}

	@PostMapping({ "/secsave" })
	public String secsave(@ModelAttribute Sec sec, RedirectAttributes redirectAttributes) {
		try {
			if (sec.getId() == null) {
				Sec createdSec = this.secService.addSec(sec);
				if (createdSec != null) {
					redirectAttributes.addFlashAttribute("successMessage", "Sec Information Save successfully.");
				} else {
					redirectAttributes.addFlashAttribute("errorMessage",
							"An Sec with the same token number already exists.");
				}
			} else {
				Optional<Sec> sec_old_opt = this.secService.getSecById(sec.getId());
				Sec sec_old = null;
				if (sec_old_opt.isPresent())
					sec_old = sec_old_opt.get();
				if (sec_old.getAadharNumber() == null || "".equals(sec_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(sec.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("errorMessage", "Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(sec.getAadharNumber());
						aadharNumberDetails2.setEntity("Sec " + sec.getId());
						aadharNumberDetails2.setFullName(sec.getFullName());
						aadharNumberDetails2.setMobileNumber(sec.getMobileNumber());
						aadharNumberDetails2.setAddress(sec.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Sec updatedSec = this.secService.updateSec(sec.getId(), sec);
						if (updatedSec != null) {
							redirectAttributes.addFlashAttribute("successMessage",
									"Sec Information Save successfully.");
						} else {
							redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Sec.");
						}
					}
				} else if (!sec.getAadharNumber().equalsIgnoreCase(sec_old.getAadharNumber())) {
					AadharNumberDetails aadharNumberDetails = this.adharNumberDetailsService
							.getAadharNumberDetailsByAadharNumber(sec.getAadharNumber());
					if (aadharNumberDetails != null) {
						redirectAttributes.addFlashAttribute("errorMessage",
								"Aadhar already used by someone else..Aadhar Already Exists.");
					} else {
						AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
								.getAadharNumberDetailsByAadharNumber(sec_old.getAadharNumber());
						this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
						AadharNumberDetails aadharNumberDetails2 = new AadharNumberDetails();
						aadharNumberDetails2.setAadharNumber(sec.getAadharNumber());
						aadharNumberDetails2.setEntity("Sec " + sec.getId());
						aadharNumberDetails2.setFullName(sec.getFullName());
						aadharNumberDetails2.setMobileNumber(sec.getMobileNumber());
						aadharNumberDetails2.setAddress(sec.getAddress());
						this.adharNumberDetailsService.saveAadharNumberDetails(aadharNumberDetails2);
						Sec updatedSec = this.secService.updateSec(sec.getId(), sec);
						if (updatedSec != null) {
							redirectAttributes.addFlashAttribute("successMessage",
									"Sec Information Save successfully.");
						} else {
							redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Sec.");
						}
					}
				} else {
					Sec updatedSec = this.secService.updateSec(sec.getId(), sec);
					if (updatedSec != null) {
						redirectAttributes.addFlashAttribute("successMessage", "Sec Information Save successfully.");
					} else {
						redirectAttributes.addFlashAttribute("errorMessage", "Failed to update the Sec.");
					}
				}
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/sec";
	}

	@GetMapping({ "/selectedSec" })
	public String sec(@RequestParam("productId") long productId, Model model) {
		Optional<Sec> sec = this.secService.getSecById(Long.valueOf(productId));
		if (sec.isPresent())
			model.addAttribute("selectedProduct", sec.get());
		addLicenseInfoToModel(model);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		return "sec_id";
	}

	@GetMapping({ "/delete-Sec-details" })
	public String deletesecDetails(@RequestParam("productId") Long productId, RedirectAttributes redirectAttributes) {
		try {
			Optional<Sec> seccopy = this.secService.getSecById(productId);
			String aadharno = null;
			if (seccopy.isPresent())
				aadharno = ((Sec) seccopy.get()).getAadharNumber();
			Sec updatesec = this.secService.deleteSecDetails(productId);
			if (updatesec != null) {
				System.out.println("aadhar to delte " + aadharno);
				AadharNumberDetails aadharNumberDetails_delete = this.adharNumberDetailsService
						.getAadharNumberDetailsByAadharNumber(aadharno);
				this.adharNumberDetailsService.deleteAadharNumberDetails(aadharNumberDetails_delete.getId());
				redirectAttributes.addFlashAttribute("successMessage", "Sec details deleted successfully.");
			} else {
				redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete Sec details.");
			}
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		return "redirect:/sec";
	}
}