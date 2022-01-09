package com.nosugarice.mybatis.test.query;

import com.nosugarice.mybatis.mapper.BaseMapper;

import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021-6-8
 */
public interface MovieMapper extends BaseMapper<Movie, Integer> {

    List<Movie> findByCategoryIsNotNull();

    List<Movie> findByLocation(String location);

    List<Movie> findByLocationAndScoreGreaterThan(String location, Double score);

    List<Movie> findByLocationAndScoreGreaterThanAndCategoryContains(String location, Double score, String category);

}
