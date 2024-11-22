package com.danpoong.withu.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.danpoong.withu.user.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  List<User> findAllByFamily_FamilyId(Long familyId);

  boolean existsByIdAndFamily_FamilyId(Long id, Long familyId);
  List<User> findAllByPushNotificationTimeIsNotNull();
}
