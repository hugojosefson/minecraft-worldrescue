package com.hugojosefson.mc.worldrescue.schedulable;

import com.hugojosefson.mc.worldrescue.commands.WorldRescueCommandExecutor;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;

public class Schedulables {

  public static Schedulable[] getSchedulables(final PluginDescriptionFile pd, final FileConfiguration config, final Server server, final WorldRescueCommandExecutor executor) {
    return new Schedulable[]{
      new AutoSave(executor, pd, server, config)
    };
  }

}

