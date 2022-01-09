package com.nosugarice.mybatis.util;

import java.util.Collection;
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

    public static String join(final Collection<?> collection, final String separator) {
        return join(collection.iterator(), separator);
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

    public static String trim(String str, String prefixOverride, String suffixesOverride) {
        if (isBlank(str)) {
            return str;
        }
        str = str.trim();
        if (isNotEmpty(prefixOverride)) {
            if (str.startsWith(prefixOverride)) {
                str = str.replaceFirst(prefixOverride, "");
            }
        }
        if (isNotEmpty(suffixesOverride)) {
            if (str.endsWith(suffixesOverride)) {
                str = str.substring(0, str.length() - suffixesOverride.length());
            }
        }
        return str;
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

    public static String trimParenthesis(String str) {
        if (isBlank(str)) {
            return str;
        }
        str = str.trim();
        if (str.startsWith("(") && str.endsWith(")")) {
            str = str.substring(1, str.length() - 1);
        }
        return str;
    }

    public static boolean isWrapParenthesis(String str) {
        if (isBlank(str)) {
            return false;
        }
        str = str.trim();
        return str.startsWith("(") && str.endsWith(")");
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

    public static boolean isChar(Class<?> clazz) {
        return CharSequence.class.isAssignableFrom(clazz);
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


}
