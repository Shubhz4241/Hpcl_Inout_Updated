package com.in_out.repo;


import com.in_out.model.Tat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TatRepository extends JpaRepository<Tat, Long> {
  Tat findByAadharNumber(String paramString);
}
