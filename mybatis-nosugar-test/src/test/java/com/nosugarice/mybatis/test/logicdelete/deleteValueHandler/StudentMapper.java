package com.nosugarice.mybatis.test.logicdelete.deleteValueHandler;

import com.nosugarice.mybatis.mapper.WithLogicDeleteBaseMapper;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021-6-8
 */
public interface StudentMapper extends WithLogicDeleteBaseMapper<Student, String> {

    Student findById(String id);

}
