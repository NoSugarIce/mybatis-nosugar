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

package com.nosugarice.mybatis.query.process;

import com.nosugarice.mybatis.query.SqlFragment;
import com.nosugarice.mybatis.sql.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author dingjingyang@foxmail.com
 * @date 2020/12/19
 */

public class Sort extends SqlFragment implements Expression<Sort> {

    private static final long serialVersionUID = -61667891363663892L;

    private final List<Order> orders = new ArrayList<>();

    public Sort(Collection<Order> orders) {
        this.orders.addAll(orders);
    }

    public Sort(Order... orders) {
        this.orders.addAll(Arrays.asList(orders));
    }

    public Sort(boolean ascending, String... columnNames) {
        append(ascending, columnNames);
    }

    public Sort append(boolean ascending, String... columnNames) {
        this.orders.addAll(Stream.of(columnNames)
                .map(columnName -> new Order(ascending, columnName))
                .collect(Collectors.toList()));
        return this;
    }

    public Sort append(Order order) {
        this.orders.add(order);
        return this;
    }

    public Sort append(Order... orders) {
        this.orders.addAll(Arrays.asList(orders));
        return this;
    }

    @Override
    public String getSql() {
        append("ORDER BY", orders.stream().map(Order::getSql).collect(Collectors.joining(",")));
        return merge();
    }

    public List<Order> getOrders() {
        return orders;
    }
}
