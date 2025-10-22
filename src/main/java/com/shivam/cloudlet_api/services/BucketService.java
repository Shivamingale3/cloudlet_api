package com.shivam.cloudlet_api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shivam.cloudlet_api.entities.Bucket;
import com.shivam.cloudlet_api.repositories.BucketRepository;

@Service
public class BucketService {

  @Autowired
  private BucketRepository bucketRepository;

  public Bucket getBucketById(String bucketId) {
    return bucketRepository.findById(bucketId).orElse(null);
  }

  public List<Bucket> getAllBuckets() {
    return bucketRepository.findAll();
  }
}
