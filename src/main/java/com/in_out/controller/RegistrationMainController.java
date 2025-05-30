package com.in_out.controller;


import com.in_out.model.License;
import com.in_out.model.PasswordResetEntity;
import com.in_out.model.User;
import com.in_out.repo.LicenseRepository;
import com.in_out.repo.UserRepository;
import com.in_out.service.InscanService;
import com.in_out.service.LicenseGateService;
import com.in_out.service.LicenseService;
import com.in_out.service.PassResetService;
import com.in_out.service.UserService;
import java.security.Principal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationMainController {
  @Autowired
  private UserRepository userRepository;
  
  @Autowired
  private UserService userService;
  
  @Autowired
  private PassResetService passResetService;
  
  @Autowired
  private LicenseRepository licenseRepository;
  
  @Autowired
  private InscanService inscanService;
  
  @Autowired
  private LicenseGateService licensegateService;
  
  @Autowired
  private LicenseService licenseService;
  
  @Autowired
  private PasswordEncoder passwordEncoder;
  
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
    User admin = (User)this.userRepository.getReferenceById(Integer.valueOf(1));
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
  
  @GetMapping({"/login"})
  public String Login(@RequestParam(name = "error", required = false) String error, Model model) {
    String status = this.userService.checkcheckLicenseStatus();
    User admin = (User)this.userRepository.getReferenceById(Integer.valueOf(1));
    License license = admin.getLicense();
    if (status == null) {
      model.addAttribute("FirstLicenseKey", "Please Enter the License Key to Activate the product");
      return "License";
    } 
    if (status.equals("Expired")) {
      model.addAttribute("OldlicensekeyExpired", "License Key Expired.Please Enter new License Key!");
      return "License";
    } 
    if (error != null)
      model.addAttribute("error", "Invalid Username or Password"); 
    long remainingdays = ChronoUnit.DAYS.between(LocalDate.now(), license.getExpirydate());
    if (remainingdays == 0L) {
      model.addAttribute("remainingdays", Boolean.valueOf(false));
    } else if (remainingdays == 1L) {
      model.addAttribute("onedayremain", Boolean.valueOf(true));
    } else {
      model.addAttribute("remainingdays", Long.valueOf(remainingdays));
    } 
    return "login";
  }
  
  @GetMapping({"/setting"})
  public String Myprofile(Model model, Principal principal) {
    addLicenseInfoToModel(model);
    String name = principal.getName();
    User user = this.userRepository.findbyuserName(name);
    model.addAttribute("userId", Integer.valueOf(user.getUserId()));
    model.addAttribute("user", user);
    addUsernameAndRoleToModel(model, (Principal)SecurityContextHolder.getContext().getAuthentication());
    return "setting";
  }
  
  @PostMapping({"/UpdateProfile"})
  public String updateProfile(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword, Model model, Principal principal, RedirectAttributes redirectAttributes) {
      addLicenseInfoToModel(model);
      addUsernameAndRoleToModel(model, (Principal)SecurityContextHolder.getContext().getAuthentication());
      String name = principal.getName();
      User currentUser = this.userRepository.findbyuserName(name);
      boolean isOldPasswordCorrect = this.passwordEncoder.matches(oldPassword, currentUser.getPassword());
      if (!isOldPasswordCorrect) {
          redirectAttributes.addFlashAttribute("errorMessage", "Invalid old password.");
          return "redirect:/setting";
      } 
      if (newPassword == null || newPassword.trim().isEmpty()) {
          redirectAttributes.addFlashAttribute("errorMessage", "New password cannot be empty.");
          return "redirect:/setting";
      }
      currentUser.setPassword(this.passwordEncoder.encode(newPassword));
      this.userRepository.save(currentUser); // Ensure this save is successful
      redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully.");
      return "redirect:/setting";
  }
  
  @GetMapping({"/access"})
  public String addUser(Model model, Principal principal) {
    String username = principal.getName();
    User user = this.userRepository.findByUserName(username);
    if (user != null && user.getRole().contains("security")) {
      model.addAttribute("message", "You don't have permission to view the list of users.");
    } else {
      List<User> users;
      if (user != null && user.getRole().contains("officer")) {
        users = this.userRepository.finfbyRole("officer");
      } else {
        users = this.userRepository.findAll();
      } 
      model.addAttribute("users", users);
    } 
    addLicenseInfoToModel(model);
    addUsernameAndRoleToModel(model, (Principal)SecurityContextHolder.getContext().getAuthentication());
    return "access";
  }
  
  @PostMapping({"/register"})
  public String Addnewuser(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
      if (user.getRole() == null || user.getRole().isEmpty()) {
          redirectAttributes.addFlashAttribute("errorMessage", "Role is required.");
          return "redirect:/access";
      }
      this.userService.CreateUser(user); // Ensure userService sets the role
      redirectAttributes.addFlashAttribute("useraddsuccess", "User Added Successfully");
      return "redirect:/access"; // Redirect to access to see the updated list
  }
  
  @GetMapping({"/users/{id}/delete"})
  public String deleteUser(@PathVariable("id") int userId, RedirectAttributes redirectAttributes) {
    boolean deletionSuccessful = this.userService.deleteUserById(userId);
    if (deletionSuccessful) {
      redirectAttributes.addFlashAttribute("deleteSuccess", "User deleted successfully");
    } else {
      redirectAttributes.addFlashAttribute("deleteError", "Error deleting user");
    } 
    return "redirect:/access";
  }
  
  @GetMapping({"/changeReset"})
  public String showResetPasswordForm(Model model) {
    PasswordResetEntity passwordResetEntity = this.passResetService.getPasswordResetEntity();
    model.addAttribute("passwordResetEntity", passwordResetEntity);
    addLicenseInfoToModel(model);
    addUsernameAndRoleToModel(model, (Principal)SecurityContextHolder.getContext().getAuthentication());
    return "changeReset";
  }
  
  /*
  @PostMapping({"/resetpassword"})
  public String resetPassword(@ModelAttribute("passwordResetEntity") PasswordResetEntity passwordResetEntity, Model model) {
    this.passResetService.resetPassword(passwordResetEntity, passwordResetEntity.getResetPassword());
    return "redirect:/changeReset";
  }
  */
  @PostMapping({"/resetpassword"})
  public String resetPassword(@ModelAttribute("passwordResetEntity") PasswordResetEntity passwordResetEntity, Model model, RedirectAttributes redirectAttributes) {
      this.passResetService.resetPassword(passwordResetEntity, passwordResetEntity.getResetPassword());
      redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully."); // Ensure attribute name matches
      return "redirect:/changeReset";
  }
  
  @PostMapping({"/resetcount"})
  public String resetForm(@RequestParam String reset, RedirectAttributes redirectAttributes) {
    PasswordResetEntity passwordResetEntity = this.passResetService.getPasswordResetEntity();
    if (reset.equals(passwordResetEntity.getResetPassword())) {
      redirectAttributes.addFlashAttribute("message", "Reset values match!");
      this.inscanService.updateNullExitDateTime();
      this.licensegateService.updateNullExiteTime();
    } else {
      redirectAttributes.addFlashAttribute("message", "Reset values do not match!");
    } 
    return "redirect:/";
  }
  
  @GetMapping({"/License"})
  public String licensekey() {
    return "License";
  }
  
  @PostMapping({"/LicenseValidate"})
  public String LicenseValidate(@ModelAttribute License license, RedirectAttributes redirectAttributes) {
    String licensestatus = this.licenseService.validateLicense(license.getLicensekey());
    if (licensestatus == null) {
      redirectAttributes.addFlashAttribute("InvalidKey", "Invalid Key ! Please Enter a Valid Key.");
      return "redirect:/License";
    } 
    if (licensestatus == "Expired") {
      redirectAttributes.addFlashAttribute("Expiredkey", "Expired Key");
      return "redirect:/License";
    } 
    return "redirect:/login";
  }
}