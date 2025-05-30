package com.in_out.service;


import com.in_out.model.PasswordResetEntity;
import com.in_out.repo.PasswordResetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PassResetService {
  @Autowired
  private PasswordResetRepository passwordResetRepository;
  
  public PasswordResetEntity getPasswordResetEntity() {
    PasswordResetEntity entity = this.passwordResetRepository.findFirstByOrderByIdDesc();
    if (entity == null)
      return new PasswordResetEntity(); 
    return entity;
  }
  
  public void resetPassword(PasswordResetEntity passwordResetEntity, String newPassword) {
    passwordResetEntity.setResetPassword(newPassword);
    this.passwordResetRepository.save(passwordResetEntity);
  }
}
