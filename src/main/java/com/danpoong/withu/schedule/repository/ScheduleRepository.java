package com.danpoong.withu.schedule.repository;

import com.danpoong.withu.family.domain.Family;
import com.danpoong.withu.schedule.domain.Schedule;
import com.danpoong.withu.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByUser_Id(Long userId);
    List<Schedule> findByFamily_FamilyId(Long familyId);
    List<Schedule> findAllByDeletedFalse();
}
