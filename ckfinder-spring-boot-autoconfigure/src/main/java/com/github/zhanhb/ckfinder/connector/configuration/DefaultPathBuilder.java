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

import java.nio.file.Path;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * Path builder that creates default values of base directory and baseURL.
 */
@Builder(builderClassName = "Builder")
@Getter
public class DefaultPathBuilder implements IBasePathBuilder {

  @NonNull
  private final Path basePath;
  @NonNull
  private final String baseUrl;

}
