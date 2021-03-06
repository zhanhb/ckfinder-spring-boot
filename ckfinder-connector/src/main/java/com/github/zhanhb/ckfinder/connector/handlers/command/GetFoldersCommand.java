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
import com.github.zhanhb.ckfinder.connector.handlers.response.ConnectorElement;
import com.github.zhanhb.ckfinder.connector.handlers.response.FolderElement;
import com.github.zhanhb.ckfinder.connector.handlers.response.FoldersElement;
import com.github.zhanhb.ckfinder.connector.support.CommandContext;
import com.github.zhanhb.ckfinder.connector.utils.FileUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to handle <code>GetFolders</code> command. Get subfolders for selected
 * location command.
 */
@Slf4j
public class GetFoldersCommand extends FinishOnErrorXmlCommand<Void> {

  @Override
  protected void createXml(Void param, CommandContext cmdContext,
          ConnectorElement.Builder builder) throws ConnectorException {
    cmdContext.checkType();
    cmdContext.checkAllPermission(AccessControl.FOLDER_VIEW);

    Path dir = cmdContext.toPath();

    if (!Files.isDirectory(dir)) {
      throw cmdContext.toException(ErrorCode.FOLDER_NOT_FOUND);
    }

    try {
      List<Path> directories = FileUtils.listChildren(dir, true);
      createFoldersData(builder, cmdContext, directories);
    } catch (IOException e) {
      log.error("", e);
      throw cmdContext.toException(ErrorCode.ACCESS_DENIED).initCause(e);
    }
  }

  /**
   * creates folder data node in XML document.
   *
   * @param rootElement root element in XML document
   * @param cmdContext command context
   * @param directories list of children folder
   */
  private void createFoldersData(ConnectorElement.Builder rootElement, CommandContext cmdContext, List<? extends Path> directories) {
    CKFinderContext context = cmdContext.getCfCtx();
    FoldersElement.Builder folders = FoldersElement.builder();
    for (Path dir : directories) {
      String dirName = dir.getFileName().toString();
      if (!context.getAccessControl().hasPermission(cmdContext.getType().getName(), cmdContext.getCurrentFolder() + dirName, cmdContext.getUserRole(),
              AccessControl.FOLDER_VIEW)) {
        continue;
      }
      if (context.isDirectoryHidden(dirName)) {
        continue;
      }
      boolean hasChildren = FileUtils.hasChildren(context.getAccessControl(),
              cmdContext.getCurrentFolder() + dirName + "/", dir,
              context, cmdContext.getType().getName(), cmdContext.getUserRole());

      folders.folder(FolderElement.builder()
              .name(dirName)
              .hasChildren(hasChildren)
              .acl(context.getAccessControl()
                      .getAcl(cmdContext.getType().getName(),
                              cmdContext.getCurrentFolder()
                              + dirName, cmdContext.getUserRole())).build());
    }
    rootElement.result(folders.build());
  }

  @Override
  protected Void parseParameters(HttpServletRequest request, CKFinderContext context) {
    return null;
  }

}
