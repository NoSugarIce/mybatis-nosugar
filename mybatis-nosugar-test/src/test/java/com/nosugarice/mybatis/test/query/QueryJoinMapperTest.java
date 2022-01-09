package com.nosugarice.mybatis.test.query;

import com.nosugarice.mybatis.criteria.CriteriaBuilder;
import com.nosugarice.mybatis.criteria.Getter;
import com.nosugarice.mybatis.criteria.tocolumn.LambdaQuery;
import com.nosugarice.mybatis.test.BaseMapperTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/7/20
 */
class QueryJoinMapperTest extends BaseMapperTest {

    @Test
    void leftJoin() {
        MovieMapper mapper = getMapper(MovieMapper.class);

        LambdaQuery<Movie> query = CriteriaBuilder.lambdaQuery(Movie.class);
        query.join(query.<FilmReview, Getter<FilmReview, ?>>buildLeftJoin(FilmReview.class, FilmReview::getMovieName, Movie::getName)
                .equalTo(FilmReview::getSex, "男")
                .greaterThanOrEqual(FilmReview::getScore, 9)
        );

        List<Movie> movies = mapper.selectList(query);
        Assertions.assertEquals(2, movies.size());
    }

    @Override
    public String[] withScriptPath() {
        return new String[]{
                "/db/movie_schema.sql",
                "/db/film_review_schema.sql",
        };
    }

    @Override
    public Class<?>[] withMapper() {
        return new Class<?>[]{
                MovieMapper.class,
                FilmReviewMapper.class,
        };
    }
}