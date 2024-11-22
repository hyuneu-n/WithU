package com.danpoong.withu.userstatus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.danpoong.withu.family.domain.Family;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.userstatus.domain.UserStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {
  List<UserStatus> findByFamily(Family family);

  List<UserStatus> findByUser(User user);

  @Query("SELECT us FROM UserStatus us WHERE us.family.familyId = :familyId")
  List<UserStatus> findByFamilyId(@Param("familyId") Long familyId);
}
