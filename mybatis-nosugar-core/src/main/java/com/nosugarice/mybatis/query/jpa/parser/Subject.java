package com.nosugarice.mybatis.query.jpa.parser;

import com.nosugarice.mybatis.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nosugarice.mybatis.query.jpa.parser.PartTree.QUERY_PATTERN;

/**
 * org.springframework.data.repository.query.parser.PartTree.Subject
 *
 * @author spring.data
 * @date 2020/12/19
 */
public class Subject {

    private static final String DISTINCT_PATTERN = "Distinct";
    private static final Pattern COUNT_BY_TEMPLATE = Pattern.compile("^(" + PartTree.COUNT_PATTERN + ")(\\p{Lu}.*?)??By");
    private static final Pattern EXISTS_BY_TEMPLATE = Pattern.compile("^(" + PartTree.EXISTS_PATTERN + ")(\\p{Lu}.*?)??By");
    private static final Pattern DELETE_BY_TEMPLATE = Pattern.compile("^(" + PartTree.DELETE_PATTERN + ")(\\p{Lu}.*?)??By");
    private static final Pattern LOGIC_DELETE_BY_TEMPLATE = Pattern.compile("^(" + PartTree.LOGIC_DELETE_PATTERN + ")(\\p{Lu}.*?)??By");

    private static final String LIMITING_QUERY_PATTERN = "(First|Top)(\\d*)?";
    private static final Pattern LIMITED_QUERY_TEMPLATE = Pattern
            .compile("^(" + QUERY_PATTERN + ")(" + DISTINCT_PATTERN + ")?" + LIMITING_QUERY_PATTERN + "(\\p{Lu}.*?)??By");

    private final boolean distinct;
    private final boolean count;
    private final boolean exists;
    private final boolean delete;
    private final boolean logicDelete;
    private final Integer maxResults;

    public Subject(String subject) {
        this.distinct = subject.contains(DISTINCT_PATTERN);
        this.count = matches(subject, COUNT_BY_TEMPLATE);
        this.exists = matches(subject, EXISTS_BY_TEMPLATE);
        this.delete = matches(subject, DELETE_BY_TEMPLATE);
        this.logicDelete = matches(subject, LOGIC_DELETE_BY_TEMPLATE);
        this.maxResults = returnMaxResultsIfFirstKSubjectOrNull(subject);
    }

    private Integer returnMaxResultsIfFirstKSubjectOrNull(String subject) {
        Matcher grp = LIMITED_QUERY_TEMPLATE.matcher(subject);
        if (!grp.find()) {
            return null;
        }
        String group = grp.group(4);
        return StringUtils.hasText(group) ? Integer.parseInt(group) : null;
    }

    private boolean matches(String subject, Pattern pattern) {
        return pattern.matcher(subject).find();
    }

    public boolean isDistinct() {
        return distinct;
    }

    public boolean isCount() {
        return count;
    }

    public boolean isExists() {
        return exists;
    }

    public boolean isDelete() {
        return delete;
    }

    public boolean isLogicDelete() {
        return logicDelete;
    }

    public boolean isLimiting() {
        return maxResults != null;
    }

    public Integer getMaxResults() {
        return maxResults;
    }
}