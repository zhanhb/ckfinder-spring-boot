package com.github.zhanhb.ckfinder.connector.handlers.command;

import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Path;

interface CopyOrMove {

  void apply(Path source, Path target, CopyOption... options) throws IOException;

}
