package de.tobynextdoor.WorldRebuild;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;


public class WorldRebuildCli {
  public static void main(String[] args) throws IOException {
    getLinesUntil("plugin.yml", line -> line.startsWith("website:"))
      .forEach(System.out::println);
  }

  public static List<String> getLinesUntil(final String classPathResource, Predicate<String> stopAfter) throws IOException {
    final InputStream nullableStream = ClassLoader.getSystemResourceAsStream(classPathResource);
    final InputStream stream = Objects.requireNonNull(nullableStream);
    final InputStreamReader streamReader = new InputStreamReader(stream);

    try (final BufferedReader reader = new BufferedReader(streamReader)) {
      final List<String> result = new ArrayList<>();
      for (; ; ) {
        final String line = reader.readLine();
        if (line == null) break;
        result.add(line);
        if (stopAfter.test(line)) break;
      }
      return result;
    }

  }
}
