package com.shivam.cloudlet_api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivityType {
  LOGGED_IN("LOGGED_IN"),
  COMPLETED_PROFILE("COMPLETED_PROFILE"),
  RESET_PASSWORD("RESET_PASSWORD"),
  LOGGED_OUT("LOGOUT"),
  CREATED("CREATED"),
  ACCESSED("ACCESSED"),
  MODIFIED("MODIFIED"),
  DELETED("DELETED");

  private final String value;

  ActivityType(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static ActivityType fromValue(String value) {
    for (ActivityType type : values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
