package com.hugojosefson.mc.worldrescue.scheduled;

import com.hugojosefson.mc.worldrescue.Scheduled;
import com.hugojosefson.mc.worldrescue.commands.Commands;
import java.util.List;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;

import static org.bukkit.Bukkit.broadcastMessage;

public class AutoSave implements Scheduled {
  public static final String SCHEDULED_NAME = "autosave";

  private final Commands commands;
  private final PluginDescriptionFile pd;
  private final Server server;


  public AutoSave(final Commands commands, final PluginDescriptionFile pd, final Server server) {
    this.commands = commands;
    this.pd = pd;
    this.server = server;
  }

  @Override
  public void run() {
    broadcastMessage("§f[§"+ pd.getName()+"§f] §6Starting "+ SCHEDULED_NAME +"...");

    final List<World> activeWorlds = server.getWorlds();
    activeWorlds.stream()
      .map(World::getName)
      .forEach(worldName -> commands.save(null, worldName, SCHEDULED_NAME));

    broadcastMessage("§f[§"+pd.getName()+"§f] §6Finished "+ SCHEDULED_NAME +".");
  }
}
