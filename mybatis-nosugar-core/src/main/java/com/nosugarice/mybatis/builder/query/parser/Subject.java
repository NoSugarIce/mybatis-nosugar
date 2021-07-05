package com.nosugarice.mybatis.builder.query.parser;

import java.util.regex.Pattern;

/**
 * org.springframework.data.repository.query.parser.PartTree.Subject
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class Subject {

    private static final String DISTINCT = "Distinct";
    private static final Pattern COUNT_BY_TEMPLATE = Pattern.compile("^(" + PartTree.COUNT_PATTERN + ")(\\p{Lu}.*?)??By");
    private static final Pattern EXISTS_BY_TEMPLATE = Pattern.compile("^(" + PartTree.EXISTS_PATTERN + ")(\\p{Lu}.*?)??By");
    private static final Pattern DELETE_BY_TEMPLATE = Pattern.compile("^(" + PartTree.DELETE_PATTERN + ")(\\p{Lu}.*?)??By");

    private final boolean distinct;
    private final boolean count;
    private final boolean exists;
    private final boolean delete;

    public Subject(String subject) {
        this.distinct = subject.contains(DISTINCT);
        this.count = matches(subject, COUNT_BY_TEMPLATE);
        this.exists = matches(subject, EXISTS_BY_TEMPLATE);
        this.delete = matches(subject, DELETE_BY_TEMPLATE);
    }

    public boolean isDelete() {
        return delete;
    }

    public boolean isCount() {
        return count;
    }

    public boolean isExists() {
        return exists;
    }

    public boolean isDistinct() {
        return distinct;
    }

    private boolean matches(String subject, Pattern pattern) {
        return pattern.matcher(subject).find();
    }
}