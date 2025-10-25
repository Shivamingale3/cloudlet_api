package com.shivam.cloudlet_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shivam.cloudlet_api.entities.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity, String> {

}
