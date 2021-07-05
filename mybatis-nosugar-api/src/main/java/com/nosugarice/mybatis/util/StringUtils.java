package com.nosugarice.mybatis.util;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * org.apache.commons.lang3.StringUtils
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/5/10
 */
public class StringUtils {

    public static final String EMPTY = "";

    private static final int STRING_BUILDER_SIZE = 256;

    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        return !isEmpty(cs);
    }

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static String join(final Iterator<?> iterator, final String separator) {

        // handle null, zero and one elements before building a buffer
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return EMPTY;
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) {
            return Objects.toString(first, "");
        }

        // two or more elements
        final StringBuilder buf = new StringBuilder(STRING_BUILDER_SIZE); // Java default is 16, probably too small
        if (first != null) {
            buf.append(first);
        }

        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    @SafeVarargs
    public static <T> String join(final T... elements) {
        return join(elements, null);
    }

    public static String join(final Object[] array, final String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    public static String join(final Object[] array, String separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = EMPTY;
        }

        // endIndex - startIndex > 0:   Len = NofStrings *(len(firstString) + len(separator))
        //           (Assuming that all Strings are roughly equally long)
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return EMPTY;
        }

        final StringBuilder buf = newStringBuilder(noOfItems);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String trim(String str, List<String> prefixOverrides, List<String> suffixesOverrides) {
        if (isBlank(str)) {
            return str;
        }
        str = str.trim();
        if (prefixOverrides != null && !prefixOverrides.isEmpty()) {
            for (String prefixOverride : prefixOverrides) {
                if (isEmpty(prefixOverride)) {
                    continue;
                }
                if (str.startsWith(prefixOverride)) {
                    str = str.replaceFirst(prefixOverride, "");
                }
            }
        }
        if (suffixesOverrides != null && !suffixesOverrides.isEmpty()) {
            for (String suffixesOverride : suffixesOverrides) {
                if (isEmpty(suffixesOverride)) {
                    continue;
                }
                if (str.endsWith(suffixesOverride)) {
                    str = str.substring(0, str.length() - suffixesOverride.length());
                }
            }
        }
        return str;
    }

    /**
     * org.springframework.util.StringUtils#hasText(java.lang.String)
     *
     * @param str
     * @return
     */
    public static boolean hasText(String str) {
        return (str != null && !str.isEmpty() && containsText(str));
    }

    /**
     * org.springframework.util.StringUtils#hasText(java.lang.String)
     *
     * @param str
     * @return
     */
    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static StringBuilder newStringBuilder(final int noOfItems) {
        return new StringBuilder(noOfItems * 16);
    }

}
