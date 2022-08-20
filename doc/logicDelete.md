## 软删除

##### 说明

默认查询条件会带上逻辑删除默认值.

##### 使用

在逻辑删除列上添加`@LogicDelete(defaultValue = "xxx", deleteValue = "xxx")`,`deleteValue` 为逻辑删除状态值必填,`defaultValue` 默认`NULL`
.当软删除字段为时间类型的时候,`deleteValue`可以为`NOW`取当前时间.

##### 限制

Jpa式根据方法名LogicDeleteByXXX的方法如果软删除字段是时间类型,则最终执行软删除的时候保存到数据库是程序启动时间,并非实际删除时间,请注意避免此情况.

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
