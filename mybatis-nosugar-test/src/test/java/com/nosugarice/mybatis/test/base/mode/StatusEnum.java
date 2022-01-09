package com.nosugarice.mybatis.test.base.mode;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021/1/17
 */
public enum StatusEnum {
    ON(0, "生效"),
    OFF(1, "失效"),
    ;

    private final Integer status;
    private final String des;

    StatusEnum(Integer status, String des) {
        this.status = status;
        this.des = des;
    }

    public static StatusEnum valueOfStatus(Integer status) {
        for (StatusEnum value : values()) {
            if (value.status.equals(status)) {
                return value;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDes() {
        return des;
    }
}
