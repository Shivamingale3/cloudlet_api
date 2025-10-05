package com.shivam.cloudlet_api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {
  INVITED("invited"),
  ACTIVE("active"),
  DISABLED("disabled");

  private final String value;

  UserStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static UserStatus fromValue(String value) {
    for (UserStatus type : values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
