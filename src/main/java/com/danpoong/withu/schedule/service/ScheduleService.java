package com.danpoong.withu.schedule.service;

import com.danpoong.withu.family.domain.Family;
import com.danpoong.withu.family.repository.FamilyRepository;
import com.danpoong.withu.schedule.domain.Schedule;
import com.danpoong.withu.schedule.dto.ScheduleResponseDto;
import com.danpoong.withu.schedule.repository.ScheduleRepository;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final FamilyRepository familyRepository;
    private final UserService userService;

    @Transactional
    public ScheduleResponseDto addSchedule(Long userId, Long familyId, String title, String memo, LocalDate date, String authorName) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("제목은 필수 입력 항목입니다.");
        }

        if (date == null) {
            throw new IllegalArgumentException("날짜는 필수 입력 항목입니다.");
        }

        User user = userService.findById(userId);

        Schedule schedule = new Schedule();
        schedule.setUser(user);

        if (familyId != null) {
            Family family = familyRepository.findById(familyId)
                    .orElseThrow(() -> new RuntimeException("가족 그룹을 찾을 수 없습니다: familyId=" + familyId));
            schedule.setFamily(family);
        }

        schedule.setTitle(title);
        schedule.setMemo(memo);
        schedule.setDate(date);
        schedule.setAuthor(authorName);
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setDday(schedule.calculateDDay());

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return new ScheduleResponseDto(savedSchedule);
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(Long scheduleId, Long userId, String title, String memo, LocalDate date) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("제목은 필수 입력 항목입니다.");
        }

        if (date == null) {
            throw new IllegalArgumentException("날짜는 필수 입력 항목입니다.");
        }

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("해당 일정을 찾을 수 없습니다: scheduleId=" + scheduleId));

        if (!schedule.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 일정을 수정할 권한이 없습니다.");
        }

        schedule.setTitle(title);
        schedule.setMemo(memo);
        schedule.setDate(date);
        schedule.setUpdatedAt(LocalDateTime.now());
        schedule.setDday(schedule.calculateDDay());

        Schedule updatedSchedule = scheduleRepository.save(schedule);

        return new ScheduleResponseDto(updatedSchedule);
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto findScheduleByUserAndFamily(Long userId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다: scheduleId=" + scheduleId));

        // 작성자인지 확인
        if (!schedule.getUser().getId().equals(userId)) {
            throw new RuntimeException("일정을 조회할 권한이 없습니다.");
        }

        return new ScheduleResponseDto(schedule);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId, Long userId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("해당 일정을 찾을 수 없습니다: scheduleId=" + scheduleId));

        if (!schedule.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 일정을 삭제할 권한이 없습니다.");
        }

        schedule.setDeleted(true); // 소프트 삭제 설정
        scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAllByDeletedFalse(); // 삭제되지 않은 일정만 반환
        return schedules.stream()
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findSchedulesByUserOrFamily(Long userId, Long familyId) {
        List<Schedule> schedules;

        if (familyId != null) {
            // 가족 ID를 기준으로 일정 검색
            schedules = scheduleRepository.findByFamily_FamilyId(familyId);
        } else {
            // 사용자 ID를 기준으로 일정 검색
            schedules = scheduleRepository.findByUser_Id(userId);
        }

        return schedules.stream()
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());
    }
}
