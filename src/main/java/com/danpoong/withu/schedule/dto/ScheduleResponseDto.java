package com.danpoong.withu.schedule.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.danpoong.withu.schedule.domain.Schedule;

@Getter
@NoArgsConstructor
public class ScheduleResponseDto {
  private Long scheduleId;
  private LocalDate date;
  private String title;
  private String memo;
  private String author;
  private int dday;
  private Long familyId;

  public ScheduleResponseDto(Schedule schedule) {
    this.scheduleId = schedule.getScheduleId();
    this.date = schedule.getDate();
    this.title = schedule.getTitle();
    this.memo = schedule.getMemo();
    this.author = schedule.getAuthor();
    this.dday = schedule.calculateDDay();
    this.familyId = schedule.getFamily() != null ? schedule.getFamily().getFamilyId() : null;
  }
}
