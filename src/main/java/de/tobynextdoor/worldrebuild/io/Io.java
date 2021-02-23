//
// Decompiled by Procyon v0.5.36
//

package de.tobynextdoor.worldrebuild.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.exists;

public class Io {

  public static final String HASH_BACKUP = "#backup";

  public static String[] list(final String world) {
    final File sourceWorld = new File(world);
    if (!sourceWorld.exists()) {
      return new String[]{"#"};
    }
    final File[] files = new File(getParentPathOf("plugins")).listFiles();
    final List<String> worlds = new ArrayList<>();
    if (files != null) {
      for (File file : files) {
        final String path = file.getAbsolutePath();
        if (file.isDirectory() && path.endsWith(HASH_BACKUP)) {
          final String[] parts = path.split("/");
          final String dirName = parts[parts.length - 1];
          final String index = dirName
            .replace(world + "_", "")
            .replace(HASH_BACKUP, "");
          final String s = "   '" + world + "' (" + index + ")";
          worlds.add(s);
        }
      }
    }
    return worlds.toArray(new String[0]);
  }

  @NotNull
  private static String getParentPathOf(String subDirectory) {
    String dirPath = "";
    final File startDir = new File(subDirectory);
    final File[] startFiles = startDir.listFiles();
    if (startFiles != null) {
      dirPath = startFiles[0].getAbsolutePath();
      while (!dirPath.split("/")[dirPath.split("/").length - 1].equals(subDirectory)) {
        dirPath = dirPath.substring(0, dirPath.length() - 1);
      }
      dirPath = dirPath.substring(0, dirPath.length() - 9);
    }
    return dirPath;
  }

  public static boolean delete(final Path path) {
    if (!exists(path)) {
      System.err.println("[WorldRebuild] Directory '" + path + "' does not exist.");
      return false;
    }

    try {
      PathUtils.deleteDirectory(path);
      System.out.println("[WorldRebuild] Deleting finished for '" + path + "'.");
      return true;
    } catch (IOException ex) {
      ex.printStackTrace();
      System.err.println("[WorldRebuild] Deleting failed for '" + path + "'.");
      return false;
    }
  }

  public static boolean copy(final Path source, final Path dest) {
    if (!Files.exists(source)) {
      System.err.println("[WorldRebuild] Can't copy  non-existing '" + source + "', to '" + dest + "'.");
      return false;
    }

    System.out.println("[WorldRebuild] Copying '" + source + "' to '" + dest + "' started.");
    try {
      FileUtils.copyDirectory(source.toFile(), dest.toFile());
      System.out.println("[WorldRebuild] Copying '" + source + "' to '" + dest + "' finished.");
      return true;
    } catch (IOException ex) {
      ex.printStackTrace();
      System.err.println("[WorldRebuild] Copying '" + source + "' to '" + dest + "' failed.");
      return false;
    }
  }

}
