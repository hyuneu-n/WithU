package com.danpoong.withu.user.repository;

import com.danpoong.withu.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByNickname(String nickname);

//    boolean existsByIdAndFamilyId(Long id, Long family);
    boolean existsByIdAndFamily_FamilyId(Long id, Long familyId);


}
