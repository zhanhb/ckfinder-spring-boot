package com.github.zhanhb.ckfinder.connector.handlers.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 *
 * @see com.github.zhanhb.ckfinder.connector.handlers.command.XmlCommand
 * @author zhanhb
 */
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "DeleteFiles")
public class DeleteFilesElement extends Result implements ConnectorChild {

  @XmlAttribute(name = "deleted")
  private int deleted;

}
