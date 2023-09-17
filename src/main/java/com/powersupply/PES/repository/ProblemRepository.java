package com.powersupply.PES.repository;

import com.powersupply.PES.domain.entity.ProblemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<ProblemEntity, Long> {
}
