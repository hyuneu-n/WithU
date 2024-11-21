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
    public UserStatus createOrUpdateEmoji(Long userId, UserStatus.StatusEmoji emoji) {
        User user = userService.findById(userId);
        List<UserStatus> userStatuses = userStatusRepository.findByUser(user);

        UserStatus userStatus;
        if (userStatuses.isEmpty()) {
            userStatus = new UserStatus(user, null, null, LocalDateTime.now().plusHours(24));
        } else {
            userStatus = userStatuses.get(0); // 첫 번째 상태를 업데이트
        }

        userStatus.setStatusEmoji(emoji);
        userStatus.setExpiresAt(LocalDateTime.now().plusHours(24)); // 만료 시간 갱신
        return userStatusRepository.save(userStatus);
    }

    @Transactional
    public void deleteEmoji(Long userId) {
        User user = userService.findById(userId);
        List<UserStatus> userStatuses = userStatusRepository.findByUser(user);

        if (userStatuses.isEmpty()) {
            throw new RuntimeException("사용자의 상태를 찾을 수 없습니다.");
        }

        UserStatus userStatus = userStatuses.get(0);
        userStatus.setStatusEmoji(null);
        userStatusRepository.save(userStatus);
    }

    @Transactional
    public UserStatus createOrUpdateMemo(Long userId, String text) {
        User user = userService.findById(userId);
        List<UserStatus> userStatuses = userStatusRepository.findByUser(user);

        UserStatus userStatus;
        if (userStatuses.isEmpty()) {
            userStatus = new UserStatus(user, null, null, LocalDateTime.now().plusHours(24));
        } else {
            userStatus = userStatuses.get(0); // 첫 번째 상태를 업데이트
        }

        userStatus.setStatusText(text);
        userStatus.setExpiresAt(LocalDateTime.now().plusHours(24)); // 만료 시간 갱신
        return userStatusRepository.save(userStatus);
    }

    @Transactional
    public void deleteMemo(Long userId) {
        User user = userService.findById(userId);
        List<UserStatus> userStatuses = userStatusRepository.findByUser(user);

        if (userStatuses.isEmpty()) {
            throw new RuntimeException("사용자의 상태를 찾을 수 없습니다.");
        }

        UserStatus userStatus = userStatuses.get(0);
        userStatus.setStatusText(null);
        userStatusRepository.save(userStatus);
    }

    @Transactional(readOnly = true)
    public Optional<UserStatus> getStatusByUserId(Long userId) {
        User user = userService.findById(userId);
        List<UserStatus> userStatuses = userStatusRepository.findByUser(user);

        if (userStatuses.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(userStatuses.get(0));
        }
    }
}
