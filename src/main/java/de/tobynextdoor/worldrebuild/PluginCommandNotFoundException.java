package de.tobynextdoor.worldrebuild;

public class PluginCommandNotFoundException extends RuntimeException {
  public PluginCommandNotFoundException(String command) {
    super("Plugin command '" + command + "' not found.");
  }
}
