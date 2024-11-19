//package com.danpoong.withu.letter.repository;
//
//import com.danpoong.withu.letter.domain.Letter;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface LetterRepository extends JpaRepository<Letter, Long> {
//    Optional<Letter> findByMemberIdAndFileTitle(Long memberId, String fileTitle);
//    boolean existsByMemberIdAndFileTitle(Long memberId, String fileTitle);
//    boolean existsByMemberIdAndUrlTitle(Long memberId, String urlTitle);
//    Optional<Letter> findByMemberIdAndUrlTitle(Long memberId, String urlTitle);
//    List<Letter> findByRecordId(Long recordId);
//}
