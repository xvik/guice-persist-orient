package ru.vyarus.guice.persist.orient.finder.placeholder;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * String template utility.
 * Template format: "some string ${placeholder}". Placeholder names are case sensitive.
 *
 * @author Vyacheslav Rusakov
 * @since 22.09.2014
 */
public final class StringTemplateUtils {

    public static final char PLACEHOLDER_START = '$';
    public static final char PLACEHOLDER_LEFT_BOUND = '{';
    public static final char PLACEHOLDER_RIGHT_BOUND = '}';

    private StringTemplateUtils() {
    }

    /**
     * Analyze string for placeholders.
     *
     * @param str string to analyze
     * @return found placeholder names
     * @throws java.lang.IllegalStateException if duplicate placeholder found
     */
    public static List<String> findPlaceholders(final String str) {
        final List<String> placeholders = new ArrayList<String>();
        final char[] strArray = str.toCharArray();
        int i = 0;
        while (i < strArray.length - 1) {
            if (strArray[i] == PLACEHOLDER_START && strArray[i + 1] == PLACEHOLDER_LEFT_BOUND) {
                i = i + 2;
                final int begin = i;
                while (strArray[i] != PLACEHOLDER_RIGHT_BOUND) {
                    ++i;
                }
                final String placeholder = str.substring(begin, i++);
                Preconditions.checkState(!placeholders.contains(placeholder),
                        "Duplicate placeholder '%s' in string '%s'", placeholder, str);
                placeholders.add(placeholder);
            } else {
                ++i;
            }
        }
        return placeholders;
    }

    /**
     * Replace placeholders in string.
     *
     * @param str    string to replace placeholders
     * @param params placeholder values map
     * @return string with replaced placeholders
     * @throws java.lang.IllegalStateException if string placeholder value is null or not provided
     */
    public static String replace(final String str, final Map<String, String> params) {
        final StringBuilder sb = new StringBuilder(str.length());
        final char[] strArray = str.toCharArray();
        int i = 0;
        while (i < strArray.length - 1) {
            if (strArray[i] == PLACEHOLDER_START && strArray[i + 1] == PLACEHOLDER_LEFT_BOUND) {
                i = i + 2;
                final int begin = i;
                while (strArray[i] != PLACEHOLDER_RIGHT_BOUND) {
                    ++i;
                }
                final String placeholder = str.substring(begin, i++);
                Preconditions.checkState(params.containsKey(placeholder), "No value provided for placeholder '%s' "
                        + "in string '%s'", placeholder, str);
                final String value = params.get(placeholder).trim();
                sb.append(value);
            } else {
                sb.append(strArray[i]);
                ++i;
            }
        }
        if (i < strArray.length) {
            sb.append(strArray[i]);
        }
        return sb.toString();
    }

    /**
     * Validate string placeholders and mapped values for correctness.
     *
     * @param string template string
     * @param params provided param names
     */
    public static void validate(final String string, final List<String> params) {
        final List<String> placeholders = StringTemplateUtils.findPlaceholders(string);
        for (String param : params) {
            Preconditions.checkState(placeholders.contains(param),
                    "Placeholder '%s' not found in target string '%s'", param, string);
        }
        placeholders.removeAll(params);
        Preconditions.checkState(placeholders.isEmpty(),
                "No parameter binding defined for placeholders '%s' from string '%s'",
                Joiner.on(',').join(placeholders), string);
    }
}
