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

package com.nosugarice.mybatis.builder.sql;

import com.nosugarice.mybatis.data.ReservedWords;
import com.nosugarice.mybatis.dialect.Dialect;
import com.nosugarice.mybatis.mapping.RelationalProperty;
import com.nosugarice.mybatis.util.StringFormatter;
import com.nosugarice.mybatis.util.StringUtils;

import java.sql.JDBCType;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/1/1
 */
public interface SqlPart {

    String LINE_SEPARATOR = System.lineSeparator();

    String SELECT = "SELECT ";
    String FROM = "FROM ";
    String AS = " AS ";
    String SET = "SET ";
    String WHERE = "WHERE";
    String AND = " AND ";
    String UPDATE = "UPDATE ";
    String DELETE = "DELETE ";
    String DOT = ".";
    String SPACE = " ";
    String EQUALS = " = ";
    String NULL = "NULL";
    String EMPTY = "";

    /**
     * 预编译占位符
     *
     * @param content
     * @return
     */
    static String placeholder(String content) {
        return "#{" + content + "}";
    }

    /**
     * 直接替换占位符
     *
     * @param content
     * @return
     */
    static String placeholderDirect(String content) {
        return "${" + content + "}";
    }

    /**
     * 占位符
     *
     * @param property
     * @return
     */
    static String placeholder(RelationalProperty property) {
        return placeholder(property.getName());
    }

    /**
     * 占位符
     *
     * @param property 列
     * @param prefix   前缀
     * @return 指定参数的
     */
    static String placeholder(RelationalProperty property, String prefix) {
        return placeholder(property.getName(), prefix, property.getColumn().getJdbcType(), property.getTypeHandler());
    }

    /**
     * 占位符
     *
     * @param property        列
     * @param prefix          前缀
     * @param jdbcType        jdbcType
     * @param typeHandlerType typeHandler类型
     * @return 指定参数的
     */
    static String placeholder(String property, String prefix, Integer jdbcType, Class<?> typeHandlerType) {
        return placeholder(property, prefix, assignJdbcType(jdbcType), assignTypeHandler(typeHandlerType));
    }

    /**
     * 占位符
     *
     * @param property          列
     * @param prefix            前缀
     * @param assignJdbcType    指定jdbcType
     * @param assignTypeHandler 指定typeHandler类
     * @return 指定参数的
     */
    static String placeholder(String property, String prefix, String assignJdbcType, String assignTypeHandler) {
        String prefixDot = StringUtils.isNotBlank(prefix) ? prefix + DOT : EMPTY;
        assignJdbcType = StringUtils.isBlank(assignJdbcType) ? EMPTY : "," + assignJdbcType;
        assignTypeHandler = StringUtils.isBlank(assignTypeHandler) ? EMPTY : "," + assignTypeHandler;
        return placeholder(prefixDot + property + assignJdbcType + assignTypeHandler);
    }

    /**
     * jdbcType
     *
     * @param jdbcType
     * @return
     */
    static String assignJdbcType(Integer jdbcType) {
        return Optional.ofNullable(jdbcType)
                .map(JDBCType::valueOf)
                .map(jdbcTypeEnum -> "jdbcType=" + jdbcTypeEnum.getName())
                .orElse("");
    }

    /**
     * typeHandlerType
     *
     * @param typeHandlerType
     * @return
     */
    static String assignTypeHandler(Class<?> typeHandlerType) {
        return Optional.ofNullable(typeHandlerType)
                .map(handlerClass -> "typeHandler=" + handlerClass.getName())
                .orElse("");
    }

    /**
     * if 标签,非空和非空字符判断
     *
     * @param parameterName   参数名称
     * @param ignoreEmptyChar 是否忽略空字符
     * @param tagContent      标签内容
     * @return
     */
    static String tagNotNull(String parameterName, boolean ignoreEmptyChar, String tagContent) {
        return tagNotNull(parameterName, ignoreEmptyChar, tagContent, true);
    }

    /**
     * if 标签,非空和非空字符判断
     *
     * @param parameterName   参数名称
     * @param ignoreEmptyChar 是否忽略空字符
     * @param tagContent      标签内容
     * @return
     */
    static String tagNotNull(String parameterName, boolean ignoreEmptyChar, String tagContent, boolean newline) {
        String newlineStr = newline ? LINE_SEPARATOR : EMPTY;
        return "<if test=\"" + parameterName + " != null " + (ignoreEmptyChar ? "and " + parameterName + " != '' " : "") +
                "\">" + newlineStr +
                (tagContent.endsWith(LINE_SEPARATOR) ? tagContent : tagContent + newlineStr) +
                "</if>" + LINE_SEPARATOR;
    }

    /**
     * if 标签,空判断
     *
     * @param name    参数名称
     * @param content 标签内容
     * @return
     */
    static String tagNull(String name, String content) {
        return StringFormatter.format("<if test=\"{} != null\">\n{}</if>\n", name
                , content.endsWith(LINE_SEPARATOR) ? content : content + LINE_SEPARATOR);
    }

    /**
     * if 标签
     *
     * @param name    参数名称
     * @param content 标签内容
     * @return
     */
    static String tagIf(String name, String content) {
        return StringFormatter.format("<if test=\"{}\">\n{}</if>\n", name
                , content.endsWith(LINE_SEPARATOR) ? content : content + LINE_SEPARATOR);
    }

    /**
     * trim 标签
     *
     * @param tagContent      标签内容
     * @param prefix
     * @param prefixOverrides
     * @param suffix
     * @param suffixOverrides
     * @return
     */
    static String tagTrim(String tagContent, String prefix, String prefixOverrides, String suffix, String suffixOverrides) {
        return "<trim " +
                (StringUtils.isNotBlank(prefix) ? "prefix=\"" + prefix + "\"" + SPACE : EMPTY) +
                (StringUtils.isNotBlank(prefixOverrides) ? "prefixOverrides=\"" + prefixOverrides + "\"" + SPACE : EMPTY) +
                (StringUtils.isNotBlank(suffix) ? "suffix=\"" + suffix + "\"" + SPACE : EMPTY) +
                (StringUtils.isNotBlank(suffixOverrides) ? "suffixOverrides=\"" + suffixOverrides + "\"" + SPACE : EMPTY) +
                ">" + LINE_SEPARATOR +
                tagContent +
                "</trim>" +
                LINE_SEPARATOR;
    }

    /**
     * bind 标签
     *
     * @param name  名称
     * @param value 值
     * @return
     */
    static String tagBind(String name, String value) {
        return StringFormatter.format("<bind name=\"{}\" value=\"{}\" />", name, value);
    }

    /**
     * 当有数据库关键字的时候加上
     *
     * @param relationalProperty
     * @param dialect
     * @return
     */
    static String safeColumnName(RelationalProperty relationalProperty, Dialect dialect) {
        return Optional.of(relationalProperty.getColumn())
                .map(column -> column.isKeyword() ? dialect.processKeywords(column.getName()) : column.getName())
                .orElse(EMPTY);
    }

    /**
     * 当有数据库关键字的时候加上
     *
     * @param column
     * @param dialect
     * @return
     */
    static String safeColumnName(String column, Dialect dialect) {
        return ReservedWords.SQL.isKeyword(column) ? dialect.processKeywords(column) : column;
    }

    interface Placeholder {

        String TABLE_NAME = "${TABLE_NAME}";
        String FROM_TABLE = "${FROM_TABLE}";
        String TABLE_NAME_ALIAS = "${TABLE_NAME_ALIAS}";
        String AS_ALIAS = "${AS_ALIAS}";
        String FROM_WITH_ALIAS = "${FROM_WITH_ALIAS}";
        String ALIAS_STATE = "${ALIAS_STATE}";

        String JPA_WHERE = "${JPA_WHERE}";

    }


}
