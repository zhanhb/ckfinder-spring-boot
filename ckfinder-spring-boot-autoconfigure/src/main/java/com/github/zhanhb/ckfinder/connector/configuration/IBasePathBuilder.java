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

/**
 *
 */
public interface IBasePathBuilder {

  /**
   * create base directory value.
   *
   * @return value of base directory
   */
  public Path getBasePath();

  /**
   * create baseURL value.
   *
   * @return value of baseURL
   */
  public String getBaseUrl();

}
