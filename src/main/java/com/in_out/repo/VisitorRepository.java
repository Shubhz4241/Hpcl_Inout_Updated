package com.in_out.repo;


import com.in_out.model.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VisitorRepository extends JpaRepository<Visitor, Long> {
  Visitor findByAadharNumber(String paramString);
  
  @Query(value = "SELECT COUNT(*) FROM visitor WHERE full_name IS NOT NULL", nativeQuery = true)
  long countVisitorsWithFullNameNotNull();
}
