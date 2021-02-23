//
// Decompiled by Procyon v0.5.36
//

package de.tobynextdoor.worldrebuild.autosave;

import de.tobynextdoor.worldrebuild.WorldRebuild;
import de.tobynextdoor.worldrebuild.commands.Commands;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;

public class AutosaveRunnable implements Runnable {
  final Commands commands;

  public AutosaveRunnable() {
    final WorldRebuild plugin = (WorldRebuild) Bukkit.getPluginManager().getPlugin("WorldRebuild");
    commands = new Commands(plugin);
  }

  @Override
  public void run() {
    Bukkit.broadcastMessage("§f[§2WorldRebuild§f] §6Starting autosave...");

    final List<World> activeWorlds = Bukkit.getServer().getWorlds();
    activeWorlds.stream()
      .map(World::getName)
      .map(name -> new String[]{"save", name, "autosave"})
      .forEach(args -> commands.saveRebuild(null, args));

    Bukkit.broadcastMessage("§f[§2WorldRebuild§f] §6Finished autosave.");
  }
}
