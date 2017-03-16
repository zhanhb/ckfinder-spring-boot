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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

@Builder(builderClassName = "Builder")
@SuppressWarnings({
  "CollectionWithoutInitialCapacity",
  "ReturnOfCollectionOrArrayField",
  "FinalMethod",
  "FinalClass",
  "PublicInnerClass"
})
@Value
public class Configuration implements IConfiguration {

  private boolean enabled;
  private LicenseFactory licenseFactory;
  private int imgWidth;
  private int imgHeight;
  private float imgQuality;
  @Singular
  private Map<String, ResourceType> types;
  private boolean thumbsEnabled;
  private String thumbsUrl;
  private String thumbsPath;
  private boolean thumbsDirectAccess;
  private int maxThumbHeight;
  private int maxThumbWidth;
  private float thumbsQuality;
  @Singular
  private List<String> hiddenFolders;
  @Singular
  private List<String> hiddenFiles;
  private boolean checkDoubleFileExtensions;
  private boolean forceAscii;
  private boolean checkSizeAfterScaling;
  private String userRoleName;
  private String publicPluginNames;
  private boolean secureImageUploads;
  @Singular
  private List<String> htmlExtensions;
  @Singular
  private Set<String> defaultResourceTypes;
  private boolean disallowUnsafeCharacters;
  @NonNull
  private Events events;
  @NonNull
  private AccessControl accessControl;
  @NonNull
  private CommandFactory commandFactory;

  @Override
  public License getLicense(HttpServletRequest request) {
    return licenseFactory.getLicense(request);
  }

  public static class Builder {

    Builder() {
      imgWidth = DEFAULT_IMG_WIDTH;
      imgHeight = DEFAULT_IMG_HEIGHT;
      imgQuality = DEFAULT_IMG_QUALITY;
      thumbsUrl = "";
      thumbsPath = "";
      thumbsQuality = DEFAULT_IMG_QUALITY;
      maxThumbHeight = DEFAULT_THUMB_MAX_HEIGHT;
      maxThumbWidth = DEFAULT_THUMB_MAX_WIDTH;
      userRoleName = "";
    }

    public Builder eventsFromPlugins(Collection<? extends Plugin> plugins) {
      CommandFactoryBuilder factory = new CommandFactoryBuilder().enableDefaultCommands();
      PluginRegister register = new PluginRegister();
      for (Plugin plugin : plugins) {
        plugin.register(register);
        plugin.registerCommands(factory);
      }
      events(register.buildEvents());
      commandFactory(factory.build());
      publicPluginNames(register.getNames());
      return this;
    }
  }

}
