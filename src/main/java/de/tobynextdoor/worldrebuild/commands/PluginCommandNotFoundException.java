package de.tobynextdoor.worldrebuild.commands;

public class PluginCommandNotFoundException extends RuntimeException {
  public PluginCommandNotFoundException(String command) {
    super("Plugin command '" + command + "' not found.");
  }
}
