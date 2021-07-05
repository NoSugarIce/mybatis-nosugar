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

package com.nosugarice.mybatis.data;

import com.nosugarice.mybatis.util.StringUtils;

import java.util.HashSet;
import java.util.Set;

public enum ReservedWords {

    /**
     * JAVA 语言关键字
     *
     * @see <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/_keywords.html">JAVA  reservedWords</a>
     */
    JAVA_LANGUAGE(new HashSet<>()) {
        {
            registerKeyword("abstract");
            registerKeyword("assert");
            registerKeyword("boolean");
            registerKeyword("break");
            registerKeyword("byte");
            registerKeyword("case");
            registerKeyword("catch");
            registerKeyword("char");
            registerKeyword("class");
            registerKeyword("const");
            registerKeyword("continue");
            registerKeyword("default");
            registerKeyword("do");
            registerKeyword("double");
            registerKeyword("else");
            registerKeyword("enum");
            registerKeyword("extends");
            registerKeyword("false");
            registerKeyword("final");
            registerKeyword("finally");
            registerKeyword("float");
            registerKeyword("for");
            registerKeyword("goto");
            registerKeyword("if");
            registerKeyword("implements");
            registerKeyword("import");
            registerKeyword("instanceof");
            registerKeyword("int");
            registerKeyword("interface");
            registerKeyword("long");
            registerKeyword("native");
            registerKeyword("new");
            registerKeyword("null");
            registerKeyword("package");
            registerKeyword("private");
            registerKeyword("protected");
            registerKeyword("public");
            registerKeyword("return");
            registerKeyword("short");
            registerKeyword("static");
            registerKeyword("strictfp");
            registerKeyword("super");
            registerKeyword("switch");
            registerKeyword("synchronized");
            registerKeyword("this");
            registerKeyword("throw");
            registerKeyword("throws");
            registerKeyword("transient");
            registerKeyword("true");
            registerKeyword("try");
            registerKeyword("void");
            registerKeyword("volatile");
            registerKeyword("while");
        }

    },
    /**
     * SQL语言关键字
     * com.mysql.cj.jdbc.DatabaseMetaData#SQL2003_KEYWORDS
     */
    SQL(new HashSet<>()) {
        {
            registerKeyword("ABS");
            registerKeyword("ALL");
            registerKeyword("ALLOCATE");
            registerKeyword("ALTER");
            registerKeyword("AND");
            registerKeyword("ANY");
            registerKeyword("ARE");
            registerKeyword("ARRAY");
            registerKeyword("AS");
            registerKeyword("ASENSITIVE");
            registerKeyword("ASYMMETRIC");
            registerKeyword("AT");
            registerKeyword("ATOMIC");
            registerKeyword("AUTHORIZATION");
            registerKeyword("AVG");
            registerKeyword("BEGIN");
            registerKeyword("BETWEEN");
            registerKeyword("BIGINT");
            registerKeyword("BINARY");
            registerKeyword("BLOB");
            registerKeyword("BOOLEAN");
            registerKeyword("BOTH");
            registerKeyword("BY");
            registerKeyword("CALL");
            registerKeyword("CALLED");
            registerKeyword("CARDINALITY");
            registerKeyword("CASCADED");
            registerKeyword("CASE");
            registerKeyword("CAST");
            registerKeyword("CEIL");
            registerKeyword("CEILING");
            registerKeyword("CHAR");
            registerKeyword("CHARACTER");
            registerKeyword("CHARACTER_LENGTH");
            registerKeyword("CHAR_LENGTH");
            registerKeyword("CHECK");
            registerKeyword("CLOB");
            registerKeyword("CLOSE");
            registerKeyword("COALESCE");
            registerKeyword("COLLATE");
            registerKeyword("COLLECT");
            registerKeyword("COLUMN");
            registerKeyword("COMMIT");
            registerKeyword("CONDITION");
            registerKeyword("CONNECT");
            registerKeyword("CONSTRAINT");
            registerKeyword("CONVERT");
            registerKeyword("CORR");
            registerKeyword("CORRESPONDING");
            registerKeyword("COUNT");
            registerKeyword("COVAR_POP");
            registerKeyword("COVAR_SAMP");
            registerKeyword("CREATE");
            registerKeyword("CROSS");
            registerKeyword("CUBE");
            registerKeyword("CUME_DIST");
            registerKeyword("CURRENT");
            registerKeyword("CURRENT_DATE");
            registerKeyword("CURRENT_DEFAULT_TRANSFORM_GROUP");
            registerKeyword("CURRENT_PATH");
            registerKeyword("CURRENT_ROLE");
            registerKeyword("CURRENT_TIME");
            registerKeyword("CURRENT_TIMESTAMP");
            registerKeyword("CURRENT_TRANSFORM_GROUP_FOR_TYPE");
            registerKeyword("CURRENT_USER");
            registerKeyword("CURSOR");
            registerKeyword("CYCLE");
            registerKeyword("DATE");
            registerKeyword("DAY");
            registerKeyword("DEALLOCATE");
            registerKeyword("DEC");
            registerKeyword("DECIMAL");
            registerKeyword("DECLARE");
            registerKeyword("DEFAULT");
            registerKeyword("DELETE");
            registerKeyword("DENSE_RANK");
            registerKeyword("DEREF");
            registerKeyword("DESCRIBE");
            registerKeyword("DETERMINISTIC");
            registerKeyword("DISCONNECT");
            registerKeyword("DISTINCT");
            registerKeyword("DOUBLE");
            registerKeyword("DROP");
            registerKeyword("DYNAMIC");
            registerKeyword("EACH");
            registerKeyword("ELEMENT");
            registerKeyword("ELSE");
            registerKeyword("END");
            registerKeyword("END");
            registerKeyword("ESCAPE");
            registerKeyword("EVERY");
            registerKeyword("EXCEPT");
            registerKeyword("EXEC");
            registerKeyword("EXECUTE");
            registerKeyword("EXISTS");
            registerKeyword("EXP");
            registerKeyword("EXTERNAL");
            registerKeyword("EXTRACT");
            registerKeyword("FALSE");
            registerKeyword("FETCH");
            registerKeyword("FILTER");
            registerKeyword("FLOAT");
            registerKeyword("FLOOR");
            registerKeyword("FOR");
            registerKeyword("FOREIGN");
            registerKeyword("FREE");
            registerKeyword("FROM");
            registerKeyword("FULL");
            registerKeyword("FUNCTION");
            registerKeyword("FUSION");
            registerKeyword("GET");
            registerKeyword("GLOBAL");
            registerKeyword("GRANT");
            registerKeyword("GROUP");
            registerKeyword("GROUPING");
            registerKeyword("HAVING");
            registerKeyword("HOLD");
            registerKeyword("HOUR");
            registerKeyword("IDENTITY");
            registerKeyword("IN");
            registerKeyword("INDICATOR");
            registerKeyword("INNER");
            registerKeyword("INOUT");
            registerKeyword("INSENSITIVE");
            registerKeyword("INSERT");
            registerKeyword("INT");
            registerKeyword("INTEGER");
            registerKeyword("INTERSECT");
            registerKeyword("INTERSECTION");
            registerKeyword("INTERVAL");
            registerKeyword("INTO");
            registerKeyword("IS");
            registerKeyword("JOIN");
            registerKeyword("LANGUAGE");
            registerKeyword("LARGE");
            registerKeyword("LATERAL");
            registerKeyword("LEADING");
            registerKeyword("LEFT");
            registerKeyword("LIKE");
            registerKeyword("LN");
            registerKeyword("LOCAL");
            registerKeyword("LOCALTIME");
            registerKeyword("LOCALTIMESTAMP");
            registerKeyword("LOWER");
            registerKeyword("MATCH");
            registerKeyword("MAX");
            registerKeyword("MEMBER");
            registerKeyword("MERGE");
            registerKeyword("METHOD");
            registerKeyword("MIN");
            registerKeyword("MINUTE");
            registerKeyword("MOD");
            registerKeyword("MODIFIES");
            registerKeyword("MODULE");
            registerKeyword("MONTH");
            registerKeyword("MULTISET");
            registerKeyword("NATIONAL");
            registerKeyword("NATURAL");
            registerKeyword("NCHAR");
            registerKeyword("NCLOB");
            registerKeyword("NEW");
            registerKeyword("NO");
            registerKeyword("NONE");
            registerKeyword("NORMALIZE");
            registerKeyword("NOT");
            registerKeyword("NULL");
            registerKeyword("NULLIF");
            registerKeyword("NUMERIC");
            registerKeyword("OCTET_LENGTH");
            registerKeyword("OF");
            registerKeyword("OLD");
            registerKeyword("ON");
            registerKeyword("ONLY");
            registerKeyword("OPEN");
            registerKeyword("OR");
            registerKeyword("ORDER");
            registerKeyword("OUT");
            registerKeyword("OUTER");
            registerKeyword("OVER");
            registerKeyword("OVERLAPS");
            registerKeyword("OVERLAY");
            registerKeyword("PARAMETER");
            registerKeyword("PARTITION");
            registerKeyword("PERCENTILE_CONT");
            registerKeyword("PERCENTILE_DISC");
            registerKeyword("PERCENT_RANK");
            registerKeyword("POSITION");
            registerKeyword("POWER");
            registerKeyword("PRECISION");
            registerKeyword("PREPARE");
            registerKeyword("PRIMARY");
            registerKeyword("PROCEDURE");
            registerKeyword("RANGE");
            registerKeyword("RANK");
            registerKeyword("READS");
            registerKeyword("REAL");
            registerKeyword("RECURSIVE");
            registerKeyword("REF");
            registerKeyword("REFERENCES");
            registerKeyword("REFERENCING");
            registerKeyword("REGR_AVGX");
            registerKeyword("REGR_AVGY");
            registerKeyword("REGR_COUNT");
            registerKeyword("REGR_INTERCEPT");
            registerKeyword("REGR_R2");
            registerKeyword("REGR_SLOPE");
            registerKeyword("REGR_SXX");
            registerKeyword("REGR_SXY");
            registerKeyword("REGR_SYY");
            registerKeyword("RELEASE");
            registerKeyword("RESULT");
            registerKeyword("RETURN");
            registerKeyword("RETURNS");
            registerKeyword("REVOKE");
            registerKeyword("RIGHT");
            registerKeyword("ROLLBACK");
            registerKeyword("ROLLUP");
            registerKeyword("ROW");
            registerKeyword("ROWS");
            registerKeyword("ROW_NUMBER");
            registerKeyword("SAVEPOINT");
            registerKeyword("SCOPE");
            registerKeyword("SCROLL");
            registerKeyword("SEARCH");
            registerKeyword("SECOND");
            registerKeyword("SELECT");
            registerKeyword("SENSITIVE");
            registerKeyword("SESSION_USER");
            registerKeyword("SET");
            registerKeyword("SIMILAR");
            registerKeyword("SMALLINT");
            registerKeyword("SOME");
            registerKeyword("SPECIFIC");
            registerKeyword("SPECIFICTYPE");
            registerKeyword("SQL");
            registerKeyword("SQLEXCEPTION");
            registerKeyword("SQLSTATE");
            registerKeyword("SQLWARNING");
            registerKeyword("SQRT");
            registerKeyword("START");
            registerKeyword("STATIC");
            registerKeyword("STDDEV_POP");
            registerKeyword("STDDEV_SAMP");
            registerKeyword("SUBMULTISET");
            registerKeyword("SUBSTRING");
            registerKeyword("SUM");
            registerKeyword("SYMMETRIC");
            registerKeyword("SYSTEM");
            registerKeyword("SYSTEM_USER");
            registerKeyword("TABLE");
            registerKeyword("TABLESAMPLE");
            registerKeyword("THEN");
            registerKeyword("TIME");
            registerKeyword("TIMESTAMP");
            registerKeyword("TIMEZONE_HOUR");
            registerKeyword("TIMEZONE_MINUTE");
            registerKeyword("TO");
            registerKeyword("TRAILING");
            registerKeyword("TRANSLATE");
            registerKeyword("TRANSLATION");
            registerKeyword("TREAT");
            registerKeyword("TRIGGER");
            registerKeyword("TRIM");
            registerKeyword("TRUE");
            registerKeyword("UESCAPE");
            registerKeyword("UNION");
            registerKeyword("UNIQUE");
            registerKeyword("UNKNOWN");
            registerKeyword("UNNEST");
            registerKeyword("UPDATE");
            registerKeyword("UPPER");
            registerKeyword("USER");
            registerKeyword("USING");
            registerKeyword("VALUE");
            registerKeyword("VALUES");
            registerKeyword("VARCHAR");
            registerKeyword("VARYING");
            registerKeyword("VAR_POP");
            registerKeyword("VAR_SAMP");
            registerKeyword("WHEN");
            registerKeyword("WHENEVER");
            registerKeyword("WHERE");
            registerKeyword("WIDTH_BUCKET");
            registerKeyword("WINDOW");
            registerKeyword("WITH");
            registerKeyword("WITHIN");
            registerKeyword("WITHOUT");
            registerKeyword("YEAR");
        }
    };

    private final Set<String> keywords;

    ReservedWords(Set<String> keywords) {
        this.keywords = keywords;
    }

    public void registerKeyword(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            keywords.add(keyword.toUpperCase());
        }
    }

    public boolean isKeyword(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return false;
        }
        return keywords.contains(keyword.toUpperCase());
    }

    public Set<String> getKeywords() {
        return keywords;
    }

}
