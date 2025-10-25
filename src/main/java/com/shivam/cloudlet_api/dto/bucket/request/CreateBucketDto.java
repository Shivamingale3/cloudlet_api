package com.shivam.cloudlet_api.dto.bucket.request;

import com.shivam.cloudlet_api.enums.BucketAccess;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBucketDto {

  private String name;
  private BucketAccess access;
  private Long storageLimit;
}
