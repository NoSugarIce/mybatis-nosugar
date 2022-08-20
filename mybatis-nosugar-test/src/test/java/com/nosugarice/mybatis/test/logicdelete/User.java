package com.nosugarice.mybatis.test.logicdelete;

import java.io.Serializable;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021-6-8
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
