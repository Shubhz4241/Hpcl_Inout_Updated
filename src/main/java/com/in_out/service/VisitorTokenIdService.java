package com.in_out.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.in_out.model.VisitorTokenId;
import com.in_out.repo.VisitorTokenIdRepository;

@Service
public class VisitorTokenIdService {

	@Autowired
	private VisitorTokenIdRepository visitorTokenRepository;

	public VisitorTokenId findById(Long id) {
		Optional<VisitorTokenId> visitorToken = visitorTokenRepository.findById(id);
		return visitorToken.orElse(null); // Return null if not found
	}

	///////////////////
	public Long generateNextSrNo() {
		VisitorTokenId lastToken = visitorTokenRepository.findFirstByOrderByIdDesc();
		return (lastToken != null && lastToken.getCurrSrNo() != null) ? lastToken.getCurrSrNo() + 1 : 1L;
	}

	public VisitorTokenId updateCurrentSrNo(Long newSrNo) {
		VisitorTokenId lastToken = visitorTokenRepository.findFirstByOrderByIdDesc();
		if (lastToken != null) {
			lastToken.setCurrSrNo(newSrNo);
			return visitorTokenRepository.save(lastToken);
		}
		return null;
	}

	public Long getCurrentSrNo() {
		VisitorTokenId lastToken = visitorTokenRepository.findFirstByOrderByIdDesc();
		return lastToken != null ? lastToken.getCurrSrNo() : 1L;
	}
	
	public VisitorTokenId findByTokenId(Long curr_sr_no) {
//    	System.out.println(curr_sr_no);
        Optional<VisitorTokenId> currSrNo = visitorTokenRepository.findByCurrSrNo(curr_sr_no);
        System.out.println(curr_sr_no);
        return currSrNo.orElse(null); // Return null if not found
    }
	//////////////////////////////
}