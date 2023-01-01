/*
 * Copyright 2021-2023 NoSugarIce
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nosugarice.mybatis.test.query;

import com.nosugarice.mybatis.criteria.CriteriaBuilder;
import com.nosugarice.mybatis.criteria.tocolumn.Getter;
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
