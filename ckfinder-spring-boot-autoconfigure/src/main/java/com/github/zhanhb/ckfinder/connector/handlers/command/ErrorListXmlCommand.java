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
import com.github.zhanhb.ckfinder.connector.configuration.ParameterFactory;
import com.github.zhanhb.ckfinder.connector.errors.ConnectorError;
import com.github.zhanhb.ckfinder.connector.errors.ConnectorException;
import com.github.zhanhb.ckfinder.connector.handlers.parameter.ErrorListXmlParameter;
import com.github.zhanhb.ckfinder.connector.handlers.response.Connector;

/**
 * Base class to handle XML commands with error list.
 *
 * @param <T>
 */
public abstract class ErrorListXmlCommand<T extends ErrorListXmlParameter> extends XmlCommand<T> {

  public ErrorListXmlCommand(ParameterFactory<T> paramFactory) {
    super(paramFactory);
  }

  @Override
  @SuppressWarnings("FinalMethod")
  final Connector buildConnector(T param, IConfiguration configuration)
          throws ConnectorException {
    Connector.Builder connector = Connector.builder();
    ConnectorError error = getDataForXml(param, configuration);
    int errorNum = error != null ? error.getCode() : 0;
    if (param.getType() != null) {
      connector.resourceType(param.getType().getName());
    }
    createCurrentFolderNode(param, connector, configuration.getAccessControl());
    createErrorNode(connector, errorNum);
    param.addErrorsTo(connector);
    addRestNodes(connector, param, configuration);
    return connector.build();
  }

  /**
   * abstract method to create XML nodes for commands.
   *
   * @param rootElement XML root node
   * @param param
   * @param configuration connector configuration
   */
  protected abstract void addRestNodes(Connector.Builder rootElement, T param, IConfiguration configuration);

  /**
   * gets all necessary data to create XML response.
   *
   * @param param
   * @param configuration connector configuration
   * @return error code
   * {@link com.github.zhanhb.ckfinder.connector.errors.ConnectorError} if no
   * error occurred.
   * @throws com.github.zhanhb.ckfinder.connector.errors.ConnectorException
   */
  protected abstract ConnectorError getDataForXml(T param, IConfiguration configuration) throws ConnectorException;

}
