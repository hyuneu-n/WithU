package com.danpoong.withu.schedule.repository;

import com.danpoong.withu.family.domain.Family;
import com.danpoong.withu.schedule.domain.Schedule;
import com.danpoong.withu.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByScheduleIdAndFamily(Long scheduleId, Family family);
    Optional<Schedule> findByScheduleIdAndUser(Long scheduleId, User user);
    List<Schedule> findAllByDeletedFalse();
}
