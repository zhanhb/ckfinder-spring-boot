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
package com.github.zhanhb.ckfinder.connector.configuration;

import com.github.zhanhb.ckfinder.connector.data.ResourceType;
import com.github.zhanhb.ckfinder.connector.utils.AccessControl;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

/**
 * Interface for configuration.
 */
public interface IConfiguration {

  /**
   * gets user role name sets in config.
   *
   * @return role name
   */
  public String getUserRoleName();

  /**
   * gets resources map types with names as map keys.
   *
   * @return resources map
   */
  public Map<String, ResourceType> getTypes();

  /**
   * returns license.
   *
   * @param request
   * @return license
   */
  public License getLicense(HttpServletRequest request);

  /**
   * gets image max width.
   *
   * @return max image height
   */
  public int getImgWidth();

  /**
   * get image max height.
   *
   * @return max image height
   */
  public int getImgHeight();

  /**
   * get image quality.
   *
   * @return image quality
   */
  public float getImgQuality();

  /**
   * check if connector is enabled.
   *
   * @return if connector is enabled
   */
  public boolean isEnabled();

  /**
   * check if thums are enabled.
   *
   * @return true if thums are enabled
   */
  public boolean isThumbsEnabled();

  /**
   * gets url to thumbs dir(path from baseUrl).
   *
   * @return thumbs url
   */
  public String getThumbsUrl();

  /**
   * gets path to thumbs directory.
   *
   * @return thumbs directory
   */
  public String getThumbsPath();

  /**
   * gets thumbs quality.
   *
   * @return thumbs quality
   */
  public float getThumbsQuality();

  /**
   * checks if thumbs are accessed direct.
   *
   * @return true if thumbs can be accessed directly
   */
  public boolean isThumbsDirectAccess();

  /**
   * gets max width of thumb.
   *
   * @return max width of thumb
   */
  public int getMaxThumbWidth();

  /**
   * gets max height of thumb.
   *
   * @return max height of thumb
   */
  public int getMaxThumbHeight();

  /**
   * check if dirname matches configuration hidden folder regex.
   *
   * @param dirName dir name
   * @return true if matches.
   */
  public boolean isDirectoryHidden(String dirName);

  /**
   * check if filename matches configuration hidden file regex.
   *
   * @param fileName file name
   * @return true if matches.
   */
  public boolean isFileHidden(String fileName);

  /**
   * get double extensions configuration.
   *
   * @return configuration value.
   */
  public boolean isCheckDoubleFileExtensions();

  /**
   * flag to check if force ASCII.
   *
   * @return true if force ASCII.
   */
  public boolean isForceAscii();

  /**
   * Checks if disallowed characters in file and folder names are turned on.
   *
   * @return disallowUnsafeCharacters
   */
  public boolean isDisallowUnsafeCharacters();

  /**
   * flag if check image size after resizing image.
   *
   * @return true if check.
   */
  public boolean isCheckSizeAfterScaling();

  /**
   * gets a list of plugins.
   *
   * @return list of plugins.
   */
  public String getPublicPluginNames();

  /**
   * gets events.
   *
   * @return events.
   */
  public Events getEvents();

  /**
   * gets param SecureImageUploads.
   *
   * @return true if is set
   */
  public boolean isSecureImageUploads();

  /**
   * gets html extensions.
   *
   * @return list of html extensions.
   */
  public List<String> getHtmlExtensions();

  /**
   * gets a list of default resource types.
   *
   * @return list of default resource types
   */
  public Set<String> getDefaultResourceTypes();

  /**
   *
   * @return the configuration
   */
  public AccessControl getAccessControl();

  /**
   *
   * @return the command factory
   */
  public CommandFactory getCommandFactory();

}
