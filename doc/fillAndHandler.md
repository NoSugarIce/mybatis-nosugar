## 自动填充器

##### 说明

当进行插入,更新,查询时自动给相关属性填充值

##### 使用

在需要自动填充值属性上添加`@Fill(value = NowHandler.class, insert = true, update = false, condition = false)`
,`value`:填充逻辑,需实现`com.nosugarice.mybatis.handler.ValueHandler`接口,`insert`:插入时是否填充值
,`update`:更新时是否填充值,`condition`:当进行带有查询时是否填充值

##### 示例

```java
/** 创建时间 */
@Fill(value = NowHandler.class, insert = true, update = false, condition = false)
@Column(name = "created_at", nullable = false)
private LocalDateTime createdAt;
```

## 值处理器

##### 说明

当进行插入,更新,查询,软删除时自动给相关属性值进行处理

##### 使用

1.使用相关注解添加在需要处理的属性上

2.值处理器需要实现`com.nosugarice.mybatis.handler.ValueHandler`接口

| 名称                    | 类型                                                     | 说明       |
|:----------------------|--------------------------------------------------------|----------|
| `@InsertHandler`      | `com.nosugarice.mybatis.annotation.InsertHandler`      | 插入时进行值处理 |
| `@UpdateHandler`      | `com.nosugarice.mybatis.annotation.UpdateHandler`      | 更新时进行值处理 |
| `@ConditionHandler`   | `com.nosugarice.mybatis.annotation.ConditionHandler`   | 查询时进行值处理 |
| `@LogicDeleteHandler` | `com.nosugarice.mybatis.annotation.LogicDeleteHandler` | 软删时进行值处理 |

##### 示例

```java
/** 更新时间 */
@UpdateHandler(NowHandler.class)
@Column(name = "updated_at")
private LocalDateTime updatedAt;
```

#### 特殊说明

向数据库发送数据时当几种处理器同时存在时.处理顺序:

1.自动填充器 -> 2.值处理器 -> 3.TypeHandler

向数据库接收数据时当几种处理器同时存在时.处理顺序:

1.TypeHandler -> 2.值处理器
