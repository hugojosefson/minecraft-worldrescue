package de.tobynextdoor.worldrebuild.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Utility functions dealing with classpath resources.
 */
public class ResourceIo {
  /**
   * Returns a stream of text lines from a classpath resource, until and including when the predicate returns
   * {@code true}.
   *
   * @param classPathResource Name used in call to {@link ClassLoader#getSystemResourceAsStream(String)}.
   * @param stopAfter Which line should be the last one returned.
   * @return A {@link Stream} of {@code String}s, one for each line.
   * @throws IOException In case underlying {@code java.io} code throws.
   */
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
}
