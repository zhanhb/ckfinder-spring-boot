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

import com.github.zhanhb.ckfinder.connector.api.Plugin;
import com.github.zhanhb.ckfinder.connector.api.PluginRegistry;

public class FileEditor implements Plugin {

  @Override
  public void register(PluginRegistry registry) {
    registry.addName("fileeditor")
            .registerCommands(new SaveFileCommand());
  }

}
