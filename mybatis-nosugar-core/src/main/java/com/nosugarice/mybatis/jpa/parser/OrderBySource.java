/*
 *    Copyright 2021 NoSugarIce
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nosugarice.mybatis.jpa.parser;

import com.nosugarice.mybatis.criteria.select.OrderByCriterion;
import com.nosugarice.mybatis.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.nosugarice.mybatis.sql.SQLConstants.ASC;

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

    private final OrderByCriterion orderByCriterion = new OrderByCriterion();

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

            this.orderByCriterion.addCriterion(propertyString, ASC.equalsIgnoreCase(directionString));
        }
    }

    public OrderByCriterion getOrderByCriterion() {
        return orderByCriterion;
    }
}
