package com.nosugarice.mybatis.test.parentproperty;

import com.nosugarice.mybatis.annotation.ColumnOptions;
import com.nosugarice.mybatis.annotation.LogicDelete;
import com.nosugarice.mybatis.test.base.valuehandler.NowHandler;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author dingjingyang@foxmail.com
 * @date 2022/1/15
 */
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = -2355967286317673597L;

    /** 主键 */
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(generator = "uuid")
    private String id;

    /** 创建时间 */
    @ColumnOptions(insertHandler = NowHandler.class)
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @ColumnOptions(updateHandler = NowHandler.class)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 删除时间 */
    @LogicDelete(deleteValue = "NOW")
    @Column(name = "disabled_at")
    private LocalDateTime disabledAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDisabledAt() {
        return disabledAt;
    }

    public void setDisabledAt(LocalDateTime disabledAt) {
        this.disabledAt = disabledAt;
    }
}
