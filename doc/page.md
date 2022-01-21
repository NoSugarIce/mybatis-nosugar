## 全新分页

##### 说明

第N+1的全新分页方式

支持数据库

- MYSQL
- ORACLE
- SQLSERVER
- SQLSERVER2012
- POSTGRESQL
- DB2

数据库不在支持的列表?

配置`mybatis.no-sugar.sql-build.dialect-class`即可支持新的数据库类型.

#### 使用

使用`SelectPageMapper`相关的`selectPagePX`方法即可.

##### 示例

```java
//Mapper中的一个普通查询方法(根据方法名查询,自动构建)
List<Student> findByNameStartsWithAndAgeBetween(String name,Integer ageStart,Integer ageEnd);

//全新的通用分页方式(非插件实现)
Page<Student> page=studentMapper.selectPageP3(new PageImpl<>(2),studentMapper::findByNameStartsWithAndAgeBetween,"王",15,19);
```

###### 注意

- 调用的方法除第一个分页参数和第二个方法引用参数外其他的参数类型要和原方法对应.
- NoSugar自动分页的生成的sql只是通用的分页方式,当数据量超过一定级别,通用的分页方式效率较低,此时应该根据主键或者其他索引字段自己实现分页sql.
- 分页时count语句的优化只是简单基础的实现,如果追求好的效果可重写`Dialect#optimizationCountSql`
