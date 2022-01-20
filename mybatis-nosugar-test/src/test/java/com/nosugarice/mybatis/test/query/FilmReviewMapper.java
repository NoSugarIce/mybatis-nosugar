package com.nosugarice.mybatis.test.query;

import com.nosugarice.mybatis.mapper.BaseMapper;

import java.util.List;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021-6-8
 */
public interface FilmReviewMapper extends BaseMapper<FilmReview, Integer> {

    List<FilmReview> findById(Integer id);

}
