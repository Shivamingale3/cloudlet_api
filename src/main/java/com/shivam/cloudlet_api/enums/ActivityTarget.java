package com.shivam.cloudlet_api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityTarget {
  AUTH("AUTH"),
  USER("USER"),
  FILE("FILE"),
  FOLDER("FOLDER"),
  BUCKET("BUCKET");

  private final String value;

  ActivityTarget(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static ActivityTarget fromValue(String value) {
    for (ActivityTarget type : values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
