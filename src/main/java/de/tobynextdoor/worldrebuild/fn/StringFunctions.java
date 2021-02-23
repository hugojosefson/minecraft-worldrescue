package de.tobynextdoor.worldrebuild.fn;

import java.util.function.Predicate;

public class StringFunctions {
  public static Predicate<String> startsWith(final String s) {
    return line -> line.startsWith(s);
  }
}
