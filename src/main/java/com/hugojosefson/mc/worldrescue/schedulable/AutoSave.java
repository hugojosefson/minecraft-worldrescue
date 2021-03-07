package com.hugojosefson.mc.worldrescue.schedulable;

import com.helospark.lightdi.annotation.Autowired;
import com.helospark.lightdi.annotation.Component;
import com.hugojosefson.mc.worldrescue.commands.Commands;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

@Component
public class AutoSave extends AbstractSchedulable {
  private final Commands commands;
  private final Server server;
  private final FileConfiguration config;

  @Autowired
  public AutoSave(final Commands commands, final PluginDescriptionFile pd, final Server server, final FileConfiguration config) {
    super(pd);
    this.commands = commands;
    this.server = server;
    this.config = config;
  }

  @Override
  protected void innerRun() {
    server.getWorlds().stream()
      .map(World::getName)
      .forEach(worldName -> commands.save(null, worldName, getName()));
  }

  @Override
  public boolean shouldSchedule() {
    return config.getBoolean("Autosave.Enabled");
  }

  @Override
  public long getDelay() {
    return 0;
  }

  @Override
  public long getInterval() {
    return 20L * 60 * config.getInt("Autosave.Frequency (in min)");
  }
}
