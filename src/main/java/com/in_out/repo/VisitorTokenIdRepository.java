package com.in_out.repo;


import com.in_out.model.VisitorTokenId;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorTokenIdRepository extends JpaRepository<VisitorTokenId, Long> {
  VisitorTokenId findFirstByOrderByIdDesc();
  Optional<VisitorTokenId> findByCurrSrNo(Long currSrNo);
//  VisitorTokenId findByTokenNumber(String tokenNumber);
//  VisitorTokenId findById(String id);
}
