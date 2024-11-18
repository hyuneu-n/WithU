package com.danpoong.withu.userstatus.repository;

import com.danpoong.withu.family.domain.Family;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.userstatus.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserStatusRepository extends JpaRepository<UserStatus, Long> {
    List<UserStatus> findByFamily(Family family);
    List<UserStatus> findByUser(User user);
    void deleteByUser(User user);
}

