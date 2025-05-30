package com.in_out.repo;


import org.springframework.data.jpa.repository.JpaRepository;

import com.in_out.model.Amc;

public interface AmcRepository extends JpaRepository<Amc, Long> {
  Amc findByAadharNumber(String paramString);
}
