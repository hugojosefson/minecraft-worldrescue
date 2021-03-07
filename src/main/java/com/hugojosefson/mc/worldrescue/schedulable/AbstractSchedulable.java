package com.hugojosefson.mc.worldrescue.schedulable;

import org.bukkit.plugin.PluginDescriptionFile;

import static java.text.MessageFormat.format;
import static org.bukkit.Bukkit.broadcastMessage;

public abstract class AbstractSchedulable implements Schedulable {
  private final PluginDescriptionFile pd;

  protected AbstractSchedulable(final PluginDescriptionFile pd) {
    this.pd = pd;
  }

  public String getName() {
    return getClass().getSimpleName();
  }

  protected abstract void innerRun();

  @Override
  public void run() {
    broadcastMessage(format("§f[§{0}§f] §6Starting {1}...", pd.getName(), getName()));
    innerRun();
    broadcastMessage(format("§f[§{0}§f] §6Finished {1}.", pd.getName(), getName()));
  }
}
