package com.in_out.repo;

import com.in_out.model.AadharNumberDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AadharNumberDetailsRepository extends JpaRepository<AadharNumberDetails, Long> {
  AadharNumberDetails findByAadharNumber(String paramString);
}
