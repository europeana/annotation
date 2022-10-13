package eu.europeana.annotation.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class GeneralUtils {
  /**
   * Splitting a string into a list. It also strips whitespace from the start and end of every String in a list.
   * A null separator splits on whitespace.
   * @param concatenatedStrings
   * @param separator
   * @return
   */
  public static List<String> splitStringIntoList(String concatenatedStrings, String separator) {
      if (StringUtils.isEmpty(concatenatedStrings))
          return Collections.<String>emptyList();
      String[] array = StringUtils.splitByWholeSeparator(concatenatedStrings, separator);
      array=StringUtils.stripAll(array);
      return new ArrayList<String>(Arrays.asList(array));
  }
}
