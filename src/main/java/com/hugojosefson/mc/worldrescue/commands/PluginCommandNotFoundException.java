package com.hugojosefson.mc.worldrescue.commands;

public class PluginCommandNotFoundException extends RuntimeException {
  public PluginCommandNotFoundException(String command) {
    super("Plugin command '" + command + "' not found.");
  }
}
