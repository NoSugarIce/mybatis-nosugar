package com.nosugarice.mybatis.sql;

import com.nosugarice.mybatis.criteria.select.QueryStructure;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author dingjingyang@foxmail.com
 * @date 2023/1/7
 */
public class TableAliasSequence {

    private final AtomicInteger sequence = new AtomicInteger();

    private final Map<QueryStructure, String> querySequenceMap = new HashMap<>();

    public TableAliasSequence(QueryStructure structure) {
        alias(structure);
    }

    public String alias(QueryStructure queryStructure) {
        return querySequenceMap.computeIfAbsent(queryStructure, q -> "t" + sequence.getAndIncrement());
    }

}
