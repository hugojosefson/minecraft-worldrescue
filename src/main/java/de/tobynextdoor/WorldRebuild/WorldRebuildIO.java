//
// Decompiled by Procyon v0.5.36
//

package de.tobynextdoor.WorldRebuild;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WorldRebuildIO {
  public String[] list(final String[] welten, final String src) {
    final File srcworld = new File(src);
    if (!srcworld.exists()) {
      welten[0] = "#";
      return welten;
    }
    int g = 0;
    String dirpath = "";
    final File startdir = new File("plugins");
    final File[] startfiles = startdir.listFiles();
    if (startfiles != null) {
      for (dirpath = startfiles[0].getAbsolutePath(); !dirpath.split("/")[dirpath.split("/").length - 1].equals("plugins"); dirpath = dirpath.substring(0, dirpath.length() - 1)) {
      }
      dirpath = dirpath.substring(0, dirpath.length() - 9);
    }
    final File f = new File(dirpath);
    final File[] files = f.listFiles();
    if (files != null) {
      for (int i = 0; i < files.length; ++i) {
        if (files[i].isDirectory() && files[i].getAbsolutePath().split("#").length > 0 && files[i].getAbsolutePath().split("#")[files[i].getAbsolutePath().split("#").length - 1].equals("backup")) {
          welten[g] = "   '" + files[i].getAbsolutePath().split("/")[files[i].getAbsolutePath().split("/").length - 1].substring(0, files[i].getAbsolutePath().split("/")[files[i].getAbsolutePath().split("/").length - 1].length() - 7).split("_")[0] + "' (" + files[i].getAbsolutePath().split("/")[files[i].getAbsolutePath().split("/").length - 1].substring(0, files[i].getAbsolutePath().split("/")[files[i].getAbsolutePath().split("/").length - 1].length() - 7).split("_")[1] + ")";
          ++g;
        }
      }
    }
    return welten;
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

  public boolean copy(final String src, final String dest) {
    final File srcFolder = new File(src);
    final File destFolder = new File(dest);
    if (!srcFolder.exists()) {
      return false;
    }
    try {
      System.out.println("[WorldRebuild] Starting to copy '" + src + "' to '" + dest + "'.");
      copyFolder(srcFolder, destFolder);
    } catch (IOException ex) {
    }
    System.out.println("[WorldRebuild] Copying finished.");
    return true;
  }

  public static void copyFolder(final File src, final File dest) throws IOException {
    if (src.isDirectory()) {
      if (!dest.exists()) {
        dest.mkdir();
      }
      final String[] files = src.list();
      if (files.length > 0) {
        for (final String file : files) {
          final File srcFile = new File(src, file);
          final File destFile = new File(dest, file);
          copyFolder(srcFile, destFile);
        }
      }
    } else {
      final InputStream in = new FileInputStream(src);
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
