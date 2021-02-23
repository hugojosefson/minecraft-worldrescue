//
// Decompiled by Procyon v0.5.36
//

package de.tobynextdoor.worldrebuild.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.Files.exists;

public class Io {
  public String[] list(final String[] worlds, final String sourceWorldId) {
    final File sourceWorld = new File(sourceWorldId);
    if (!sourceWorld.exists()) {
      worlds[0] = "#";
      return worlds;
    }
    int g = 0;
    String dirPath = "";
    final File startDir = new File("plugins");
    final File[] startFiles = startDir.listFiles();
    if (startFiles != null) {
      for (dirPath = startFiles[0].getAbsolutePath(); !dirPath.split("/")[dirPath.split("/").length - 1].equals("plugins"); dirPath = dirPath.substring(0, dirPath.length() - 1)) {
      }
      dirPath = dirPath.substring(0, dirPath.length() - 9);
    }
    final File f = new File(dirPath);
    final File[] files = f.listFiles();
    if (files != null) {
      for (int i = 0; i < files.length; ++i) {
        if (files[i].isDirectory() && files[i].getAbsolutePath().split("#").length > 0 && files[i].getAbsolutePath().split("#")[files[i].getAbsolutePath().split("#").length - 1].equals("backup")) {
          worlds[g] = "   '" + files[i].getAbsolutePath().split("/")[files[i].getAbsolutePath().split("/").length - 1].substring(0, files[i].getAbsolutePath().split("/")[files[i].getAbsolutePath().split("/").length - 1].length() - 7).split("_")[0] + "' (" + files[i].getAbsolutePath().split("/")[files[i].getAbsolutePath().split("/").length - 1].substring(0, files[i].getAbsolutePath().split("/")[files[i].getAbsolutePath().split("/").length - 1].length() - 7).split("_")[1] + ")";
          ++g;
        }
      }
    }
    return worlds;
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
