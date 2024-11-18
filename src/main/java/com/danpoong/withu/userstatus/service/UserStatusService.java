package com.danpoong.withu.userstatus.service;

import com.danpoong.withu.family.domain.Family;
import com.danpoong.withu.family.service.FamilyService;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.repository.UserRepository;
import com.danpoong.withu.user.service.UserService;
import com.danpoong.withu.userstatus.domain.UserStatus;
import com.danpoong.withu.userstatus.domain.UserStatus.StatusEmoji;
import com.danpoong.withu.userstatus.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserService userService;
    private final FamilyService familyService;

    public List<UserStatus> getStatusByFamilyId(Long familyId) {
        if (familyId == null) {
            return List.of();
        }
        Family family = familyService.findById(familyId);
        return userStatusRepository.findByFamily(family);
    }

    @Transactional
    public void deleteStatusByUserId(Long userId) {
        User user = userService.findById(userId);
        userStatusRepository.deleteByUser(user);
    }

    @Transactional
    public UserStatus createOrUpdateStatus(Long userId, StatusEmoji emoji, String text) {
        User user = userService.findById(userId);
        List<UserStatus> userStatuses = userStatusRepository.findByUser(user);
        UserStatus userStatus;

        // 둘 중 하나만 설정되도록 처리
        if (emoji != null && text != null) {
            text = null; // text가 들어오면 emoji만 유지
        }

        if (userStatuses.isEmpty()) {
            // 상태가 없을 경우 새로 생성
            userStatus = new UserStatus(user, emoji, text, LocalDateTime.now().plusHours(24));
        } else {
            // 상태가 이미 있을 경우 첫번째 항목 업데이트
            userStatus = userStatuses.get(0);
            userStatus.setStatusEmoji(emoji);
            userStatus.setStatusText(text);
            userStatus.setExpiresAt(LocalDateTime.now().plusHours(24)); // 만료 시간 설정
        }

        return userStatusRepository.save(userStatus);
    }

    @Transactional(readOnly = true)
    public Optional<UserStatus> getStatusByUserId(Long userId) {
        User user = userService.findById(userId);
        List<UserStatus> userStatuses = userStatusRepository.findByUser(user);

        return userStatuses.isEmpty() ? Optional.empty() : Optional.of(userStatuses.get(0));
    }
}
