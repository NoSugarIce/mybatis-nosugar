## 软删除

##### 说明

默认查询条件会带上逻辑删除默认值.

##### 使用

在逻辑删除列上添加`@LogicDelete(defaultValue = "xxx", deleteValue = "xxx")`,`deleteValue` 为逻辑删除状态值必填,`defaultValue` 默认`NULL`.当软删除字段为时间类型的时候,`deleteValue`可以为`NOW`取当前时间.

##### 示例

```java
@LogicDelete(defaultValue = "true", deleteValue = "false")
@Column(name = "status", nullable = false)
private Boolean status;
```

```java
@LogicDelete(deleteValue = "NOW")
@Column(name = "disabled_at")
private LocalDateTime disabledAt;
```
