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
package com.github.zhanhb.ckfinder.connector.support;

import com.github.zhanhb.ckfinder.connector.api.AccessControl;
import com.github.zhanhb.ckfinder.connector.api.CommandFactory;
import com.github.zhanhb.ckfinder.connector.api.Configuration;
import com.github.zhanhb.ckfinder.connector.api.Constants;
import com.github.zhanhb.ckfinder.connector.api.EventHandler;
import com.github.zhanhb.ckfinder.connector.api.LicenseFactory;
import com.github.zhanhb.ckfinder.connector.api.ResourceType;
import com.github.zhanhb.ckfinder.connector.api.ThumbnailProperties;
import com.github.zhanhb.ckfinder.connector.utils.PathUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.experimental.Delegate;
import lombok.experimental.NonFinal;

@Builder(builderClassName = "Builder")
@SuppressWarnings({
  "CollectionWithoutInitialCapacity",
  "ReturnOfCollectionOrArrayField",
  "FinalClass",
  "PublicInnerClass"
})
@Value
public class DefaultConfiguration implements Configuration {

  private boolean enabled;
  @Delegate
  @NonNull
  private LicenseFactory licenseFactory;
  private int imgWidth;
  private int imgHeight;
  private float imgQuality;
  @Singular
  private Map<String, ResourceType> types;
  @Nullable
  private ThumbnailProperties thumbnail;
  @Singular
  private List<String> hiddenFolders;
  @Singular
  private List<String> hiddenFiles;
  private boolean checkDoubleFileExtensions;
  private boolean forceAscii;
  private boolean checkSizeAfterScaling;
  @NonNull
  private String userRoleName;
  @Nullable
  private String publicPluginNames;
  private boolean secureImageUploads;
  @Singular
  private List<String> htmlExtensions;
  @Singular
  private Set<String> defaultResourceTypes;
  private boolean disallowUnsafeCharacters;
  @Delegate
  @NonNull
  private EventHandler events;
  @Delegate
  @NonNull
  private AccessControl accessControl;
  @Delegate
  @NonNull
  private CommandFactory commandFactory;
  @NonFinal
  private transient Pattern fileHiddenPattern;
  @NonFinal
  private transient Pattern directoryHiddenPattern;

  @Override
  public boolean isDirectoryHidden(String dirName) {
    if (dirName == null || dirName.isEmpty()) {
      return false;
    }
    String dir = PathUtils.normalize(dirName);
    StringTokenizer sc = new StringTokenizer(dir, "/");
    Pattern pattern = directoryHiddenPattern;
    if (pattern == null) {
      pattern = Pattern.compile(buildHiddenFileOrFolderRegex(hiddenFolders));
      directoryHiddenPattern = pattern;
    }
    while (sc.hasMoreTokens()) {
      if (pattern.matcher(sc.nextToken()).matches()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isFileHidden(String fileName) {
    Pattern pattern = fileHiddenPattern;
    if (pattern == null) {
      pattern = Pattern.compile(buildHiddenFileOrFolderRegex(hiddenFiles));
      fileHiddenPattern = pattern;
    }
    return pattern.matcher(fileName).matches();
  }

  /**
   * get hidden folder regex pattern.
   *
   * @param hiddenList list of hidden file or files patterns.
   * @return full folder regex pattern
   */
  private String buildHiddenFileOrFolderRegex(List<String> hiddenList) {
    StringBuilder sb = new StringBuilder("(");
    for (String item : hiddenList) {
      if (sb.length() > 3) {
        sb.append("|");
      }

      sb.append("(");
      sb.append(item.replace(".", "\\.").replace("*", ".+").replace("?", "."));
      sb.append(")");
    }
    sb.append(")+");
    return sb.toString();
  }

  public static class Builder {

    Builder() {
      imgWidth = Constants.DEFAULT_IMG_WIDTH;
      imgHeight = Constants.DEFAULT_IMG_HEIGHT;
      imgQuality = Constants.DEFAULT_IMG_QUALITY;
      userRoleName = "";
    }

    public Builder eventsFromPlugins(Collection<? extends Plugin> plugins) {
      PluginRegister register = new PluginRegister();
      for (Plugin plugin : plugins) {
        plugin.regist(register);
      }
      events(register.buildEventHandler());
      commandFactory(register.buildCommandFactory());
      publicPluginNames(register.getNames());
      return this;
    }
  }

}