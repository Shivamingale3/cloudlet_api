package com.shivam.cloudlet_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.shivam.cloudlet_api.entities.ActivityLog;

@Repository
@Transactional
public interface ActivityLogRepository extends JpaRepository<ActivityLog, String> {

}
