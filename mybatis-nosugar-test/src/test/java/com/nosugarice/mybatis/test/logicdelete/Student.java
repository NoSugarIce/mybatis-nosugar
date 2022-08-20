package com.nosugarice.mybatis.test.logicdelete;

import com.nosugarice.mybatis.annotation.ColumnOptions;
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
    @LogicDelete(defaultValue = "0", deleteValue = "1")
    @Column(name = "disabled")
    private Integer disabled;

    @ColumnOptions(logicDeleteHandler = LogicDeleteDisabledByHandler.class)
    @Column(name = "disabled_by")
    private Integer disabledBy;

    @ColumnOptions(logicDeleteHandler = LogicDeleteDisabledNameHandler.class)
    @Column(name = "disabled_name", nullable = false)
    private String disabledName;

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

    public Integer getDisabledBy() {
        return disabledBy;
    }

    public void setDisabledBy(Integer disabledBy) {
        this.disabledBy = disabledBy;
    }

    public String getDisabledName() {
        return disabledName;
    }

    public void setDisabledName(String disabledName) {
        this.disabledName = disabledName;
    }
}
