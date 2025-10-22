package com.shivam.cloudlet_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.shivam.cloudlet_api.entities.Bucket;

@Repository
@Transactional
public interface BucketRepository extends JpaRepository<Bucket, String> {

}
