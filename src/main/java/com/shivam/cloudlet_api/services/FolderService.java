package com.shivam.cloudlet_api.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.shivam.cloudlet_api.dto.folder.CreateFolderDto;
import com.shivam.cloudlet_api.dto.folder.FolderResponseDto;
import com.shivam.cloudlet_api.entities.Folder;
import com.shivam.cloudlet_api.entities.User;
import com.shivam.cloudlet_api.enums.UserRole;
import com.shivam.cloudlet_api.exceptions.CustomException;
import com.shivam.cloudlet_api.repositories.FolderRepository;

@Service
public class FolderService {

  @Autowired
  private FolderRepository folderRepository;

  @Autowired
  private UserService userService;

  public List<FolderResponseDto> getAllFolders(User requestingUser) {
    if (requestingUser.getRole() == UserRole.ADMIN) {
      return folderRepository.findAll().stream().map(folder -> FolderResponseDto.builder()
          .folderId(folder.getFolderId())
          .name(folder.getName())
          .parentId(folder.getParent() != null ? folder.getParent().getFolderId() : null)
          .owner(folder.getOwner() != null ? userService.mapUserToDto(folder.getOwner()) : null)
          .lastModifiedBy(
              folder.getLastModifiedBy() != null ? userService.mapUserToDto(folder.getLastModifiedBy()) : null)
          .sizeInBytes(folder.getSizeInBytes())
          .shared(folder.getShared())
          .createdAt(folder.getCreatedAt())
          .updatedAt(folder.getUpdatedAt())
          .build())
          .collect(Collectors.toList());
    } else {
      return folderRepository.findByOwner(requestingUser).stream().map(folder -> FolderResponseDto.builder()
          .folderId(folder.getFolderId())
          .name(folder.getName())
          .parentId(folder.getParent() != null ? folder.getParent().getFolderId() : null)
          .owner(folder.getOwner() != null ? userService.mapUserToDto(folder.getOwner()) : null)
          .lastModifiedBy(
              folder.getLastModifiedBy() != null ? userService.mapUserToDto(folder.getLastModifiedBy()) : null)
          .sizeInBytes(folder.getSizeInBytes())
          .shared(folder.getShared())
          .createdAt(folder.getCreatedAt())
          .updatedAt(folder.getUpdatedAt())
          .build())
          .collect(Collectors.toList());
    }
  }

  public List<FolderResponseDto> getAllFoldersByParentId(String parentId, String search, User requestingUser) {

    Folder parent = null;
    if (parentId != null && !parentId.isBlank() && !parentId.isEmpty()) {
      parent = folderRepository.findById(parentId).orElse(null);
    }

    if (search == null) {
      search = "";
    }

    return folderRepository
        .findByOwnerAndParentAndNameContainingIgnoreCase(requestingUser, parent, search).stream()
        .map(folder -> FolderResponseDto.builder()
            .folderId(folder.getFolderId())
            .name(folder.getName())
            .parentId(folder.getParent() != null ? folder.getParent().getFolderId() : null)
            .owner(folder.getOwner() != null ? userService.mapUserToDto(folder.getOwner()) : null)
            .lastModifiedBy(
                folder.getLastModifiedBy() != null ? userService.mapUserToDto(folder.getLastModifiedBy()) : null)
            .sizeInBytes(folder.getSizeInBytes())
            .shared(folder.getShared())
            .createdAt(folder.getCreatedAt())
            .updatedAt(folder.getUpdatedAt())
            .build())
        .collect(Collectors.toList());
  }

  public FolderResponseDto getFolderById(String folderId, User requestingUser) {
    Folder folder = folderRepository.findById(folderId).orElse(null);
    if (folder == null) {
      return null;
    }

    if (!folder.getOwner().getUserId().equals(requestingUser.getUserId())
        && requestingUser.getRole() != UserRole.ADMIN) {
      throw new CustomException(HttpStatus.FORBIDDEN, "You are not allowed to view this folder!");
    }

    return FolderResponseDto.builder()
        .folderId(folder.getFolderId())
        .name(folder.getName())
        .parentId(folder.getParent() != null ? folder.getParent().getFolderId() : null)
        .owner(folder.getOwner() != null ? userService.mapUserToDto(folder.getOwner()) : null)
        .lastModifiedBy(
            folder.getLastModifiedBy() != null ? userService.mapUserToDto(folder.getLastModifiedBy()) : null)
        .sizeInBytes(folder.getSizeInBytes())
        .shared(folder.getShared())
        .createdAt(folder.getCreatedAt())
        .updatedAt(folder.getUpdatedAt())
        .build();
  }

  public void createFolder(CreateFolderDto folderData, String userId, Boolean shared) {
    User folderOwner = userService.findById(userId);
    if (folderOwner == null) {
      throw new CustomException(HttpStatus.NOT_FOUND, "No user found for this userId");
    }
    Folder folderToBeCreated = Folder.builder().name(folderData.getName()).owner(folderOwner)
        .lastModifiedBy(folderOwner).shared(shared).build();

    if (folderData.getParentId() != null && !folderData.getParentId().isBlank()
        && !folderData.getParentId().isEmpty()) {
      Folder parentFolder = folderRepository.findById(folderData.getParentId()).orElse(null);
      if (parentFolder == null) {
        throw new CustomException(HttpStatus.NOT_FOUND, "No folder found by this parent Id");
      }
      folderToBeCreated.setParent(parentFolder);
    }
    folderRepository.save(folderToBeCreated);
  }

  public void renameFolder(String folderId, String newName, User requestingUser) {
    Folder folder = checkFolderAccessibility(folderId, requestingUser);
    checkFolderNameAvailability(newName, requestingUser, folder.getParent());
    folder.setName(newName);
    folderRepository.save(folder);
  }

  public void moveFolder(String folderId, String newParentId, User requestingUser) {
    Folder folder = checkFolderAccessibility(folderId, requestingUser);
    Folder newParent = checkFolderAccessibility(newParentId, requestingUser);
    folder.setParent(newParent);
    folderRepository.save(folder);
  }

  public void changeFolderVisibility(String folderId, Boolean isShared, User requestingUser) {
    Folder folder = checkFolderAccessibility(folderId, requestingUser);
    folder.setShared(isShared);
    folderRepository.save(folder);
  }

  public void checkFolderNameAvailability(String newName, User requestingUser, Folder parentFolder) {
    if (folderRepository.existsByNameIgnoreCaseAndOwnerAndParent(newName, requestingUser, parentFolder)) {
      throw new CustomException(HttpStatus.BAD_REQUEST, "A folder already exists by this name in this directory!");
    }
    return;
  }

  public Folder checkFolderAccessibility(String folderId, User requestingUser) {
    Folder folder = folderRepository.findById(folderId)
        .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "No folder found for this folderId!"));
    if (!folder.getOwner().getUserId().equals(requestingUser.getUserId())
        && requestingUser.getRole() == UserRole.ADMIN) {
      throw new CustomException(HttpStatus.FORBIDDEN, "Not allowed to access this folder!");
    }
    return folder;
  }
}
