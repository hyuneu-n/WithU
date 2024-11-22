package com.danpoong.withu.common.exception;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(String dataSource, long id) {
    super(dataSource + "에서 ID " + id + "를 찾을 수 없습니다.");
  }

  public ResourceNotFoundException(String dataSource, String details) {
    super(dataSource + "에서 " + details + "를 찾을 수 없습니다.");
  }
}
