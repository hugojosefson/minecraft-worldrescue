package de.tobynextdoor.WorldRebuild;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static java.util.function.Predicate.isEqual;

public class WorldRebuildCli {

  public static void main(String[] args) throws IOException {
    if (stream(args).anyMatch(isEqual("--version"))) {
      getVersion("plugin.yml").ifPresent(System.out::println);
    } else {
      getLinesUntil("plugin.yml", startsWith("website:"))
        .forEach(System.out::println);
    }
  }

  public static Optional<String> getVersion(final String classPathResource) throws IOException {
    final Stream<String> lines = getLinesUntil(classPathResource, startsWith("version:"));
    final Stream<String> versionLines = lines.filter(startsWith("version:"));
    return versionLines.findFirst()
      .map(line -> line.replaceFirst("version:", ""))
      .map(String::trim);
  }

  public static Stream<String> getLinesUntil(final String classPathResource, Predicate<String> stopAfter) throws IOException {
    final InputStream nullableStream = ClassLoader.getSystemResourceAsStream(classPathResource);
    final InputStream nonNullStream = Objects.requireNonNull(nullableStream);
    final InputStreamReader streamReader = new InputStreamReader(nonNullStream);
    try (final BufferedReader reader = new BufferedReader(streamReader)) {
      final List<String> lines = new ArrayList<>();
      for (; ; ) {
        final String line = reader.readLine();
        if (line == null) break;
        lines.add(line);
        if (stopAfter.test(line)) break;
      }
      return lines.stream();
    }

  }

  private static Predicate<String> startsWith(final String s) {
    return line -> line.startsWith(s);
  }
}
