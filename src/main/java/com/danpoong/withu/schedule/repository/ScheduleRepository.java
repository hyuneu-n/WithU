package com.danpoong.withu.schedule.repository;

import java.time.LocalDate;
import java.util.List;

import com.danpoong.withu.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.danpoong.withu.schedule.domain.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
  List<Schedule> findByUser_Id(Long userId);

  List<Schedule> findByFamily_FamilyId(Long familyId);

  List<Schedule> findAllByIsDeletedFalse();
  boolean existsByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
