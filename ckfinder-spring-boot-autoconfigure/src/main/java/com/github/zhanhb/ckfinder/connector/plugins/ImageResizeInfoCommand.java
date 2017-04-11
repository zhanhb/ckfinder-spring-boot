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
package com.github.zhanhb.ckfinder.connector.plugins;

import com.github.zhanhb.ckfinder.connector.api.AccessControl;
import com.github.zhanhb.ckfinder.connector.api.Configuration;
import com.github.zhanhb.ckfinder.connector.api.ConnectorException;
import com.github.zhanhb.ckfinder.connector.api.ErrorCode;
import com.github.zhanhb.ckfinder.connector.handlers.command.BaseXmlCommand;
import com.github.zhanhb.ckfinder.connector.handlers.parameter.ImageResizeInfoParameter;
import com.github.zhanhb.ckfinder.connector.handlers.response.Connector;
import com.github.zhanhb.ckfinder.connector.handlers.response.ImageInfo;
import com.github.zhanhb.ckfinder.connector.utils.FileUtils;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageResizeInfoCommand extends BaseXmlCommand<ImageResizeInfoParameter> {

  @Override
  protected void createXml(Connector.Builder rootElement, ImageResizeInfoParameter param, Configuration configuration) throws ConnectorException {
    if (param.getType() == null) {
      throw new ConnectorException(ErrorCode.INVALID_TYPE);
    }

    if (!configuration.getAccessControl().hasPermission(param.getType().getName(),
            param.getCurrentFolder(), param.getUserRole(),
            AccessControl.FILE_VIEW)) {
      param.throwException(ErrorCode.UNAUTHORIZED);
    }

    if (param.getFileName() == null || param.getFileName().isEmpty()
            || !FileUtils.isFileNameValid(param.getFileName())
            || configuration.isFileHidden(param.getFileName())) {
      param.throwException(ErrorCode.INVALID_REQUEST);
    }

    if (!FileUtils.isFileExtensionAllowed(param.getFileName(), param.getType())) {
      param.throwException(ErrorCode.INVALID_REQUEST);
    }

    Path imageFile = getPath(param.getType().getPath(),
            param.getCurrentFolder(),
            param.getFileName());

    try {
      if (!Files.isRegularFile(imageFile)) {
        param.throwException(ErrorCode.FILE_NOT_FOUND);
      }

      BufferedImage image;
      try (InputStream is = Files.newInputStream(imageFile)) {
        image = ImageIO.read(is);
      }
      param.setImageWidth(image.getWidth());
      param.setImageHeight(image.getHeight());
    } catch (IOException e) {
      log.error("", e);
      param.throwException(ErrorCode.ACCESS_DENIED);
    }
    rootElement.result(ImageInfo.builder()
            .width(param.getImageWidth())
            .height(param.getImageHeight()).build());
  }

  @Override
  protected ImageResizeInfoParameter popupParams(HttpServletRequest request, Configuration configuration)
          throws ConnectorException {
    ImageResizeInfoParameter param = doInitParam(new ImageResizeInfoParameter(), request, configuration);
    param.setFileName(request.getParameter("fileName"));
    return param;
  }

}
