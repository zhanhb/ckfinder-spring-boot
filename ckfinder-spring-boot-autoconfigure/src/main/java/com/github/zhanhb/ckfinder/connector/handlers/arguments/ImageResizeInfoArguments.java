package com.github.zhanhb.ckfinder.connector.handlers.arguments;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author zhanhb
 */
@Getter
@Setter
public class ImageResizeInfoArguments extends Arguments {

  private int imageWidth;
  private int imageHeight;
  private String fileName;

}
