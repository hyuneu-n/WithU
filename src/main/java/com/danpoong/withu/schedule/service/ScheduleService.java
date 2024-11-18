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
        User user = userService.findById(userId);

        Schedule schedule = new Schedule();
        schedule.setUser(user);

        // familyId가 null이 아닌 경우에만 가족 설정
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

        // D-Day 계산 후 설정
        int dday = schedule.calculateDDay();
        schedule.setDday(dday);

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return new ScheduleResponseDto(savedSchedule);
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(Long scheduleId, Long userId, String title, String memo, LocalDate date) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("해당 일정을 찾을 수 없습니다: scheduleId=" + scheduleId));

        // 현재 사용자가 일정의 작성자인지 확인
        if (!schedule.getUser().getId().equals(userId)) {
            throw new RuntimeException("해당 일 정에 대한 수정 권한이 없습니다.");
        }

        schedule.setTitle(title);
        schedule.setMemo(memo);
        schedule.setDate(date);
        schedule.setUpdatedAt(LocalDate.now().atStartOfDay());

        int dday = schedule.calculateDDay();
        schedule.setDday(dday);

        Schedule updatedSchedule = scheduleRepository.save(schedule);

        return new ScheduleResponseDto(updatedSchedule);
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto findScheduleByUserAndFamily(Long userId, Long scheduleId) {
        User user = userService.findById(userId);
        Long familyId = user.getFamily() != null ? user.getFamily().getFamilyId() : null;

        Schedule schedule;

        // 가족 ID가 있는 경우 가족 구성원이 추가한 일정 확인
        if (familyId != null) {
            schedule = scheduleRepository.findByScheduleIdAndFamily(scheduleId, user.getFamily())
                    .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));
        } else {
            // 가족 ID가 없는 경우 사용자가 추가한 일정만 확인
            schedule = scheduleRepository.findByScheduleIdAndUser(scheduleId, user)
                    .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다."));
        }

        return new ScheduleResponseDto(schedule);
    }

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("해당 일정을 찾을 수 없습니다: scheduleId=" + scheduleId));
        schedule.setDeleted(true); // 소프트 삭제 설정
        scheduleRepository.save(schedule);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();
        return schedules.stream()
                .map(ScheduleResponseDto::new)
                .collect(Collectors.toList());
    }
}
