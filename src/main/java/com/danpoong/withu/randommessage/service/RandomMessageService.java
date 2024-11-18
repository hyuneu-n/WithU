package com.danpoong.withu.randommessage.service;

import com.danpoong.withu.randommessage.domain.RandomMessage;
import com.danpoong.withu.randommessage.repository.RandomMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RandomMessageService {

    private final RandomMessageRepository randomMessageRepository;

    public String getRandomMessage() {
        List<RandomMessage> messages = randomMessageRepository.findAll();
        if (messages.isEmpty()) {
            return "저장된 메시지가 없습니다!";
        }
        Random random = new Random();
        int index = random.nextInt(messages.size());
        return messages.get(index).getMessage();
    }
}
