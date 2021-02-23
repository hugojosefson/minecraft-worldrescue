//
// Decompiled by Procyon v0.5.36
//

package de.tobynextdoor.worldrebuild;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WorldRebuildIo {
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

  public boolean delete(final String src) {
    final File srcFolder = new File(src);
    if (!srcFolder.exists()) {
      System.out.println("[WorldRebuild] Directory does not exist.");
      return false;
    }
    try {
      delFolder(srcFolder);
    } catch (IOException ex) {
    }
    System.out.println("[WorldRebuild] Deleting finished.");
    return true;
  }

  public static void delFolder(final File src) throws IOException {
    if (src.isDirectory()) {
      final String[] arr$;
      final String[] files = arr$ = src.list();
      for (final String file : arr$) {
        final File srcFile = new File(src, file);
        delFolder(srcFile);
      }
      src.delete();
    } else {
      src.delete();
    }
  }

  public boolean copy(final String source, final String dest) {
    final File sourceFolder = new File(source);
    final File destFolder = new File(dest);
    if (!sourceFolder.exists()) {
      return false;
    }
    try {
      System.out.println("[WorldRebuild] Starting to copy '" + source + "' to '" + dest + "'.");
      copyFolder(sourceFolder, destFolder);
    } catch (IOException ex) {
    }
    System.out.println("[WorldRebuild] Copying finished.");
    return true;
  }

  public static void copyFolder(final File source, final File dest) throws IOException {
    if (source.isDirectory()) {
      if (!dest.exists()) {
        dest.mkdir();
      }
      final String[] files = source.list();
      if (files.length > 0) {
        for (final String file : files) {
          final File sourceFile = new File(source, file);
          final File destFile = new File(dest, file);
          copyFolder(sourceFile, destFile);
        }
      }
    } else {
      final InputStream in = new FileInputStream(source);
      final OutputStream out = new FileOutputStream(dest);
      final byte[] buffer = new byte[1024];
      int length;
      while ((length = in.read(buffer)) > 0) {
        out.write(buffer, 0, length);
      }
      in.close();
      out.close();
    }
  }
}
