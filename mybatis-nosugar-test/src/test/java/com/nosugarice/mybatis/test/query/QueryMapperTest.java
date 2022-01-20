package com.nosugarice.mybatis.test.query;

import com.nosugarice.mybatis.criteria.CriteriaBuilder;
import com.nosugarice.mybatis.criteria.select.AggFunction;
import com.nosugarice.mybatis.criteria.tocolumn.LambdaQuery;
import com.nosugarice.mybatis.criteria.where.GroupCriterion;
import com.nosugarice.mybatis.criteria.where.criterion.GroupCriterionImpl;
import com.nosugarice.mybatis.criteria.where.criterion.OperatorType;
import com.nosugarice.mybatis.domain.Page;
import com.nosugarice.mybatis.domain.PageImpl;
import com.nosugarice.mybatis.mapper.function.FunS;
import com.nosugarice.mybatis.test.BaseMapperTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
class QueryMapperTest extends BaseMapperTest {

    @Test
    void distinct() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.distinct().select(Collections.singletonList(Movie::getLocation)).equalTo(Movie::getLocation, "US");

        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(1, movies.size());
    }

    @Test
    void select() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.select(Arrays.asList(Movie::getId, Movie::getName, Movie::getLocation))
                .equalTo(Movie::getId, 0);

        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(1, movies.size());

        Movie movie = movies.get(0);
        Assertions.assertNull(movie.getLength());
        Assertions.assertNull(movie.getReleaseDate());
    }

    @Test
    void exclude() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.exclude(Arrays.asList(Movie::getLength, Movie::getReleaseDate))
                .equalTo(Movie::getId, 0);

        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(1, movies.size());

        Movie movie = movies.get(0);
        Assertions.assertNull(movie.getLength());
        Assertions.assertNull(movie.getReleaseDate());
    }

    @Test
    void expand() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.select(Collections.singletonList(Movie::getLocation))
                .expand(AggFunction.SUM, Movie::getScore, "score")
                .groupBy(Movie::getLocation);

        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(5, movies.size());
    }

    @Test
    void isNull() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.isNull(Movie::getCategory);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(1, movies.size());
    }

    @Test
    void isNotNull() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.isNotNull(Movie::getCategory);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(14, movies.size());
    }

    @Test
    void isEmpty() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.isEmpty(Movie::getCategory);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(1, movies.size());
    }

    @Test
    void isNotEmpty() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.isNotEmpty(Movie::getCategory);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(13, movies.size());
    }

    @Test
    void equalTo() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.equalTo(Movie::getName, "疯狂动物城");
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(1, movies.size());
    }

    @Test
    void notEqualTo() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.notEqualTo(Movie::getName, "疯狂动物城");
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(14, movies.size());
    }

    @Test
    void greaterThan() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.greaterThan(Movie::getLength, 200);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(1, movies.size());
    }

    @Test
    void notGreaterThan() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.notGreaterThan(Movie::getLength, 200);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(14, movies.size());
    }

    @Test
    void greaterThanOrEqual() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.greaterThanOrEqual(Movie::getLength, 201);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(1, movies.size());
    }

    @Test
    void notGreaterThanOrEqual() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.notGreaterThanOrEqual(Movie::getLength, 201);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(14, movies.size());
    }

    @Test
    void lessThan() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.lessThan(Movie::getLength, 201);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(14, movies.size());
    }

    @Test
    void notLessThan() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.notLessThan(Movie::getLength, 201);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(1, movies.size());
    }

    @Test
    void lessThanOrEqual() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.lessThanOrEqual(Movie::getLength, 201);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(15, movies.size());
    }

    @Test
    void notLessThanOrEqual() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.notLessThanOrEqual(Movie::getLength, 201);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(0, movies.size());
    }

    @Test
    void between() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.between(Movie::getLength, 200, 250);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(1, movies.size());
    }

    @Test
    void notBetween() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.notBetween(Movie::getLength, 200, 250);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(14, movies.size());
    }

    @Test
    void in() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.in(Movie::getName, new String[]{"这个杀手不太冷", "罗马假日"});
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(2, movies.size());

        LambdaQuery<Movie> query1 = CriteriaBuilder.lambdaQuery(Movie.class);
        query1.in(Movie::getName, Arrays.asList("这个杀手不太冷", "罗马假日"));
        List<Movie> movies1 = mapper.selectList(query);
        Assertions.assertEquals(2, movies1.size());
    }

    @Test
    void notIn() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.notIn(Movie::getName, new String[]{"这个杀手不太冷", "罗马假日"});
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(13, movies.size());

        LambdaQuery<Movie> query1 = CriteriaBuilder.lambdaQuery(Movie.class);
        query1.notIn(Movie::getName, Arrays.asList("这个杀手不太冷", "罗马假日"));
        List<Movie> movies1 = mapper.selectList(query);
        Assertions.assertEquals(13, movies1.size());
    }

    @Test
    void like() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.like(Movie::getName, "大话西游之大圣娶亲");
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(1, movies.size());
    }

    @Test
    void notLike() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.notLike(Movie::getName, "大话西游之大圣娶亲");
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(14, movies.size());
    }

    @Test
    void startsWith() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.startsWith(Movie::getName, "大话西游");
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(2, movies.size());
    }

    @Test
    void notStartsWith() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.notStartsWith(Movie::getName, "大话西游");
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(13, movies.size());
    }

    @Test
    void endsWith() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.endsWith(Movie::getName, "月光宝盒");
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(1, movies.size());
    }

    @Test
    void notEndsWith() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.notEndsWith(Movie::getName, "月光宝盒");
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(14, movies.size());
    }

    @Test
    void contains() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.contains(Movie::getName, "之");
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(2, movies.size());
    }

    @Test
    void notContains() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.notContains(Movie::getName, "之");
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(13, movies.size());
    }

    @Test
    void or() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.contains(Movie::getName, "之")
                .or(query.buildEqualTo(Movie::getName, "肖申克的救赎"))
                .or(query.buildEqualTo(Movie::getName, "千与千寻"))
                .or(query.buildEqualTo(Movie::getName, "这个杀手不太冷"));
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(5, movies.size());

        LambdaQuery<Movie> query1 = CriteriaBuilder.lambdaQuery(Movie.class);
        query1.contains(Movie::getName, "之")
                .or(query1.buildEqualTo(Movie::getName, "肖申克的救赎")
                        , query1.buildEqualTo(Movie::getLocation, "US")
                        , query1.buildEqualTo(Movie::getCategory, "剧情"));
        List<Movie> movies1 = mapper.selectList(query1);
        Assertions.assertEquals(3, movies1.size());

        LambdaQuery<Movie> query2 = CriteriaBuilder.lambdaQuery(Movie.class);

        GroupCriterion groupCriterion = new GroupCriterionImpl()
                .append(query2.buildEqualTo(Movie::getName, "肖申克的救赎"))
                .append(query2.buildEqualTo(Movie::getLocation, "US"))
                .append(query2.buildEqualTo(Movie::getCategory, "剧情"))
                .append(query2.buildEqualTo(Movie::getName, "星际穿越").byOr());

        query2.contains(Movie::getName, "之").or(groupCriterion);
        List<Movie> movies2 = mapper.selectList(query2);
        Assertions.assertEquals(4, movies2.size());
    }

    @Test
    void groupBy() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.select(Collections.singletonList(Movie::getLocation)).groupBy(Movie::getLocation);

        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(5, movies.size());
    }

    @Test
    void having() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.select(Collections.singletonList(Movie::getLocation)).expand(AggFunction.SUM, Movie::getScore, "score")
                .groupBy(Movie::getLocation)
                .having(AggFunction.SUM, Movie::getScore, OperatorType.GREATER_THAN, 20.0);

        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(2, movies.size());
    }

    @Test
    void orderBy() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.orderBy(Movie::getOrdinal);
        List<Movie> movies = mapper.selectList(query);

        Assertions.assertAll(
                () -> Assertions.assertEquals(15, movies.size()),
                () -> Assertions.assertEquals(1, movies.get(0).getOrdinal()),
                () -> Assertions.assertEquals(15, movies.get(14).getOrdinal())
        );
    }

    @Test
    void orderByDesc() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.orderByDesc(Movie::getOrdinal);
        List<Movie> movies = mapper.selectList(query);

        Assertions.assertAll(
                () -> Assertions.assertEquals(15, movies.size()),
                () -> Assertions.assertEquals(15, movies.get(0).getOrdinal()),
                () -> Assertions.assertEquals(1, movies.get(14).getOrdinal())
        );
    }

    @Test
    void limit() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.limit(5);
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(5, movies.size());
    }

    @Test
    void limitByPage() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.limit(new PageImpl<>(5));
        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(5, movies.size());
    }

    @Test
    void selectPageP0() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        Page<Movie> page = mapper.selectPageP0(new PageImpl<>(2, 5), mapper::findByCategoryIsNotNull);

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, page.getNumber()),
                () -> Assertions.assertEquals(3, page.getTotalPages()),
                () -> Assertions.assertEquals(14, page.getTotal()),
                () -> Assertions.assertEquals(5, page.getNumberOfElements())
        );
    }

    @Test
    void selectPageP1() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        Page<Movie> page = mapper.selectPageP1(new PageImpl<>(2, 5), mapper::findByLocation, "US");

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, page.getNumber()),
                () -> Assertions.assertEquals(2, page.getTotalPages()),
                () -> Assertions.assertEquals(8, page.getTotal()),
                () -> Assertions.assertEquals(3, page.getNumberOfElements())
        );
    }

    @Test
    void selectPageP2() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        Page<Movie> page = mapper.selectPageP2(new PageImpl<>(2, 5)
                , mapper::findByLocationAndScoreGreaterThan, "US", 9.0);

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, page.getNumber()),
                () -> Assertions.assertEquals(2, page.getTotalPages()),
                () -> Assertions.assertEquals(8, page.getTotal()),
                () -> Assertions.assertEquals(3, page.getNumberOfElements())
        );
    }

    @Test
    void selectPageP3() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        Page<Movie> page = mapper.selectPageP3(new PageImpl<>(2, 2)
                , mapper::findByLocationAndScoreGreaterThanAndCategoryContains, "US", 9.0, "情");

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, page.getNumber()),
                () -> Assertions.assertEquals(2, page.getTotalPages()),
                () -> Assertions.assertEquals(4, page.getTotal()),
                () -> Assertions.assertEquals(2, page.getNumberOfElements())
        );
    }

    @Test
    void adapterCount() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        long count = mapper.adapterCount((FunS.Param3<String, Double, String, List<Movie>>)
                mapper::findByLocationAndScoreGreaterThanAndCategoryContains, "US", 9.0, "情").longValue();
        Assertions.assertEquals(4, count);
    }

    @Test
    void countP3() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        long count = mapper.countP3(mapper::findByLocationAndScoreGreaterThanAndCategoryContains, "US", 9.0, "情");
        Assertions.assertEquals(4, count);
    }

    @Test
    void existsP3() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        Optional<Integer> optional = mapper.existsP3(mapper::findByLocationAndScoreGreaterThanAndCategoryContains, "US", 9.0, "情");
        Assertions.assertTrue(optional.isPresent());
    }

    @Override
    public String[] withScriptPath() {
        return new String[]{
                "/db/movie_schema.sql"
        };
    }

    @Override
    public Class<?>[] withMapper() {
        return new Class<?>[]{
                MovieMapper.class
        };
    }
}