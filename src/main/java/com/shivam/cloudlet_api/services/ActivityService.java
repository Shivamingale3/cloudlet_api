package com.shivam.cloudlet_api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shivam.cloudlet_api.entities.ActivityLog;
import com.shivam.cloudlet_api.repositories.ActivityLogRepository;

@Service
public class ActivityService {

  @Autowired
  private ActivityLogRepository activityLogRepository;

  public List<ActivityLog> getAllActivityLogs() {
    return activityLogRepository.findAll();
  }

  public ActivityLog saveActivityLog(ActivityLog activityLog) {
    return activityLogRepository.save(activityLog);
  }
}
