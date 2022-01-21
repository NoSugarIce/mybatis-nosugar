## 动态表名

##### 说明

配置了`@DynamicTableName`的实体,每次执行数据库操作会动态的来获取对应的运行时表名

##### 使用

在实体类上配置`@DynamicTableName`

- 普通的转换方法

把表名转换器方法`Function<String, String>`注册到表名映射器中.全局有效.

```java
DynamicTableNameMapping.registerHandler("student", tableName -> tableName + "2021");
```

- 当前线程上下文的变量

```java
DynamicTableNameMapping.setTableName("student","student2021");
```

###### 注意

同一张表即注册了转换器又设置类线程转换变量,线程上下文映射关系优先级高于 表名转换器.
