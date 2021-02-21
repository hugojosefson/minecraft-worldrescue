// 
// Decompiled by Procyon v0.5.36
// 

package de.tobynextdoor.WorldRebuild;

import java.util.Iterator;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.World;
import org.bukkit.Bukkit;

public class WorldRebuildAutosaveRunnable implements Runnable
{
    WorldRebuildCommands cmd;
    
    public WorldRebuildAutosaveRunnable() {
        this.cmd = new WorldRebuildCommands((WorldRebuild)Bukkit.getPluginManager().getPlugin("WorldRebuild"));
    }
    
    @Override
    public void run() {
        Bukkit.broadcastMessage("§f[§2WorldRebuild§f] §6Starting autosave...");
        final List<World> activeWorlds = (List<World>)Bukkit.getServer().getWorlds();
        for (final World w : activeWorlds) {
            final String[] args = { "save", w.getName(), "autosave" };
            this.cmd.saveRebuild(null, args);
        }
        Bukkit.broadcastMessage("§f[§2WorldRebuild§f] §6Finished autosave.");
    }
}
