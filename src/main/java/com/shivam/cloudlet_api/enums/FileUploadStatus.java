package com.shivam.cloudlet_api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FileUploadStatus {
  IN_PROGRESS("in_progress"),
  COMPLETED("completed"),
  FAILED("failed");

  private final String value;

  FileUploadStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static FileUploadStatus fromValue(String value) {
    for (FileUploadStatus type : values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
