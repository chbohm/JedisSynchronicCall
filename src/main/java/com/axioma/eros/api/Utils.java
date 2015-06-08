package com.axioma.eros.api;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Utils {

   public static String concat(final List<String> strs) {
      return StringUtils.join(strs.toArray(), ' ');
   }

}
