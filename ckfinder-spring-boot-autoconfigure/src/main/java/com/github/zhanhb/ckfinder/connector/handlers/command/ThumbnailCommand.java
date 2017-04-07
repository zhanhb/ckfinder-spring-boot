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
import com.github.zhanhb.ckfinder.connector.handlers.parameter.ThumbnailParameter;
import com.github.zhanhb.ckfinder.connector.utils.AccessControl;
import com.github.zhanhb.ckfinder.connector.utils.FileUtils;
import com.github.zhanhb.ckfinder.connector.utils.ImageUtils;
import com.github.zhanhb.ckfinder.download.ContentDisposition;
import com.github.zhanhb.ckfinder.download.PathPartial;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to handle <code>Thumbnail</code> command. Get thumbnail for file
 * command.
 */
@Slf4j
public class ThumbnailCommand extends Command<ThumbnailParameter> {

  @Override
  @SuppressWarnings("FinalMethod")
  final void execute(ThumbnailParameter param, HttpServletRequest request, HttpServletResponse response, IConfiguration configuration) throws ConnectorException, IOException {
    if (configuration.getThumbnail() == null) {
      param.throwException(ConnectorError.THUMBNAILS_DISABLED);
    }

    if (param.getType() == null) {
      throw new ConnectorException(ConnectorError.INVALID_TYPE);
    }

    if (!configuration.getAccessControl().hasPermission(param.getType().getName(),
            param.getCurrentFolder(), param.getUserRole(),
            AccessControl.FILE_VIEW)) {
      param.throwException(ConnectorError.UNAUTHORIZED);
    }

    if (!FileUtils.isFileNameValid(param.getFileName())) {
      param.throwException(ConnectorError.INVALID_REQUEST);
    }

    if (configuration.isFileHidden(param.getFileName())) {
      param.throwException(ConnectorError.FILE_NOT_FOUND);
    }

    Path fullCurrentPath = getPath(param.getType().getThumbnailPath(), param.getCurrentFolder());
    log.debug("typeThumbDir: {}", fullCurrentPath);

    try {
      log.debug("ThumbnailCommand.createThumb({})", fullCurrentPath);
      Files.createDirectories(fullCurrentPath);
    } catch (IOException e) {
      throw new ConnectorException(ConnectorError.ACCESS_DENIED, e);
    }
    log.debug("", fullCurrentPath);
    Path thumbFile = getPath(fullCurrentPath, param.getFileName());
    log.debug("thumbFile: {}", thumbFile);

    if (!Files.exists(thumbFile)) {
      Path orginFile = getPath(param.getType().getPath(),
              param.getCurrentFolder(), param.getFileName());
      log.debug("orginFile: {}", orginFile);
      if (!Files.exists(orginFile)) {
        param.throwException(ConnectorError.FILE_NOT_FOUND);
      }
      try {
        boolean success = ImageUtils.createThumb(orginFile, thumbFile, configuration.getThumbnail());
        if (!success) {
          param.throwException(ConnectorError.FILE_NOT_FOUND);
        }
      } catch (IOException | ConnectorException e) {
        try {
          Files.deleteIfExists(thumbFile);
        } catch (IOException ex) {
          e.addSuppressed(ex);
        }
        throw new ConnectorException(ConnectorError.ACCESS_DENIED, e);
      }
    }

    response.setHeader("Cache-Control", "public");

    try {
      PartialHolder.INSTANCE.service(request, response, thumbFile);
    } catch (UncheckedConnectorException ex) {
      throw ex.getCause();
    } catch (ServletException ex) {
      throw new AssertionError(ex);
    } catch (IOException ex) {
      throw new ConnectorException(ConnectorError.ACCESS_DENIED, ex);
    }
  }

  @Override
  protected ThumbnailParameter popupParams(HttpServletRequest request, IConfiguration configuration)
          throws ConnectorException {
    ThumbnailParameter param = doInitParam(new ThumbnailParameter(), request, configuration);
    param.setFileName(request.getParameter("FileName"));
    return param;
  }

  @SuppressWarnings("UtilityClassWithoutPrivateConstructor")
  private static class PartialHolder {

    static PathPartial INSTANCE;

    static {
      INSTANCE = PathPartial.builder()
              .notFound(context -> {
                throw new UncheckedConnectorException(ConnectorError.FILE_NOT_FOUND);
              })
              .contentDisposition(ContentDisposition.inline())
              .build();
    }

  }

}
