package com.nosugarice.mybatis.builder.query.parser;

import com.nosugarice.mybatis.util.Preconditions;
import com.nosugarice.mybatis.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * org.springframework.data.repository.query.parser.PartTree
 *
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */
public class PartTree {

    private static final String KEYWORD_TEMPLATE = "(%s)(?=(\\p{Lu}|\\P{InBASIC_LATIN}))";
    public static final String QUERY_PATTERN = "find|query";
    public static final String COUNT_PATTERN = "count";
    public static final String EXISTS_PATTERN = "exists";
    public static final String DELETE_PATTERN = "delete|remove";
    public static final Pattern PREFIX_TEMPLATE = Pattern.compile(
            "^(" + QUERY_PATTERN + "|" + COUNT_PATTERN + "|" + EXISTS_PATTERN + "|" + DELETE_PATTERN + ")((\\p{Lu}.*?))??By");

    private final Subject subject;
    private final Predicate predicate;

    public PartTree(String source) {
        Matcher matcher = PREFIX_TEMPLATE.matcher(source);
        Preconditions.checkArgument(matcher.find(), true, "当前方法不支持根据方法名构建查询");
        this.subject = new Subject(matcher.group());
        this.predicate = new Predicate(source.substring(matcher.group().length()));
    }

    private static String[] split(String text, String keyword) {
        Pattern pattern = Pattern.compile(String.format(KEYWORD_TEMPLATE, keyword));
        return pattern.split(text);
    }

    public Subject getSubject() {
        return subject;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public static class OrPart {

        private final List<Part> children;

        OrPart(String source) {
            String[] split = split(source, "And");
            this.children = Arrays.stream(split)
                    .filter(StringUtils::hasText)
                    .map(Part::new)
                    .collect(Collectors.toList());
        }

        public List<Part> getChildren() {
            return children;
        }
    }


    public static class Predicate {

        private final List<OrPart> nodes;

        public Predicate(String predicate) {
            this.nodes = Arrays.stream(split(predicate, "Or"))
                    .filter(StringUtils::hasText)
                    .map(OrPart::new)
                    .collect(Collectors.toList());

        }

        public List<OrPart> getNodes() {
            return nodes;
        }
    }

}
