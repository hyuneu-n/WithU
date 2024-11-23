package com.danpoong.withu.family.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.danpoong.withu.family.domain.Family;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {
    Family findByFamilyId(Long familyId);
}
