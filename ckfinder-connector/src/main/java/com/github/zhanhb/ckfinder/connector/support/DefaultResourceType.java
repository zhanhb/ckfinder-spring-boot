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

import com.github.zhanhb.ckfinder.connector.api.ResourceType;
import com.github.zhanhb.ckfinder.connector.utils.FileUtils;
import java.nio.file.Path;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Resource type entity.
 */
@Builder(builderClassName = "Builder")
@SuppressWarnings("FinalClass")
@Value
public class DefaultResourceType implements ResourceType {

  /**
   * resource name.
   */
  private String name;

  /**
   * resource url.
   */
  private String url;

  /**
   * resource directory.
   */
  @NonNull
  private Path path;

  /**
   * max file size in resource.
   */
  private long maxSize;

  /**
   * resource directory.
   */
  @Nullable
  private Path thumbnailPath;

  /**
   * list of allowed extensions in resource (separated with comma).
   */
  private String allowedExtensions;

  /**
   * list of denied extensions in resource (separated with comma).
   */
  private String deniedExtensions;

  @Override
  public Path resolve(String... names) {
    return FileUtils.resolve(path, names);
  }

  @Override
  public Optional<Path> resolveThumbnail(String... names) {
    return Optional.ofNullable(thumbnailPath).map(p -> FileUtils.resolve(p, names));
  }

  @SuppressWarnings("PublicInnerClass")
  public static class Builder {

    Builder() {
      allowedExtensions = "";
      deniedExtensions = "";
    }

  }

}
