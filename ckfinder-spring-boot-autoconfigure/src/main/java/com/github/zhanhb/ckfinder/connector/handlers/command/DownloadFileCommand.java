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
import com.github.zhanhb.ckfinder.connector.handlers.parameter.DownloadFileParameter;
import com.github.zhanhb.ckfinder.connector.utils.AccessControl;
import com.github.zhanhb.ckfinder.connector.utils.FileUtils;
import com.github.zhanhb.ckfinder.download.ContentDisposition;
import com.github.zhanhb.ckfinder.download.ContentTypeResolver;
import com.github.zhanhb.ckfinder.download.PathPartial;
import java.io.IOException;
import java.nio.file.Path;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Class to handle <code>DownloadFile</code> command.
 */
public class DownloadFileCommand extends Command<DownloadFileParameter> {

  /**
   * executes the download file command. Writes file to response.
   *
   * @param param
   * @param request
   * @throws ConnectorException when something went wrong during reading file.
   * @throws java.io.IOException
   */
  @Override
  void execute(DownloadFileParameter param, HttpServletRequest request, HttpServletResponse response, IConfiguration configuration)
          throws ConnectorException, IOException {
    if (param.getType() == null) {
      throw new ConnectorException(ConnectorError.INVALID_TYPE);
    }

    if (!configuration.getAccessControl().hasPermission(param.getType().getName(),
            param.getCurrentFolder(), param.getUserRole(),
            AccessControl.FILE_VIEW)) {
      param.throwException(ConnectorError.UNAUTHORIZED);
    }

    if (!FileUtils.isFileNameValid(param.getFileName())
            || !FileUtils.isFileExtensionAllowed(param.getFileName(),
                    param.getType())) {
      param.throwException(ConnectorError.INVALID_REQUEST);
    }

    if (configuration.isDirectoryHidden(param.getCurrentFolder())) {
      param.throwException(ConnectorError.INVALID_REQUEST);
    }

    if (configuration.isFileHidden(param.getFileName())) {
      param.throwException(ConnectorError.FILE_NOT_FOUND);
    }

    Path file = getPath(param.getType().getPath(), param.getCurrentFolder(), param.getFileName());

    response.setHeader("Cache-Control", "cache, must-revalidate");
    response.setHeader("Pragma", "public");
    response.setHeader("Expires", "0");

    try {
      PartialHolder.INSTANCE.service(request, response, file);
    } catch (UncheckedConnectorException ex) {
      throw ex.getCause();
    } catch (ServletException ex) {
      throw new AssertionError(ex);
    }
  }

  /**
   * inits params for download file command.
   *
   * @param request request
   * @param configuration connector configuration
   * @return
   * @throws ConnectorException when error occurs.
   */
  @Override
  protected DownloadFileParameter popupParams(HttpServletRequest request, IConfiguration configuration)
          throws ConnectorException {
    DownloadFileParameter param = doInitParam(new DownloadFileParameter(), request, configuration);
    // problem with showing filename when dialog window appear
    param.setFileName(request.getParameter("FileName"));
    return param;
  }

  @SuppressWarnings("UtilityClassWithoutPrivateConstructor")
  private static class PartialHolder {

    static PathPartial INSTANCE;

    static {
      ContentTypeResolver contentTypeResolver = ContentTypeResolver.getDefault();
      INSTANCE = PathPartial.builder()
              .contentType(context -> {
                String mimetype = contentTypeResolver.getValue(context);
                if (mimetype == null) {
                  return "application/octet-stream";
                }
                if (mimetype.startsWith("text/") || mimetype.endsWith("/javascript") || mimetype.endsWith("/xml")) {
                  return mimetype + ";charset=UTF-8";
                }
                return mimetype;
              })
              .notFound(context -> {
                throw new UncheckedConnectorException(ConnectorError.FILE_NOT_FOUND);
              })
              .contentDisposition(ContentDisposition.attachment())
              .build();
    }

  }

}
