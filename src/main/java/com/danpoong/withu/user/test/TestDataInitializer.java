//package com.danpoong.withu.user.test;
//
//import com.danpoong.withu.family.domain.Family;
//import com.danpoong.withu.family.repository.FamilyRepository;
//import com.danpoong.withu.user.domain.User;
//import com.danpoong.withu.user.repository.UserRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//
//@Component
//public class TestDataInitializer implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final FamilyRepository familyRepository;
//
//    public TestDataInitializer(UserRepository userRepository, FamilyRepository familyRepository) {
//        this.userRepository = userRepository;
//        this.familyRepository = familyRepository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        // 가족 그룹 데이터 초기화
//        Family family1 = familyRepository.save(new Family(null, LocalDate.now()));
//        Family family2 = familyRepository.save(new Family(null, LocalDate.now()));
//
//        if (userRepository.count() == 0) { // 중복 데이터 방지
//            userRepository.save(new User(1, "abc@abc", "엄마","https://example.com/profile2.png",
//                    LocalDate.of(1995, 2, 15), family1, "ROLE_USER", null));
//            userRepository.save(createUser("test2@example.com", "User2", "https://example.com/profile2.png",
//                    LocalDate.of(1995, 2, 15), family1, "ROLE_USER", null));
//            userRepository.save(createUser("test3@example.com", "User3", "https://example.com/profile3.png",
//                    LocalDate.of(1998, 5, 20), family2, "ROLE_USER", null));
//            userRepository.save(createUser("test4@example.com", "User4", "https://example.com/profile4.png",
//                    LocalDate.of(2000, 10, 10), family2, "ROLE_USER", null));
//        }
//    }
//}
