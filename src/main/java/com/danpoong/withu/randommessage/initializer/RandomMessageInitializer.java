package com.danpoong.withu.randommessage.initializer;

import com.danpoong.withu.randommessage.domain.RandomMessage;
import com.danpoong.withu.randommessage.repository.RandomMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RandomMessageInitializer implements CommandLineRunner {

    private final RandomMessageRepository randomMessageRepository;

    @Override
    public void run(String... args) {
        // 데이터가 없는 경우에만 초기 데이터를 삽입
        if (randomMessageRepository.count() == 0) {
            randomMessageRepository.save(new RandomMessage("안녕하세요!"));
            randomMessageRepository.save(new RandomMessage("좋은 하루 되세요!"));
            randomMessageRepository.save(new RandomMessage("화이팅!"));
            randomMessageRepository.save(new RandomMessage("오늘도 즐거운 하루!"));
            randomMessageRepository.save(new RandomMessage("항상 행복하세요!"));
        }
    }
}
