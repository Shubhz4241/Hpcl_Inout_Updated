package com.in_out.repo;

import com.in_out.model.Contractorworkman;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractorworkmanRepository extends JpaRepository<Contractorworkman, Long> {
  Contractorworkman findByAadharNumber(String paramString);
}
