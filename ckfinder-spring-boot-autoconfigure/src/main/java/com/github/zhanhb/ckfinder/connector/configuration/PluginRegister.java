package com.github.zhanhb.ckfinder.connector.configuration;

import com.github.zhanhb.ckfinder.connector.data.FileUploadListener;
import com.github.zhanhb.ckfinder.connector.data.PluginInfoRegister;
import com.github.zhanhb.ckfinder.connector.handlers.command.Command;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 *
 * @author zhanhb
 */
public class PluginRegister {

  private final List<FileUploadListener> fileUploadListeners = new ArrayList<>(4);
  private final List<PluginInfoRegister> pluginInfoRegisters = new ArrayList<>(4);
  private final Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
  private final CommandFactoryBuilder commandFactory = new CommandFactoryBuilder().enableDefaultCommands();

  public PluginRegister addFileUploadListener(FileUploadListener fileUploadListener) {
    Objects.requireNonNull(fileUploadListener);
    fileUploadListeners.add(fileUploadListener);
    return this;
  }

  public PluginRegister addPluginInfoRegister(PluginInfoRegister pluginInfoRegister) {
    Objects.requireNonNull(pluginInfoRegister);
    pluginInfoRegisters.add(pluginInfoRegister);
    return this;
  }

  public PluginRegister addName(String name) {
    if (name.matches("\\s+")) {
      throw new IllegalArgumentException("name should not be empty");
    }
    names.add(name);
    return this;
  }

  public PluginRegister registCommands(Command<?>... commands) {
    commandFactory.registCommands(commands);
    return this;
  }

  public PluginRegister registCommand(String name, Command<?> command) {
    commandFactory.registCommand(name, command);
    return this;
  }

  Events buildEvents() {
    return new Events(new ArrayList<>(fileUploadListeners), new ArrayList<>(pluginInfoRegisters));
  }

  String getNames() {
    return names.stream().collect(Collectors.joining(","));
  }

  CommandFactory buildCommandFactory() {
    return commandFactory.build();
  }

}