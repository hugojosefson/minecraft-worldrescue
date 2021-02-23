//
// Decompiled by Procyon v0.5.36
//

package de.tobynextdoor.worldrebuild.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.file.Files.exists;

public class Io {

  public static final String HASH_BACKUP = "#backup";

  public static Optional<String[]> listBackups(final Path world) {
    final Path worldDir = world.toAbsolutePath();
    if (!Files.exists(worldDir)) {
      return Optional.empty();
    }
    try {
      final String[] backupIndices = Files.list(worldDir)
        .filter(path -> path.toFile().isDirectory())
        .filter(path -> path.toFile().getAbsolutePath().endsWith(HASH_BACKUP))
        .map(Path::getFileName)
        .map(Path::toString)
        .map(s -> s.replace(world + "_", ""))
        .map(s -> s.replace(HASH_BACKUP, ""))
        .toArray(String[]::new);
      return Optional.of(backupIndices);
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
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
