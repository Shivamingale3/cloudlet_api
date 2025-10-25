package com.shivam.cloudlet_api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shivam.cloudlet_api.dto.Response;
import com.shivam.cloudlet_api.dto.folder.CreateFolderDto;
import com.shivam.cloudlet_api.dto.folder.FolderResponseDto;
import com.shivam.cloudlet_api.dto.folder.MoveFolderDto;
import com.shivam.cloudlet_api.dto.folder.RenameFolderDto;
import com.shivam.cloudlet_api.entities.User;
import com.shivam.cloudlet_api.services.FolderService;

@RestController
@RequestMapping("api/v1/folder")
public class FolderController {

  @Autowired
  private FolderService folderService;

  @GetMapping("")
  public ResponseEntity<Response> getAllFolders(@AuthenticationPrincipal User requestingUser) {
    List<FolderResponseDto> allFolders = folderService.getAllFolders(requestingUser);
    return ResponseEntity.ok().body(new Response(HttpStatus.OK, "Success", allFolders));
  }

  @GetMapping("/by-parent-id/{parentId}")
  public ResponseEntity<Response> getFoldersByParentId(@PathVariable String parentId, @RequestParam String search,
      @AuthenticationPrincipal User requestingUser) {
    return ResponseEntity.ok().body(new Response(HttpStatus.OK, "Success", null));
  }

  @GetMapping("/{folderId}")
  public ResponseEntity<Response> getFolderById(@PathVariable String folderId,
      @AuthenticationPrincipal User requestingUser) {
    FolderResponseDto folder = folderService.getFolderById(folderId, requestingUser);
    return ResponseEntity.ok().body(new Response(HttpStatus.OK, "Success", folder));
  }

  @PostMapping("")
  public ResponseEntity<Response> createFolder(@RequestBody CreateFolderDto folderData,
      @AuthenticationPrincipal User requestingUser,
      @RequestParam(defaultValue = "false") Boolean isPublic) {
    folderService.createFolder(folderData, requestingUser.getUserId(), isPublic);
    return ResponseEntity.ok().body(new Response(HttpStatus.OK, "Success", null));
  }

  @PatchMapping("/rename/{folderId}")
  public ResponseEntity<Response> renameFolder(@PathVariable String folderId,
      @RequestBody RenameFolderDto renameFolderDto,
      @AuthenticationPrincipal User requestingUser) {
    folderService.renameFolder(folderId, renameFolderDto.getNewName(), requestingUser);
    return ResponseEntity.ok().body(new Response(HttpStatus.OK, "Success", null));
  }

  @PatchMapping("/move/{folderId}")
  public ResponseEntity<Response> moveFolder(@PathVariable String folderId, @RequestBody MoveFolderDto moveFolderDto,
      @AuthenticationPrincipal User requestingUser) {
    folderService.moveFolder(folderId, moveFolderDto.getNewParentId(), requestingUser);
    return ResponseEntity.ok().body(new Response(HttpStatus.OK, "Success", null));
  }
}
