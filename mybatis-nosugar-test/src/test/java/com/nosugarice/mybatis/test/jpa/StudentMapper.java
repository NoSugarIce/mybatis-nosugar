package com.nosugarice.mybatis.test.jpa;

import com.nosugarice.mybatis.mapper.BaseMapper;

import java.util.List;
import java.util.Optional;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021-6-8
 */
public interface StudentMapper extends BaseMapper<Student, String> {

    List<Student> findByNameStartsWithAndAgeBetween(String name, Integer ageStart, Integer ageEnd);

    List<Student> findByNameStartsWithOrAgeBetween(String name, Integer ageStart, Integer ageEnd);

    long countByNameStartsWithAndAgeBetween(String name, Integer ageStart, Integer ageEnd);

    Optional<Integer> existsByNameStartsWithAndAgeBetween(String name, Integer ageStart, Integer ageEnd);

    int deleteByNameStartsWithAndAgeBetween(String name, Integer ageStart, Integer ageEnd);

    int logicDeleteByNameStartsWithAndAgeBetween(String name, Integer ageStart, Integer ageEnd);

}