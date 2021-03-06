package com.github.zhanhb.ckfinder.connector.support;

import com.github.zhanhb.ckfinder.connector.api.Command;
import com.github.zhanhb.ckfinder.connector.api.CommandFactory;
import com.github.zhanhb.ckfinder.connector.api.EventHandler;
import com.github.zhanhb.ckfinder.connector.api.FileUploadListener;
import com.github.zhanhb.ckfinder.connector.api.PluginInfo;
import com.github.zhanhb.ckfinder.connector.api.PluginRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author zhanhb
 */
public class DefaultPluginRegistry implements PluginRegistry {

  public static DefaultPluginRegistry newInstance() {
    return new DefaultPluginRegistry();
  }

  private final List<FileUploadListener> fileUploadListeners = new ArrayList<>(4);
  private final List<PluginInfo> pluginInfos = new ArrayList<>(4);
  private final Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
  private final CommandFactoryBuilder commandFactory = new CommandFactoryBuilder().enableDefaultCommands();

  private DefaultPluginRegistry() {
  }

  @Override
  public DefaultPluginRegistry addFileUploadListener(FileUploadListener fileUploadListener) {
    Objects.requireNonNull(fileUploadListener);
    fileUploadListeners.add(fileUploadListener);
    return this;
  }

  @Override
  public PluginRegistry addPluginInfo(PluginInfo pluginInfo) {
    Objects.requireNonNull(pluginInfo);
    pluginInfos.add(pluginInfo);
    return this;
  }

  @Override
  public DefaultPluginRegistry addName(String name) {
    if (name.matches("\\s*")) {
      throw new IllegalArgumentException("name should not be empty");
    }
    names.add(name);
    return this;
  }

  @Override
  public DefaultPluginRegistry registerCommands(Command... commands) {
    commandFactory.registerCommands(commands);
    return this;
  }

  @Override
  public DefaultPluginRegistry registerCommand(String name, Command command) {
    commandFactory.registerCommand(name, command);
    return this;
  }

  public EventHandler buildEventHandler() {
    return new DefaultEventHandler(new ArrayList<>(fileUploadListeners));
  }

  public List<PluginInfo> getPluginInfos() {
    List<PluginInfo> pi = pluginInfos;
    switch (pi.size()) {
      case 0:
        return Collections.emptyList();
      case 1:
        return Collections.singletonList(pi.get(0));
      default:
        return Collections.unmodifiableList(new ArrayList<>(pi));
    }
  }

  public String getPluginNames() {
    return names.isEmpty() ? null : String.join(",", names);
  }

  public CommandFactory buildCommandFactory() {
    return commandFactory.build();
  }

}
