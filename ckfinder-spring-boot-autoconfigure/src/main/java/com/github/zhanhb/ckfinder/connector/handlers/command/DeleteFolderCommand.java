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

import com.github.zhanhb.ckfinder.connector.configuration.IConfiguration;
import com.github.zhanhb.ckfinder.connector.errors.ConnectorError;
import com.github.zhanhb.ckfinder.connector.errors.ConnectorException;
import com.github.zhanhb.ckfinder.connector.handlers.parameter.ErrorListXmlParameter;
import com.github.zhanhb.ckfinder.connector.handlers.response.Connector;
import com.github.zhanhb.ckfinder.connector.utils.AccessControl;
import com.github.zhanhb.ckfinder.connector.utils.FileUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to handle <code>DeleteFolder</code> command.
 */
@Slf4j
public class DeleteFolderCommand extends BaseXmlCommand<ErrorListXmlParameter> implements IPostCommand {

  public DeleteFolderCommand() {
    super(ErrorListXmlParameter::new);
  }

  @Override
  protected void createXml(Connector.Builder rootElement, ErrorListXmlParameter param, IConfiguration configuration) throws ConnectorException {
    if (param.getType() == null) {
      throw new ConnectorException(ConnectorError.INVALID_TYPE);
    }

    if (!configuration.getAccessControl().hasPermission(param.getType().getName(),
            param.getCurrentFolder(),
            param.getUserRole(),
            AccessControl.FOLDER_DELETE)) {
      param.throwException(ConnectorError.UNAUTHORIZED);
    }
    if (param.getCurrentFolder().equals("/")) {
      param.throwException(ConnectorError.INVALID_REQUEST);
    }

    if (configuration.isDirectoryHidden(param.getCurrentFolder())) {
      param.throwException(ConnectorError.INVALID_REQUEST);
    }

    Path dir = Paths.get(param.getType().getPath(), param.getCurrentFolder());

    try {
      if (!Files.isDirectory(dir)) {
        param.throwException(ConnectorError.FOLDER_NOT_FOUND);
      }

      if (FileUtils.delete(dir)) {
        Path thumbDir = Paths.get(configuration.getThumbsPath(),
                param.getType().getName(), param.getCurrentFolder());
        FileUtils.delete(thumbDir);
      } else {
        param.throwException(ConnectorError.ACCESS_DENIED);
      }
    } catch (SecurityException e) {
      log.error("", e);
      param.throwException(ConnectorError.ACCESS_DENIED);
    }

  }

}
