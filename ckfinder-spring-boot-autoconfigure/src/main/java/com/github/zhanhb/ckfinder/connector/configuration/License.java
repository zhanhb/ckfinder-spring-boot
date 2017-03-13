package com.github.zhanhb.ckfinder.connector.configuration;

import lombok.Builder;
import lombok.Getter;

/**
 *
 * @author zhanhb
 */
@Builder(builderClassName = "Builder")
@Getter
public class License {

  private final String name;
  private final String key;

}