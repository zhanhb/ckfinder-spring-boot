/*
 * CKFinder
 * ========
 * http://cksource.com/ckfinder
 * Copyright (C) 2007-2015, CKSource - Frederico Knabben. All rights reserved.
 *
 * The software, this file and its contents are subject to the CKFinder
 * License. Please read the license.txt file before using, installing, copying,
 * modifying or distribute this file or part of its contents. The contents of
 * this file is part of the Source Code of CKFinder.
 */
package com.github.zhanhb.ckfinder.connector.handlers.command;

import com.github.zhanhb.ckfinder.connector.api.AccessControl;
import com.github.zhanhb.ckfinder.connector.api.CKFinderContext;
import com.github.zhanhb.ckfinder.connector.api.ConnectorException;
import com.github.zhanhb.ckfinder.connector.api.ErrorCode;
import com.github.zhanhb.ckfinder.connector.api.PostCommand;
import com.github.zhanhb.ckfinder.connector.api.ResourceType;
import com.github.zhanhb.ckfinder.connector.handlers.response.ConnectorElement;
import com.github.zhanhb.ckfinder.connector.handlers.response.DeleteFilesElement;
import com.github.zhanhb.ckfinder.connector.support.CommandContext;
import com.github.zhanhb.ckfinder.connector.support.ErrorListResult;
import com.github.zhanhb.ckfinder.connector.support.FileItem;
import com.github.zhanhb.ckfinder.connector.utils.FileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Class used to handle <code>DeleteFiles</code> command.
 */
@Slf4j
@PostCommand
public class DeleteFilesCommand extends FailAtEndXmlCommand<List<FileItem>> {

  /**
   * Prepares data for XML response.
   *
   * @param files the files
   * @param cmdContext command context
   * @throws ConnectorException when error occurs
   */
  @Override
  protected void createXml(List<FileItem> files, CommandContext cmdContext,
          ConnectorElement.Builder rootElement) throws ConnectorException {
    cmdContext.checkType();
    CKFinderContext context = cmdContext.getCfCtx();
    for (FileItem file : files) {
      ResourceType resource = file.getType();
      if (resource == null) {
        throw cmdContext.toException(ErrorCode.INVALID_REQUEST);
      }
      String folder = file.getFolder();
      if (!StringUtils.hasLength(folder)) {
        throw cmdContext.toException(ErrorCode.INVALID_REQUEST);
      }
      if (FileUtils.isPathNameInvalid(folder)) {
        throw cmdContext.toException(ErrorCode.INVALID_REQUEST);
      }
      if (context.isDirectoryHidden(folder)) {
        throw cmdContext.toException(ErrorCode.INVALID_REQUEST);
      }
      String name = file.getName();
      if (FileUtils.isFileNameInvalid(name)) {
        throw cmdContext.toException(ErrorCode.INVALID_REQUEST);
      }
      if (context.isFileHidden(name)) {
        throw cmdContext.toException(ErrorCode.INVALID_REQUEST);
      }
      if (!FileUtils.isFileExtensionAllowed(name, resource)) {
        throw cmdContext.toException(ErrorCode.INVALID_REQUEST);
      }
      cmdContext.checkAllPermission(resource, folder, AccessControl.FILE_DELETE);
    }

    ErrorListResult.Builder builder = ErrorListResult.builder();

    boolean addExtraNode = false;
    int success = 0;
    for (FileItem fileItem : files) {
      Path file = fileItem.toPath();

      addExtraNode = true;
      if (!Files.exists(file)) {
        builder.appendError(fileItem, ErrorCode.FILE_NOT_FOUND);
        continue;
      }

      log.debug("prepare delete file '{}'", file);
      if (FileUtils.delete(file)) {
        ++success;
        fileItem.toThumbnailPath().ifPresent(thumbFile -> {
          try {
            log.debug("prepare delete thumb file '{}'", thumbFile);
            FileUtils.delete(thumbFile);
          } catch (Exception ignore) {
            log.info("delete thumb file '{}' failed", thumbFile);
            // No errors if we are not able to delete the thumb.
          }
        });
      } else { //If access is denied, report error and try to delete rest of files.
        builder.appendError(fileItem, ErrorCode.ACCESS_DENIED);
      }
    }
    builder.ifError(ErrorCode.DELETE_FAILED).addErrorsTo(rootElement);
    if (addExtraNode) {
      rootElement.result(new DeleteFilesElement(success));
    }
  }

  /**
   * Initializes parameters for command handler.
   *
   * @param request current response object
   * @param context ckfinder context object
   * @return the parameter
   */
  @Override
  protected List<FileItem> parseParameters(HttpServletRequest request, CKFinderContext context) {
    return RequestFileHelper.getFilesList(request, context);
  }

}
