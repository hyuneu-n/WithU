package com.danpoong.withu.randommessage.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.danpoong.withu.randommessage.service.RandomMessageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/random-message")
@RequiredArgsConstructor
@Tag(name = "Random Message")
public class RandomMessageController {

  private final RandomMessageService randomMessageService;

  @Operation(summary = "랜덤 멘트")
  @GetMapping
  public ResponseEntity<String> getRandomMessage() {
    String message = randomMessageService.getRandomMessage();
    return ResponseEntity.ok(message);
  }
}
