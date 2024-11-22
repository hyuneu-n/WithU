package com.danpoong.withu.notification.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * EmitterRepository는 각 사용자의 SseEmitter 객체를 관리합니다.
 */
@Repository
@RequiredArgsConstructor
public class EmitterRepository {

    // 사용자 ID별로 SseEmitter를 저장하는 ConcurrentHashMap
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 사용자 ID와 SseEmitter를 저장
     *
     * @param userId   사용자 ID
     * @param emitter  SseEmitter 객체
     */
    public void save(Long userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
    }

    /**
     * 사용자 ID로 SseEmitter를 조회
     *
     * @param userId 사용자 ID
     * @return SseEmitter 객체
     */
    public SseEmitter get(Long userId) {
        return emitters.get(userId);
    }

    /**
     * 사용자 ID로 저장된 SseEmitter를 삭제
     *
     * @param userId 사용자 ID
     */
    public void deleteById(Long userId) {
        emitters.remove(userId);
    }

    /**
     * 모든 SseEmitter를 삭제
     */
    public void deleteAll() {
        emitters.clear();
    }

    /**
     * 저장된 모든 사용자 ID와 SseEmitter를 반환
     *
     * @return 사용자 ID와 SseEmitter의 Map
     */
    public Map<Long, SseEmitter> findAllEmitters() {
        return emitters;
    }
}
