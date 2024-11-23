//package com.danpoong.withu.notification.service;
//
//import com.danpoong.withu.notification.domain.EmitterRepository;
//import com.danpoong.withu.notification.domain.Notification;
//import com.danpoong.withu.notification.domain.NotificationType;
//import com.danpoong.withu.notification.dto.NotificationDto;
//import com.danpoong.withu.notification.dto.NotificationResponseDto;
//import com.danpoong.withu.notification.repository.NotificationRepository;
//import com.danpoong.withu.schedule.repository.ScheduleRepository;
//import com.danpoong.withu.user.domain.User;
//import com.danpoong.withu.user.service.UserService;
//import com.danpoong.withu.letter.repository.LetterRepository;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class NotificationService {
//
//    private final EmitterRepository emitterRepository;
//    private final NotificationRepository notificationRepository;
//    private final ScheduleRepository scheduleRepository;
//    private final LetterRepository letterRepository;
//    private final UserService userService;
//    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간 타임아웃
//
//    /**
//     * 1. "나에게 도착한 편지" 알림 전송
//     * 매 분마다 실행. 사용자 알림 시간이 현재 시간과 일치하면 읽지 않은 편지가 있는지 확인 후 알림 전송
//     */
//    @Scheduled(cron = "0 * * * * *") // 매분 실행
//    public void sendLetterNotifications() {
//        List<User> users = userService.findAllUsersWithNotificationSettings(); // 알림 설정된 사용자 조회
//        LocalDateTime now = LocalDateTime.now(); // 현재 시간
//
//        for (User user : users) {
//            if (user.getPushNotificationTime() == null) continue; // 알림 시간이 설정되지 않은 사용자 제외
//
//            // 현재 시간과 사용자가 설정한 알림 시간이 일치하지 않으면 넘어감
//            if (now.getHour() != user.getPushNotificationTime().getHour() ||
//                    now.getMinute() != user.getPushNotificationTime().getMinute()) {
//                continue;
//            }
//
//            // 읽지 않은 편지가 있으면 알림 전송
//            if (letterRepository.existsByReceiverIdAndIsReadFalse(user)) {
//                NotificationDto dto = NotificationDto.builder()
//                        .msgTitle("📨 읽지 않은 편지가 도착했습니다!") // 알림 제목
//                        .type(NotificationType.LETTER_NOTIFICATION) // 알림 타입
//                        .build();
//
//                sendNotification(user, dto); // 알림 전송 메서드 호출
//            }
//        }
//    }
//
//    /**
//     * 2. "일정 알림" 전송
//     * 매일 아침 7시에 실행. 당일이나 D-2, D-1 일정이 있는 경우 알림 전송
//     */
//    @Scheduled(cron = "0 0 7 * * *") // 매일 오전 7시 실행
//    public void sendScheduleNotifications() {
//        LocalDate today = LocalDate.now(); // 오늘 날짜
//
//        List<User> users = userService.findAllUsers(); // 모든 사용자 조회
//        for (User user : users) {
//            // 당일, D-2, D-1 범위 내 일정이 있는지 확인
//            if (scheduleRepository.existsByUserAndDateBetween(user, today.minusDays(2), today)) {
//                NotificationDto dto = NotificationDto.builder()
//                        .msgTitle("🗓️ 잊지 마세요! 가족 일정이 있습니다.") // 알림 제목
//                        .type(NotificationType.SCHEDULE_NOTIFICATION) // 알림 타입
//                        .build();
//
//                sendNotification(user, dto); // 알림 전송 메서드 호출
//            }
//        }
//    }
//
//    /**
//     * 알림 전송 메서드
//     * 사용자에게 알림 데이터를 전송. SseEmitter가 없으면 로그만 남김
//     */
//    private void sendNotification(User user, NotificationDto dto) {
//        Notification notification = notificationRepository.save(dto.toEntity(user)); // 알림 저장
//        NotificationResponseDto responseDto = new NotificationResponseDto(notification); // DTO 변환
//
//        SseEmitter emitter = emitterRepository.get(user.getId()); // 사용자에 대한 Emitter 조회
//        if (emitter != null) {
//            try {
//                emitter.send(SseEmitter.event().name("notification").data(responseDto)); // 알림 데이터 전송
//                log.info("알림 전송 완료: userId={}, notification={}", user.getId(), responseDto);
//            } catch (IOException e) {
//                log.error("알림 전송 실패: userId={}, error={}", user.getId(), e.getMessage());
//                emitter.completeWithError(e); // 에러 발생 시 Emitter 종료
//                emitterRepository.deleteById(user.getId()); // Emitter 삭제
//            }
//        } else {
//            log.warn("Emitter가 없어 알림을 전송하지 못했습니다. userId={}", user.getId());
//        }
//    }
//
//    /**
//     * SSE 연결 설정
//     * 사용자와 SSE 연결을 설정. 연결 중 오류 발생 시 로그를 남기고 Emitter 삭제
//     */
//    public SseEmitter connect(Long userId) {
//        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT); // SSE 연결 생성
//        emitterRepository.save(userId, emitter); // Emitter 저장
//
//        emitter.onCompletion(() -> emitterRepository.deleteById(userId)); // 완료 시 삭제
//        emitter.onTimeout(() -> emitterRepository.deleteById(userId)); // 타임아웃 시 삭제
//        emitter.onError((e) -> emitterRepository.deleteById(userId)); // 에러 발생 시 삭제
//
//        try {
//            emitter.send(SseEmitter.event().name("INIT").data("연결되었습니다.")); // 초기 연결 메시지 전송
//        } catch (Exception e) {
//            log.error("Emitter 연결 중 오류 발생: {}", e.getMessage());
//            emitter.completeWithError(e); // 연결 종료
//        }
//
//        log.info("SSE 연결 완료: userId={}", userId);
//        return emitter;
//    }
//
//    /**
//     * 테스트 알림 전송
//     * 테스트용 알림 데이터를 전송
//     */
//    public void sendTestNotification(Long userId, String content) {
//        SseEmitter emitter = emitterRepository.get(userId); // 사용자에 대한 Emitter 조회
//        if (emitter != null) {
//            try {
//                emitter.send(SseEmitter.event().name("notification").data(content)); // 테스트 데이터 전송
//                log.info("테스트 알림 전송 완료: userId={}, content={}", userId, content);
//            } catch (Exception e) {
//                log.error("알림 전송 중 오류 발생: userId={}, error={}", userId, e.getMessage());
//                emitter.completeWithError(e); // 에러 발생 시 Emitter 종료
//                emitterRepository.deleteById(userId); // Emitter 삭제
//            }
//        } else {
//            log.warn("해당 userId에 대한 Emitter가 없습니다: userId={}", userId);
//        }
//    }
//}
