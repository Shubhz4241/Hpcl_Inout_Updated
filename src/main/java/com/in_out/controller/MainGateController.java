package com.in_out.controller;

import com.in_out.model.Inscan;
import com.in_out.model.License;
import com.in_out.model.User;
import com.in_out.repo.UserRepository;
import com.in_out.service.AmcService;
import com.in_out.service.BulkService;
import com.in_out.service.ContractorService;
import com.in_out.service.ContractorworkmanService;
import com.in_out.service.EmployeeService;
import com.in_out.service.FegService;
import com.in_out.service.GatService;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;
import com.in_out.service.MathadiService;
import com.in_out.service.OfficerService;
import com.in_out.service.PackedService;
import com.in_out.service.ProjectService;
import com.in_out.service.SecService;
import com.in_out.service.TatService;
import com.in_out.service.transporterService;
import com.in_out.service.VisitorService;
import com.in_out.service.WorkmanService;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainGateController {
	@Autowired
	private OfficerService officerService;

	@Autowired
	private ContractorService contractorService;

	@Autowired
	private EmployeeService emplloyeeService;

	@Autowired
	private GatService gatService;

	@Autowired
	private TatService tatService;

	@Autowired
	private FegService fegService;

	@Autowired
	private SecService secService;

	@Autowired
	private ContractorworkmanService contractorWorkmanService;

	@Autowired
	private MathadiService mathadiService;

	@Autowired
	private PackedService packedService;

	@Autowired
	private BulkService bulkService;

	@Autowired
	private transporterService transporterService;

	@Autowired
	private WorkmanService workmanService;

	@Autowired
	private AmcService amcService;

	@Autowired
	private InscanService inscanService;

	@Autowired
	private VisitorService visitorService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private LicenseGateService LicensegateService;

	@Autowired
	private UserRepository userRepository;

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

	@GetMapping({ "/mainGate" })
	public String dshbord(Model model) {
		Long totalInscanRecordCount = this.inscanService.getCountOfInscanDetailsForCurrentDay();
		model.addAttribute("totalInscanRecordCount", totalInscanRecordCount);
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
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		addLicenseInfoToModel(model);
		return "mainGate";
	}

	@GetMapping({ "/maingateOperation" })
	public String getInfoMoreI(Model model) {
		List<Inscan> inscanDetailsForOperation = this.inscanService
				.findByEntryDateTimeBetweenOrderByDetailsForOperation();
		model.addAttribute("inscanDetailsForOperation", inscanDetailsForOperation);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		addLicenseInfoToModel(model);
		return "maingateOperation";
	}

	@GetMapping({ "/maingateDriver" })
	public String getMaingateDriver(Model model) {
		List<Inscan> operatorTotalDetails = this.inscanService.getAllInscanDetailsForCurrentDay();
		model.addAttribute("operatorTotalDetails", operatorTotalDetails);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		addLicenseInfoToModel(model);
		return "maingateDriver";
	}

	@GetMapping({ "/maingeteProject" })
	public String getMaingateProject(Model model) {
		List<Inscan> inscanDetailsForProject = this.inscanService.findByEntryDateTimeBetweenOrderByDetailsForProject();
		model.addAttribute("inscanDetailsForProject", inscanDetailsForProject);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		addLicenseInfoToModel(model);
		return "maingeteProject";
	}

	@GetMapping({ "/maingeteVisitor" })
	public String getMaingateVisitor(Model model) {
		List<Inscan> inscanDetailsForVisitor = this.inscanService.findByEntryDateTimeBetweenOrderByDetailsForVisitor();
		model.addAttribute("inscanDetailsForVisitor", inscanDetailsForVisitor);
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		addLicenseInfoToModel(model);
		return "maingateVisitor";
	}

	@GetMapping({ "/totalOperation" })
	public String getTotalOperation(Model model) {
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		addLicenseInfoToModel(model);
		return "totalOperation";
	}

	@GetMapping({ "/licenseGate" })
	public String dshbord1(Model model) {
		Long totalInscanRecordCount = this.inscanService.getCountOfInscanDetailsForCurrentDay();
		model.addAttribute("totalInscanRecordCount", totalInscanRecordCount);
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
		addUsernameAndRoleToModel(model, (Principal) SecurityContextHolder.getContext().getAuthentication());
		addLicenseInfoToModel(model);
		return "licenseGate";
	}

	@PostMapping({ "/maingatein" })
	public String processForm(@RequestParam String inputValue, RedirectAttributes redirectAttributes) {
		System.out.println("Input value: " + inputValue);
		String inoutFlag = "";
		String[] parts = inputValue.split("/");
		if (parts.length == 3) {
			String entityType = parts[0];
			String category = parts[1];
			String idStr = parts[2];
			if ("HPNSK".equals(category))
				try {
					boolean restrictstatus;
					Long entityId = Long.valueOf(Long.parseLong(idStr));
					String fullName = null;
					switch (entityType) {
					case "OFC":
						fullName = this.officerService.getFullName(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						inoutFlag = this.officerService.processAndSaveDetails(entityId);
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Officer " + fullName + " Scan " + inoutFlag);
						return "redirect:/mainGate";
					case "EMP":
						fullName = this.emplloyeeService.getFullName(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						inoutFlag = this.emplloyeeService.processAndSaveDetails(entityId);
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Employee " + fullName + " Scan " + inoutFlag);
						return "redirect:/mainGate";
					case "GAT":
						fullName = this.gatService.getFullName(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						inoutFlag = this.gatService.processAndSaveDetails(entityId);
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Officer " + fullName + " Scan " + inoutFlag);
						System.out.println("inoutFlag " + inoutFlag);
						return "redirect:/mainGate";
					case "TAT":
						fullName = this.tatService.getFullName(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						inoutFlag = this.tatService.processAndSaveDetails(entityId);
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Employee " + fullName + " Scan " + inoutFlag);
						System.out.println("inoutFlag " + inoutFlag);
						return "redirect:/mainGate";
					case "FEG":
						fullName = this.fegService.getFullName(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						inoutFlag = this.fegService.processAndSaveDetails(entityId);
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Gat " + fullName + " Scan " + inoutFlag);
						System.out.println("inoutFlag " + inoutFlag);
						return "redirect:/mainGate";
					case "SEC":
						fullName = this.secService.getFullName(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						inoutFlag = this.secService.processAndSaveDetails(entityId);
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Tat " + fullName + " Scan " + inoutFlag);
						System.out.println("inoutFlag " + inoutFlag);
						return "redirect:/mainGate";
					case "CON":
						fullName = this.contractorService.getFullName(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						inoutFlag = this.contractorService.processAndSaveDetails(entityId);
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Feg " + fullName + " Scan " + inoutFlag);
						System.out.println("inoutFlag " + inoutFlag);
						return "redirect:/mainGate";
					case "CONW":
						fullName = this.contractorWorkmanService.getFullName(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						inoutFlag = this.contractorWorkmanService.processAndSaveDetails(entityId);
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Sec " + fullName + " Scan " + inoutFlag);
						System.out.println("inoutFlag " + inoutFlag);
						return "redirect:/mainGate";

					case "MT":
						fullName = this.mathadiService.getFullName(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						inoutFlag = this.mathadiService.processAndSaveDetails(entityId);
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Contractor " + fullName + " Scan " + inoutFlag);
						System.out.println("inoutFlag " + inoutFlag);
						return "redirect:/mainGate";

					case "PT":
						fullName = this.packedService.getFullName(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						inoutFlag = this.packedService.processAndSaveDetails(entityId);
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Contractor Workman " + fullName + " Scan " + inoutFlag);
						System.out.println("inoutFlag " + inoutFlag);
						return "redirect:/mainGate";
					case "BK":
						fullName = this.bulkService.getFullName(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						inoutFlag = this.bulkService.processAndSaveDetails(entityId);
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Mathadi " + fullName + " Scan " + inoutFlag);
						System.out.println("inoutFlag " + inoutFlag);
						return "redirect:/mainGate";
					case "TR":
						fullName = this.transporterService.getFullName(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						inoutFlag = this.transporterService.processAndSaveDetails(entityId);
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Packed " + fullName + " Scan " + inoutFlag);
						System.out.println("inoutFlag " + inoutFlag);
						return "redirect:/mainGate";
					case "PW":
						fullName = this.workmanService.getFullName(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						inoutFlag = this.workmanService.processAndSaveDetails(entityId);
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Bulk " + fullName + " Scan " + inoutFlag);
						System.out.println("inoutFlag " + inoutFlag);
						return "redirect:/mainGate";
					case "AMC":
						fullName = this.amcService.getFullName(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						inoutFlag = this.amcService.processAndSaveDetails(entityId);
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Transporter " + fullName + " Scan " + inoutFlag);
						System.out.println("inoutFlag " + inoutFlag);
						return "redirect:/mainGate";
					case "VS":
						fullName = this.visitorService.getFullName(entityId);
						restrictstatus = this.visitorService.restrictornot(entityId);
						if (fullName == null || fullName.trim().isEmpty()) {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"Invalid data for " + entityType + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						if (!restrictstatus) {
							inoutFlag = this.visitorService.processAndSaveDetails(entityId);
						} else {
							redirectAttributes.addFlashAttribute("UnknownEntityType",
									"User is restricted." + fullName + " with ID " + entityId);
							return "redirect:/mainGate";
						}
						redirectAttributes.addFlashAttribute("ScanSuccessful",
								"Workman " + fullName + " Scan " + inoutFlag);
						System.out.println("inoutFlag " + inoutFlag);
						return "redirect:/mainGate";
					}
					System.out.println("Unknown entity type: " + entityType);
					redirectAttributes.addFlashAttribute("UnknownEntityType", "UnknownEntityType" + inputValue);
					return "redirect:/mainGate";
				} catch (NumberFormatException e) {
					System.out.println("Invalid ID format: " + idStr);
				}
		}
		redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid input format" + inputValue);
		return "redirect:/mainGate";
	}

	@PostMapping({ "/licensegatein" })
	public String licensegatein(@RequestParam String inputValue, RedirectAttributes redirectAttributes) {
		System.out.println("Input value: " + inputValue);
		String inoutFlag = "";
		String[] parts = inputValue.split("/");
		if (parts.length == 3) {
			String entityType = parts[0];
			String category = parts[1];
			String idStr = parts[2];
			if ("HPNSK".equals(category))
				try {
					Long entityId = Long.valueOf(Long.parseLong(idStr));
					String fullName = null;
					String status = this.inscanService.getMainGateSatus(entityId, entityType);
					if (status != null && status.equalsIgnoreCase("N")) {
						boolean restrictstatus;
						switch (entityType) {
						case "OFC":
							fullName = this.officerService.getFullName(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							inoutFlag = this.officerService.processAndSaveLicenseGateDetails(entityId,
									redirectAttributes);
							redirectAttributes.addFlashAttribute("ScanSuccessful",
									"AMC " + fullName + " Scan " + inoutFlag);
							return "redirect:/licenseGate";
						case "EMP":
							fullName = this.emplloyeeService.getFullName(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							inoutFlag = this.emplloyeeService.processAndSaveLicenseGateDetails(entityId);
							redirectAttributes.addFlashAttribute("ScanSuccessful",
									"Visitor " + fullName + " Scan " + inoutFlag);
							return "redirect:/licenseGate";
						case "GAT":
							fullName = this.gatService.getFullName(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							inoutFlag = this.gatService.processAndSaveLicenseGateDetails(entityId);
							redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
							return "redirect:/licenseGate";
						case "TAT":
							fullName = this.tatService.getFullName(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							inoutFlag = this.tatService.processAndSaveLicenseGateDetails(entityId);
							redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
							return "redirect:/licenseGate";
						case "FEG":
							fullName = this.fegService.getFullName(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							inoutFlag = this.fegService.processAndSaveLicenseGateDetails(entityId);
							redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
							return "redirect:/licenseGate";
						case "SEC":
							fullName = this.secService.getFullName(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							inoutFlag = this.secService.processAndSaveLicenseGateDetails(entityId);
							redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
							return "redirect:/licenseGate";
						case "CON":
							fullName = this.contractorService.getFullName(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							inoutFlag = this.contractorService.processAndSaveLicenseGateDetails(entityId);
							redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
							return "redirect:/licenseGate";
						case "CONW":
							fullName = this.contractorWorkmanService.getFullName(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							inoutFlag = this.contractorWorkmanService.processAndSaveLicenseGateDetails(entityId);
							redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
							return "redirect:/licenseGate";

						case "MT":
							fullName = this.mathadiService.getFullName(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							inoutFlag = this.mathadiService.processAndSaveLicenseGateDetails(entityId,
									redirectAttributes);
							redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
							System.out.println("inoutFlag " + inoutFlag);
							return "redirect:/licenseGate";

						case "PT":
							fullName = this.packedService.getFullName(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							inoutFlag = this.packedService.processAndSaveLicenseGateDetails(entityId);
							redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
							return "redirect:/licenseGate";
						case "BK":
							fullName = this.bulkService.getFullName(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							inoutFlag = this.bulkService.processAndSaveLicenseGateDetails(entityId);
							redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
							return "redirect:/licenseGate";
						case "TR":
							fullName = this.transporterService.getFullName(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							inoutFlag = this.transporterService.processAndSaveLicenseGateDetails(entityId);
							redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
							return "redirect:/licenseGate";
						case "PW":
							fullName = this.workmanService.getFullName(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							inoutFlag = this.workmanService.processAndSaveLicenseGateDetails(entityId);
							redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
							return "redirect:/licenseGate";
						case "AMC":
							fullName = this.amcService.getFullName(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							inoutFlag = this.amcService.processAndSaveLicenseGateDetails(entityId);
							redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
							return "redirect:/licenseGate";
						case "VS":
							fullName = this.visitorService.getFullName(entityId);
							restrictstatus = this.visitorService.restrictornot(entityId);
							if (fullName == null || fullName.trim().isEmpty()) {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"Invalid data for " + entityType + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							if (!restrictstatus) {
								inoutFlag = this.visitorService.processAndSaveLicenseGateDetails(entityId);
							} else {
								redirectAttributes.addFlashAttribute("UnknownEntityType",
										"User is restricted." + fullName + " with ID " + entityId);
								return "redirect:/licenseGate";
							}
							redirectAttributes.addFlashAttribute("ScanSuccessful", inoutFlag + " " + fullName);
							return "redirect:/licenseGate";
						}
						System.out.println("Unknown entity type: " + entityType);
						redirectAttributes.addFlashAttribute("UnknownEntityType", "UnknownEntityType" + inputValue);
						return "redirect:/licenseGate";
					}
					redirectAttributes.addFlashAttribute("ScanSuccessful", "Please enter main gate");
					return "redirect:/licenseGate";
				} catch (NumberFormatException e) {
					System.out.println("Invalid ID format: " + idStr);
				}
		}
		redirectAttributes.addFlashAttribute("UnknownEntityType", "Invalid input format" + inputValue);
		return "redirect:/licenseGate";
	}

	@GetMapping({ "/restrictVisitor" })
	public ResponseEntity<String> restrictUser(@RequestParam("visitorId") long visitorId) {
		boolean success = this.visitorService.restrictUser(Long.valueOf(visitorId));
		if (success)
			return ResponseEntity.ok("User restricted successfully.");
		return ResponseEntity.status((HttpStatusCode) HttpStatus.INTERNAL_SERVER_ERROR)
				.body("Failed to restrict user.");
	}

	@GetMapping({ "/unrestrictVisitor" })
	public ResponseEntity<String> unrestrictUser(@RequestParam("visitorId") long visitorId) {
		boolean success = this.visitorService.unrestrictUser(Long.valueOf(visitorId));
		if (success)
			return ResponseEntity.ok("User unrestricted successfully.");
		return ResponseEntity.status((HttpStatusCode) HttpStatus.INTERNAL_SERVER_ERROR)
				.body("Failed to unrestrict user.");
	}
}
