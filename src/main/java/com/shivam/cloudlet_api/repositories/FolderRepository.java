package com.shivam.cloudlet_api.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.shivam.cloudlet_api.entities.Folder;
import com.shivam.cloudlet_api.entities.User;

@Repository
@Transactional
public interface FolderRepository extends JpaRepository<Folder, String> {
  List<Folder> findByOwner(User owner);

  boolean existsByNameIgnoreCaseAndOwnerAndParent(String name, User owner, Folder parent);

  List<Folder> findByOwnerAndParentAndNameContainingIgnoreCase(User owner, Folder parent, String name);
}
