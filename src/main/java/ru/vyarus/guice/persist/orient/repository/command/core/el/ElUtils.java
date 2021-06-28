package ru.vyarus.guice.persist.orient.repository.command.core.el;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Query variables utility.
 * Template format: "some string ${name}". Variable names are case-sensitive.
 * Single variable may be used many times in query.
 *
 * @author Vyacheslav Rusakov
 * @since 22.09.2014
 */
public final class ElUtils {

    public static final char VAR_START = '$';
    public static final char VAR_LEFT_BOUND = '{';
    public static final char VAR_RIGHT_BOUND = '}';

    private ElUtils() {
    }

    /**
     * Analyze string for variables.
     *
     * @param str string to analyze
     * @return found variable names
     */
    @SuppressWarnings("checkstyle:IllegalIdentifierName")
    public static List<String> findVars(final String str) {
        final List<String> vars = new ArrayList<>();
        final char[] strArray = str.toCharArray();
        int i = 0;
        while (i < strArray.length - 1) {
            if (strArray[i] == VAR_START && strArray[i + 1] == VAR_LEFT_BOUND) {
                i = i + 2;
                final int begin = i;
                while (strArray[i] != VAR_RIGHT_BOUND) {
                    ++i;
                }
                final String var = str.substring(begin, i++);
                if (!vars.contains(var)) {
                    vars.add(var);
                }
            } else {
                ++i;
            }
        }
        return vars;
    }

    /**
     * Replace placeholders in string.
     *
     * @param str    string to replace placeholders
     * @param params placeholder values map
     * @return string with replaced placeholders
     * @throws java.lang.IllegalStateException if string placeholder value is null or not provided
     */
    @SuppressWarnings("checkstyle:IllegalIdentifierName")
    public static String replace(final String str, final Map<String, String> params) {
        final StringBuilder sb = new StringBuilder(str.length());
        final char[] strArray = str.toCharArray();
        int i = 0;
        while (i < strArray.length - 1) {
            if (strArray[i] == VAR_START && strArray[i + 1] == VAR_LEFT_BOUND) {
                i = i + 2;
                final int begin = i;
                while (strArray[i] != VAR_RIGHT_BOUND) {
                    ++i;
                }
                final String var = str.substring(begin, i++);
                Preconditions.checkState(params.containsKey(var), "No value provided for variable '%s' "
                        + "in string '%s'", var, str);
                final String value = params.get(var).trim();
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
     * Validate string variables and declared values for correctness.
     *
     * @param string template string
     * @param params provided param names
     */
    public static void validate(final String string, final List<String> params) {
        final List<String> vars = findVars(string);
        for (String param : params) {
            Preconditions.checkState(vars.contains(param),
                    "Variable '%s' not found in target string '%s'", param, string);
        }
        vars.removeAll(params);
        Preconditions.checkState(vars.isEmpty(),
                "No parameter binding defined for variables '%s' from string '%s'",
                Joiner.on(',').join(vars), string);
    }
}
