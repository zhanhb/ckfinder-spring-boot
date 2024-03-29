package com.github.zhanhb.ckfinder.connector.support;

import com.github.zhanhb.ckfinder.connector.api.CKFinderContext;
import com.github.zhanhb.ckfinder.connector.api.ConnectorException;
import com.github.zhanhb.ckfinder.connector.api.ErrorCode;
import com.github.zhanhb.ckfinder.connector.api.ResourceType;
import com.github.zhanhb.ckfinder.connector.handlers.response.ConnectorElement;
import com.github.zhanhb.ckfinder.connector.handlers.response.CurrentFolderElement;
import com.github.zhanhb.ckfinder.connector.utils.FileUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
public class CommandContext {

  private final CKFinderContext cfCtx;
  private final String userRole;
  private final String currentFolder;
  private final ResourceType type;

  public CommandContext(CKFinderContext context, String userRole,
          String currentFolder, ResourceType type) throws ConnectorException {
    if (context.isDirectoryHidden(currentFolder)) {
      throw new ConnectorException(ErrorCode.INVALID_REQUEST);
    }

    if (currentFolder != null && type != null) {
      if (!"/".equals(currentFolder)) {
        Path currDir = type.resolve(currentFolder);
        if (!Files.isDirectory(currDir)) {
          throw new ConnectorException(ErrorCode.FOLDER_NOT_FOUND);
        }
      }
    }
    this.cfCtx = context;
    this.userRole = userRole;
    this.currentFolder = currentFolder;
    this.type = type;
  }

  public void setResourceType(ConnectorElement.Builder builder) {
    if (type != null) {
      builder.resourceType(type.getName());
    }
  }

  public void checkType() throws ConnectorException {
    if (type == null) {
      throw new ConnectorException(ErrorCode.INVALID_TYPE);
    }
  }

  public ConnectorException toException(ErrorCode code) {
    return new ConnectorException(code, type, currentFolder);
  }

  public Collection<ResourceType> typeToCollection() {
    if (type != null) {
      return Collections.singleton(type);
    } else {
      Set<String> defaultResourceTypes = cfCtx.getDefaultResourceTypes();
      if (!defaultResourceTypes.isEmpty()) {
        List<ResourceType> list = new ArrayList<>(defaultResourceTypes.size());
        for (String key : defaultResourceTypes) {
          ResourceType resourceType = cfCtx.getResource(key);
          if (resourceType != null) {
            list.add(resourceType);
          }
        }
        return list;
      } else {
        return cfCtx.getResources();
      }
    }
  }

  public int getAcl(ResourceType resourceType, String path) {
    return cfCtx.getAccessControl().getAcl(resourceType.getName(), path, userRole);
  }

  private boolean hasAllPermission(ResourceType resourceType, String path, int acl) {
    return (getAcl(resourceType, path) & acl) == acl;
  }

  public int getAcl() {
    return getAcl(type, currentFolder);
  }

  public void checkAllPermission(ResourceType type, String currentFolder, int acl)
          throws ConnectorException {
    if (!hasAllPermission(type, currentFolder, acl)) {
      throw toException(ErrorCode.UNAUTHORIZED);
    }
  }

  public void checkAllPermission(int acl) throws ConnectorException {
    checkAllPermission(type, currentFolder, acl);
  }

  public Path resolve(String fileName) {
    return type.resolve(currentFolder, fileName);
  }

  public Path toPath() {
    return type.resolve(currentFolder);
  }

  public Optional<Path> toThumbnail() {
    return type.resolveThumbnail(currentFolder);
  }

  public Optional<Path> resolveThumbnail(String name) {
    return type.resolveThumbnail(currentFolder, name);
  }

  public Path checkDirectory() throws ConnectorException {
    Path dir = toPath();
    if (!Files.isDirectory(dir)) {
      if ("/".equals(currentFolder)) {
        try {
          Files.createDirectories(dir);
        } catch (IOException ex) {
          throw toException(ErrorCode.FOLDER_NOT_FOUND);
        }
      } else {
        throw toException(ErrorCode.FOLDER_NOT_FOUND);
      }
    }
    return dir;
  }

  public void checkFilePostParam(Collection<FileItem> files, int requireAccess)
          throws ConnectorException {
    CKFinderContext context = cfCtx;
    for (FileItem file : files) {
      ResourceType resource = file.getType();
      if (resource == null) {
        throw toException(ErrorCode.INVALID_REQUEST);
      }
      String folder = file.getFolder();
      if (!StringUtils.hasLength(folder)) {
        throw toException(ErrorCode.INVALID_REQUEST);
      }
      if (FileUtils.isPathNameInvalid(folder)) {
        throw toException(ErrorCode.INVALID_REQUEST);
      }
      if (context.isDirectoryHidden(folder)) {
        throw toException(ErrorCode.INVALID_REQUEST);
      }
      String name = file.getName();
      if (FileUtils.isFileNameInvalid(name)) {
        throw toException(ErrorCode.INVALID_REQUEST);
      }
      if (context.isFileHidden(name)) {
        throw toException(ErrorCode.INVALID_REQUEST);
      }
      checkAllPermission(resource, folder, requireAccess);
    }
  }

  /**
   * creates <code>CurrentFolderElement</code>.
   *
   * @param builder XML root node.
   */
  public void createCurrentFolderNode(ConnectorElement.Builder builder) {
    if (type != null && currentFolder != null) {
      builder.currentFolder(CurrentFolderElement.builder()
              .path(currentFolder)
              .url(getType().getUrl() + currentFolder)
              .acl(getAcl())
              .build());
    }
  }

}
