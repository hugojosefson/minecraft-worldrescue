package de.tobynextdoor.worldrebuild;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static de.tobynextdoor.worldrebuild.fn.StringFunctions.startsWith;
import static de.tobynextdoor.worldrebuild.io.ResourceIo.getLinesUntil;
import static java.util.Arrays.stream;
import static java.util.function.Predicate.isEqual;

/**
 * Entrypoint for when you start this JAR, using {@code java -jar}, from a command line interface.
 */
public class Cli {
  public static final String PLUGIN_YML = "plugin.yml";
  public static final String WEBSITE_COLON = "website:";
  public static final String VERSION_COLON = "version:";

  public static void main(String[] args) throws IOException {
    if (stream(args).anyMatch(isEqual("--version"))) {
      final Optional<String> maybeVersion = getVersion(PLUGIN_YML);
      if (maybeVersion.isPresent()) {
        maybeVersion.ifPresent(System.out::println);
        return;
      }
    }

    getLinesUntil(PLUGIN_YML, startsWith(WEBSITE_COLON))
      .forEach(System.out::println);
  }

  /**
   * Returns the version if available, or {@link Optional#empty()} if not.
   *
   * @param yml Name of an {@code .yml} file on the classpath.
   * @return Whatever is after the {@code ":"} on the first line starting with {@code "version:"}.
   * @throws IOException In case underlying {@code java.io} code throws.
   */
  public static Optional<String> getVersion(final String yml) throws IOException {
    final Stream<String> lines = getLinesUntil(yml, startsWith(VERSION_COLON));
    final Stream<String> versionLines = lines.filter(startsWith(VERSION_COLON));
    return versionLines
      .findFirst()
      .map(line -> line.replaceFirst(VERSION_COLON, ""))
      .map(String::trim);
  }

}
