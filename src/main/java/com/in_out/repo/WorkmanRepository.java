package com.in_out.repo;


import com.in_out.model.Workman;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkmanRepository extends JpaRepository<Workman, Long> {
  Workman findByAadharNumber(String paramString);
}
