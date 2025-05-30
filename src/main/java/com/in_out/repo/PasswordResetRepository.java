package com.in_out.repo;


import com.in_out.model.PasswordResetEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRepository extends CrudRepository<PasswordResetEntity, Long> {
  PasswordResetEntity findFirstByOrderByIdDesc();
}
