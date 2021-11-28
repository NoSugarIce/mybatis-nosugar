package com.nosugarice.mybatis.query.jpa.parser;

import com.nosugarice.mybatis.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * org.springframework.data.repository.query.parser.OrderBySource
 *
 * @author spring.data
 * @date 2021/10/24
 */
public class OrderBySource {

    private static final String BLOCK_SPLIT = "(?<=Asc|Desc)(?=\\p{Lu})";
    private static final Pattern DIRECTION_SPLIT = Pattern.compile("(.+?)(Asc|Desc)?$");
    private static final String INVALID_ORDER_SYNTAX = "Invalid order syntax for part %s!";
    private static final Set<String> DIRECTION_KEYWORDS = new HashSet<>(Arrays.asList("Asc", "Desc"));

    private final Map<String, Boolean> orderMap = new LinkedHashMap<>();

    OrderBySource(String clause) {
        if (!StringUtils.hasText(clause)) {
            return;
        }
        for (String part : clause.split(BLOCK_SPLIT)) {
            Matcher matcher = DIRECTION_SPLIT.matcher(part);

            if (!matcher.find()) {
                throw new IllegalArgumentException(String.format(INVALID_ORDER_SYNTAX, part));
            }

            String propertyString = matcher.group(1);
            String directionString = matcher.group(2);

            // No property, but only a direction keyword
            if (DIRECTION_KEYWORDS.contains(propertyString) && directionString == null) {
                throw new IllegalArgumentException(String.format(INVALID_ORDER_SYNTAX, part));
            }

            this.orderMap.put(propertyString, "ASC".equalsIgnoreCase(directionString));
        }
    }

    public Map<String, Boolean> getOrderMap() {
        return orderMap;
    }
}
