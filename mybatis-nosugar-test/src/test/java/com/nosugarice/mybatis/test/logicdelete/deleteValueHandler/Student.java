package com.nosugarice.mybatis.test.logicdelete.deleteValueHandler;

import com.nosugarice.mybatis.annotation.LogicDelete;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021-6-8
 */
@Table(name = "student")
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    /** 姓名 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 删除 */
    @LogicDelete(defaultValue = "0", deleteValue = "1", deleteValueHandler = DateValueHandler.class)
    @Column(name = "disabled")
    private Integer disabled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

}
