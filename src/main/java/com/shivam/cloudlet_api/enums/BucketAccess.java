package com.shivam.cloudlet_api.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BucketAccess {
  PUBLIC("public"),
  PRIVATE("private");

  private final String value;

  BucketAccess(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @JsonCreator
  public static BucketAccess fromValue(String value) {
    for (BucketAccess type : values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown value: " + value);
  }
}
