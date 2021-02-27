//
// Decompiled by Procyon v0.5.36
//

package com.hugojosefson.mc.worldrescue.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.newDirectoryStream;
import static java.util.stream.StreamSupport.stream;
import static org.apache.commons.lang.StringUtils.removeEnd;

public class Io {

  public static final String HASH_BACKUP = "#backup";
  private static final Pattern WORLD_AND_INDEX = Pattern.compile("^(.*)_([^_]+)$");

  private static class WorldAndIndex {
    final String world;
    final String index;

    private WorldAndIndex(String world, String index) {
      this.world = world;
      this.index = index;
    }
  }

  private static WorldAndIndex getNameAndIndex(final String directoryName){
    final Matcher matcher = WORLD_AND_INDEX.matcher(directoryName);
    if (!matcher.find()) throw new IllegalArgumentException("directoryName '" + directoryName + "' unexpectedly did not match " + WORLD_AND_INDEX);
    return new WorldAndIndex(matcher.group(1), matcher.group(2));
  }

  public static Optional<String[]> listBackups(final Path world) {
    final Path worldDir;
    try {
      worldDir = world.toRealPath().toAbsolutePath();
    } catch (NoSuchFileException | FileNotFoundException e) {
      return Optional.empty();
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
    if (!Files.exists(worldDir)) {
      return Optional.empty();
    }
    final Path baseDir = worldDir.getParent();
    try (final DirectoryStream<Path> directoryStream = newDirectoryStream(baseDir)) {
      final String[] backupIndices = stream(directoryStream.spliterator(), false)
        .filter(Files::isDirectory)
        .map(Path::getFileName)
        .map(Path::toString)
        .filter(directoryName -> directoryName.endsWith(HASH_BACKUP))
        .map(s -> removeEnd(s, HASH_BACKUP))
        .filter(WORLD_AND_INDEX.asPredicate())
        .map(Io::getNameAndIndex)
        .filter(nameAndIndex -> nameAndIndex.world.equals(world.getFileName().toString()))
        .map(nameAndIndex -> nameAndIndex.index)
        .toArray(String[]::new);
      return Optional.of(backupIndices);
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  public static boolean delete(final Path path) {
    if (!exists(path)) {
      System.err.println("[WorldRescue] Directory '" + path + "' does not exist.");
      return false;
    }

    try {
      PathUtils.deleteDirectory(path);
      System.out.println("[WorldRescue] Deleting finished for '" + path + "'.");
      return true;
    } catch (IOException ex) {
      ex.printStackTrace();
      System.err.println("[WorldRescue] Deleting failed for '" + path + "'.");
      return false;
    }
  }

  public static boolean copy(final Path source, final Path dest) {
    if (!Files.exists(source)) {
      System.err.println("[WorldRescue] Can't copy  non-existing '" + source + "', to '" + dest + "'.");
      return false;
    }

    System.out.println("[WorldRescue] Copying '" + source + "' to '" + dest + "' started.");
    try {
      FileUtils.copyDirectory(source.toFile(), dest.toFile());
      System.out.println("[WorldRescue] Copying '" + source + "' to '" + dest + "' finished.");
      return true;
    } catch (IOException ex) {
      ex.printStackTrace();
      System.err.println("[WorldRescue] Copying '" + source + "' to '" + dest + "' failed.");
      return false;
    }
  }

  public static long getFreeSpace() {
    return Paths.get(".").toFile().getFreeSpace();
  }

}
