package com.github.zhanhb.ckfinder.connector.api;

/**
 * Registry of plugins.
 *
 *
 * @author zhanhb
 */
public interface PluginRegistry {

  PluginRegistry addFileUploadListener(FileUploadListener fileUploadListener) ;

  PluginRegistry addPluginInfo(PluginInfo pluginInfo) ;

  PluginRegistry addName(String name);

  @SuppressWarnings("UnusedReturnValue")
  PluginRegistry registerCommands(Command... commands);

  @SuppressWarnings("unused")
  PluginRegistry registerCommand(String name, Command command);

}
