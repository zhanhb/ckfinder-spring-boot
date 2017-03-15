package com.github.zhanhb.ckfinder.connector.handlers.parameter;

import com.github.zhanhb.ckfinder.connector.data.FilePostParam;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author zhanhb
 */
@Getter
@Setter
@SuppressWarnings("CollectionWithoutInitialCapacity")
public class DeleteFilesParameter extends ErrorListXMLParameter {

  private List<FilePostParam> files = new ArrayList<>();
  private int filesDeleted;
  private boolean addDeleteNode;

  public void filesDeletedPlus() {
    filesDeleted++;
  }

}