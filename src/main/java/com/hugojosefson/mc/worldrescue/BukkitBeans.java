package com.hugojosefson.mc.worldrescue;

import com.helospark.lightdi.annotation.Autowired;
import com.helospark.lightdi.annotation.Bean;
import com.hugojosefson.mc.worldrescue.fn.BukkitFunctions;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitScheduler;

public class BukkitBeans {
  @Bean
  public Server getServer() {
    return Bukkit.getServer();
  }

  @Bean
  public BukkitScheduler getScheduler() {
    return Bukkit.getScheduler();
  }

  @Bean
  @Autowired
  public FileConfiguration getConfig(Plugin plugin) {
    return plugin.getConfig();
  }

  @Bean
  @Autowired
  public PluginCommand[] getPluginCommands(PluginDescriptionFile pd) {
    return pd.getCommands().keySet()
      .stream()
      .map(BukkitFunctions::getPluginCommand).toArray(PluginCommand[]::new);
  }
}
