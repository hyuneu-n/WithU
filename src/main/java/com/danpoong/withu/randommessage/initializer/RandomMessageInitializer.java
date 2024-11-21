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
        if (randomMessageRepository.count() == 0) {
            randomMessageRepository.save(new RandomMessage("24시간이내 편지를 보관해주세요"));
            randomMessageRepository.save(new RandomMessage("편지를 보관하지 않으면 사라져요"));
            randomMessageRepository.save(new RandomMessage("오늘은 어떤 하루였나요?"));
            randomMessageRepository.save(new RandomMessage("편지에 하트를 눌러봐요!"));
            randomMessageRepository.save(new RandomMessage("오늘의 편지 볼까요?"));
            randomMessageRepository.save(new RandomMessage("편지를 보내볼까요?"));
        }
    }
}
