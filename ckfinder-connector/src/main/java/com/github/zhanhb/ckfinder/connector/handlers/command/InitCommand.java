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
import com.github.zhanhb.ckfinder.connector.api.License;
import com.github.zhanhb.ckfinder.connector.api.PluginInfo;
import com.github.zhanhb.ckfinder.connector.api.ResourceType;
import com.github.zhanhb.ckfinder.connector.api.ThumbnailProperties;
import com.github.zhanhb.ckfinder.connector.handlers.response.Connector;
import com.github.zhanhb.ckfinder.connector.handlers.response.ConnectorInfo;
import com.github.zhanhb.ckfinder.connector.handlers.response.PluginInfos;
import com.github.zhanhb.ckfinder.connector.handlers.response.ResourceTypes;
import com.github.zhanhb.ckfinder.connector.support.CommandContext;
import com.github.zhanhb.ckfinder.connector.support.KeyGenerator;
import com.github.zhanhb.ckfinder.connector.utils.FileUtils;
import com.github.zhanhb.ckfinder.connector.utils.PathUtils;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Class to handle <code>Init</code> command.
 */
@Slf4j
public class InitCommand extends XmlCommand<String> {

  /**
   * chars taken to license key.
   */
  private static final int[] LICENSE_CHARS = {11, 0, 8, 12, 26, 2, 3, 25, 1};
  private static final int LICENSE_CHAR_NR = 5;
  private static final int MIN_LICENSE_KEY_LENGTH = 26;
  private static final char[] hexChars = "0123456789abcdef".toCharArray();

  @Override
  Connector buildConnector(String host, CommandContext cmdContext) {
    Connector.Builder rootElement = Connector.builder();
    CKFinderContext context = cmdContext.getCfCtx();
    cmdContext.setResourceType(rootElement);
    createErrorNode(rootElement, 0);
    createConnectorData(rootElement, host, context);
    createResourceTypesData(rootElement, cmdContext, context);
    createPluginsData(rootElement, context);
    return rootElement.build();
  }

  /**
   * Creates connector node in XML.
   *
   * @param rootElement root element in XML
   * @param host request head host
   * @param context connector context
   */
  private void createConnectorData(Connector.Builder rootElement, String host, CKFinderContext context) {
    ThumbnailProperties thumbnail = context.getThumbnail();
    License license = context.getLicense(host);

    // connector info
    ConnectorInfo.Builder element = ConnectorInfo.builder()
            .enabled(context.isEnabled())
            .licenseName(getLicenseName(license))
            .licenseKey(createLicenseKey(license.getKey()))
            .uploadCheckImages(!context.isCheckSizeAfterScaling())
            .imgWidth(context.getImage().getMaxWidth())
            .imgHeight(context.getImage().getMaxHeight())
            .csrfProtection(context.isCsrf())
            .thumbsEnabled(thumbnail != null)
            .plugins(context.getPublicPluginNames());
    if (thumbnail != null) {
      element.thumbsUrl(PathUtils.addSlashToEnd(thumbnail.getUrl()))
              .thumbsDirectAccess(thumbnail.isDirectAccess())
              .thumbsWidth(thumbnail.getMaxWidth())
              .thumbsHeight(thumbnail.getMaxHeight());
    }
    rootElement.result(element.build());
  }

  /**
   * checks license key.
   *
   * @param license license from configuration
   * @return license name if key is ok, or empty string if not.
   */
  private String getLicenseName(License license) {
    if (validateLicenseKey(license.getKey())) {
      int index = KeyGenerator.INSTANCE.indexOf(license.getKey().charAt(0))
              % LICENSE_CHAR_NR;
      if (index == 1 || index == 4) {
        return license.getName();
      }
    }
    return "";
  }

  /**
   * Creates license key from key in configuration.
   *
   * @param licenseKey license key from configuration
   * @return hashed license key
   */
  private String createLicenseKey(String licenseKey) {
    if (validateLicenseKey(licenseKey)) {
      StringBuilder sb = new StringBuilder(LICENSE_CHARS.length);
      for (int i : LICENSE_CHARS) {
        sb.append(licenseKey.charAt(i));
      }
      return sb.toString();
    }
    return "";
  }

  /**
   * validates license key length.
   *
   * @param licenseKey config license key
   * @return true if has correct length
   */
  private boolean validateLicenseKey(String licenseKey) {
    return licenseKey != null && licenseKey.length() >= MIN_LICENSE_KEY_LENGTH;
  }

  /**
   * Creates plugins node in XML.
   *
   * @param rootElement root element in XML
   * @param context ckfinder context
   */
  private void createPluginsData(Connector.Builder rootElement, CKFinderContext context) {
    Collection<PluginInfo> pluginInfos = context.getPluginInfos();
    if (pluginInfos != null && !pluginInfos.isEmpty()) {
      PluginInfos.Builder builder = PluginInfos.builder();
      for (PluginInfo pluginInfo : pluginInfos) {
        builder.add(pluginInfo.getName(), pluginInfo.getAttributes());
      }
      rootElement.result(builder.build());
    }
  }

  /**
   * Creates plugins node in XML.
   *
   * @param rootElement root element in XML
   * @param context ckfinder context
   */
  private void createResourceTypesData(Connector.Builder rootElement, CommandContext cmdContext, CKFinderContext context) {
    //resource types
    ResourceTypes.Builder resourceTypes = ResourceTypes.builder();
    Collection<ResourceType> types = cmdContext.typeToCollection();

    for (ResourceType resourceType : types) {
      int acl = cmdContext.getAcl(resourceType, "/");
      if ((acl & AccessControl.FOLDER_VIEW) != 0) {
        boolean hasChildren = FileUtils.hasChildren(context.getAccessControl(), "/", resourceType.getPath(), context, resourceType.getName(), cmdContext.getUserRole());
        resourceTypes.resourceType(com.github.zhanhb.ckfinder.connector.handlers.response.ResourceType.builder()
                .name(resourceType.getName())
                .acl(acl)
                .hash(randomHash(resourceType.getPath()))
                .allowedExtensions(resourceType.getAllowedExtensions())
                .deniedExtensions(resourceType.getDeniedExtensions())
                .url(PathUtils.addSlashToEnd(resourceType.getUrl()))
                .maxSize(Math.max(resourceType.getMaxSize(), 0))
                .hasChildren(hasChildren).build());
      }
    }
    rootElement.result(resourceTypes.build());
  }

  /**
   * Gets hash for folders in XML response to avoid cached responses.
   *
   * @param folder folder
   * @return hash value
   */
  private String randomHash(Path folder) {
    try {
      MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
      byte[] bytes = algorithm.digest(folder.toString().getBytes(StandardCharsets.UTF_8));
      int len = bytes.length;

      StringBuilder hexString = new StringBuilder(len << 1);

      for (byte b : bytes) {
        hexString.append(hexChars[b >> 4 & 15]).append(hexChars[b & 15]);
      }
      return hexString.substring(0, 15);
    } catch (NoSuchAlgorithmException e) {
      log.error("", e);
      return "";
    }
  }

  @Override
  protected String popupParams(HttpServletRequest request, CKFinderContext context) {
    return request.getServerName();
  }

  @Override
  String getCurrentFolder(HttpServletRequest request) {
    return null;
  }

}