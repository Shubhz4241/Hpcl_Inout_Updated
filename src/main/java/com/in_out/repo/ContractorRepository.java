package com.in_out.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.in_out.model.Contractor;

public interface ContractorRepository extends JpaRepository<Contractor, Long> {
  Contractor findByAadharNumber(String paramString);
}
