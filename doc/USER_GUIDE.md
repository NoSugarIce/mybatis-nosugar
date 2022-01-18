### 配置

配置可以使用两种方式加载进NoSugar

- 使用Mybatis原生Properties配置
- 把配置类对象加载到Spring环境


###### 注意:

- Spring环境中的配置优先级高于properties的形式.同一个配置类仅能在Spring 环境中配置一次.
- 所有基于Properties配置的Class,都要提供公共的无参构造函数.配置注册到Spring环境的配置类的则没有这个限制.
- Java类转换数据库表映射关系优先使用`Java Persistence API`相关注解配置表名列属性等,如果不存在`Java Persistence API相关`注解,名称默认以驼峰转下划线策略.

NoSugar配置使用`mybatis.no-sugar`作为前缀.

##### 功能开关配置

| 前缀:`switch`             | 说明                                                         | 类型                | 默认    |
| :------------------------ | ------------------------------------------------------------ | ------------------- | ------- |
| `include-mapper-builders` | 把Mapper方法转换成SQL加载到MyBatis环境中的处理类,全限定类名,以逗号分隔,默认内置处理器,无特殊需求不用配置. | String[ClassName..] |         |
| `exclude-mapper-builders`   | 排除部分处理类,全限定类名,以逗号分隔.无特殊需求不用配置.     | String[ClassName..] |         |
| `logic-delete`            | 开启软删除                                                   | `boolean`           | `true`  |
| `version`                 | 开启乐观锁功能                                               | `boolean`           | `true`  |
| `lazy-builder`            | 开启懒加载                                                   | `boolean`           | `false` |
| `speed-batch`             | 开启批处理增强模式                                           | `boolean`           | `true`  |

##### 实体类映射配置

从实体类Class转换为数据库对应的表信息有默认的实现类,如果结构差异较大可以实现自己的从类结构解析到表结构方法.


| 前缀:`relational`                          | 说明                                                         | 类型                            | 默认                  |
| :----------------------------------------- | :----------------------------------------------------------- | :------------------------------ | :-------------------- |
| `mapper-strategy` | 从Mapper接口Class文件解析到具体实体类 | Class<?> | `DefaultMapperStrategy.class` |
| `entity-builder-class` | 实体类到表信息的解析类 | `Class<? extends AbstractEntityBuilder<?>>` | `DefaultEntityBuilder.class` |
| `access-type`                              | 从类的属性或者方法映射到数据库表字段,`FIELD`:属性,`PROPERTY`:`get`方法 | `AccessType`          | `FIELD`               |
| `class-name-to-table-name-strategy`        | 类名转换表名的处理方法                                       | `NameStrategyType`         | `CAMEL_TO_UNDERSCORE` |
| `class-name-to-table-name-strategy-class`  | 类名转换表名的处理类                                         | `Class<? extends NameStrategy>` |                       |
| `field-name-to-column-name-strategy`       | 类属性映射到数据库表字段处理方法                             | `NameStrategyType`          | `CAMEL_TO_UNDERSCORE` |
| `field-name-to-column-name-strategy-class` | 类属性映射到数据库表字段处理类                               | `Class<? extends NameStrategy>` |                       |
| `javax-validation-mapping-not-null`        | `javax.validation`接口的注解是否映射为非空,包含`NotNull` | `boolean`                       | `true`                |
| `id-generator-types` | 主键填充类型,Map结构,名称为键,类型为值 | `Class<IdGenerator<?>>` |  |


##### 生成SQL配置

| 前缀:`sql-build` | 说明             | 类型                       | 默认                   |
| ---------------- | :--------------- | -------------------------- | ---------------------- |
| `dialect-class`  | 数据库方言实现类 | `Class<? extends Dialect>` | 根据数据库连接自动判断 |

注:

`AccessType->javax.persistence.AccessType`

`NameStrategyType->com.nosugarice.mybatis.support.NameStrategyType`

`NameStrategy->com.nosugarice.mybatis.support.NameStrategy`

`Dialect->com.nosugarice.mybatis.dialect.Dialect`

##### 示例:

###### 基于 Spring-Boot `application.properties`

```properties
mybatis.configuration-properties.mybatis.no-sugar.switch.logic-delete=true
mybatis.configuration-properties.mybatis.no-sugar.sql-build.dialect-class=com.nosugarice.mybatis.dialect.MySqlDialect
```

###### 基于 Spring-Boot `application.yml`

```yml
mybatis:
  configuration-properties:
    mybatis.no-sugar.switch.logic-delete: true
    mybatis.no-sugar.sql-build.dialect-class: com.nosugarice.mybatis.dialect.MySqlDialect
```

###### 基于 Spring `spring.xml`

```xml
<configuration>
    <properties>
        <property name="mybatis.no-sugar.switch.logic-delete" value="true"/>
        <property name="mybatis.no-sugar.sql-build.dialect-class" value="com.nosugarice.mybatis.dialect.MySqlDialect"/>
    </properties>
</configuration>
```

###### 基于 Spring `@bean`

```java
@Bean
public SwitchConfig switchConfig() {
    SwitchConfig config = new SwitchConfig();
    config.setLogicDelete(true);
    return config;
}

@Bean
public SqlBuildConfig sqlBuildConfig() {
    SqlBuildConfig config = new SqlBuildConfig();
    config.setDialect(new MySqlDialect());
    return config;
}
```

### 注解概览

| 名称                | 类型                                                         | 说明                                 |
| :------------------ | ------------------------------------------------------------ | ------------------------------------ |
| `@Access`           | `javax.persistence.Access`                                   | 从类的属性或者方法映射到数据库表字段 |
| `@Table`            | `javax.persistence.Table`                                    | 类到表的映射                         |
| `@Id`               | `javax.persistence.Id`                                       | 是否是id                             |
| `@GeneratedValue`   | `javax.persistence.GeneratedValue`                           | 主键生成策略                         |
| `@Column`           | `javax.persistence.Column`                                   | 类属性到表字段的映射               |
| `@Version`          | `javax.persistence.Version`                                  | 乐观锁标识                         |
| `@ColumnOptions`    | `ColumnOptions`   | 列其他选项,忽略空字符,列对应值处理器等 |
| `@DynamicTableName` | `DynamicTableName` | 是否开启动态表名                   |
| `@LogicDelete`      | `LogicDelete`     | 软删除                             |
| `@SpeedBatch`       | `SpeedBatch`      | 批处理增强模式                       |



### 插入时主键策略

##### 数据库支持自增主键

在需要自增的主键列上添加`@GeneratedValue(strategy = GenerationType.IDENTITY)`

```java
@Id
@Column(name = "id", nullable = false)
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;
```



##### 从数据库中查询自增主键

查询语句可以为

- 查询最后生成的id(需要数据库支持).如DB2 的`VALUES IDENTITY_VAL_LOCAL()`
- 查询序列的方式(需要数据库支持序列),如Oracle的`SELECT xxx.nextval FROM DUAL`
- 查询表

在需要自增的主键列上添加`@GeneratedValue(strategy = GenerationType.IDENTITY)`.

如果数据库不支持*查询最后生成的id*并且`generator`没有设置查询语句会报错.系统会自动使用数据库默认的*查询最后生成id*的语句查询并赋值,如果`generator`有值,则会优先使用`generator`返回的语句,

```java
@Id
@Column(name = "id", nullable = false)
@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT xxx.nextval  FROM DUAL")
private Integer id;
```



##### 自定义主键填充

在需要自动填充的主键列上添加`@GeneratedValue(generator = "uuid")`,`generator`值为定义的配置主键生成类注册名称,NoSugar自带一个`uuid` 策略,可以自己实现雪花等算法然后配置.

配置基于`Spring-boot`环境`application.properties`

系统默认已经添加`uuid`策略,示例上的`uuid`仅为演示怎么配置使用.

```properties
mybatis.configuration-properties.mybatis.no-sugar.relational.id-generator-types[uuid]=com.nosugarice.mybatis.valuegenerator.id.UUIDGenerator
mybatis.configuration-properties.mybatis.no-sugar.relational.id-generator-types[uuid1]=com.nosugarice.mybatis.valuegenerator.id.UUIDGenerator
```

```java
@Id
@Column(name = "id", nullable = false)
@GeneratedValue(generator = "uuid")
private String id;
```

### 增删改查

### 查询条件构造

##### 说明

条件构造就是表中的列值符合什么样的条件,把这些条件组合.

支持条件方法

| 条件               | 快速方法                                     | 对应的sql             |
| :----------------- | :------------------------------------------- | --------------------- |
| 是空             | `isNull(C column)`                           | `IS NULL`             |
| 不为空           | `isNotNull(C column)`                 | `IS NOT NULL`         |
| 是空字符串       | `isEmpty(C column)`                     | `= ''`                |
| 不是空字符串     | `isNotEmpty(C column)`               | `<> ''`               |
| 等于             | `equalTo(C column, V value)`            | `= ?`                 |
| 不等于           | `notEqualTo(C column, V value)`      | `<> ?`                |
| 大于             | `greaterThan(C column, V value)`             | `> ?`                 |
| 不大于           | `notGreaterThan(C column, V value)`          | `<= ?`                |
| 大于等于         | `greaterThanOrEqual(C column, V value)` | `>= ?`                |
| 不大于等于       | `notGreaterThanOrEqual(C column, V value)` | `< ?`                 |
| 小于             | `lessThan(C column, V value)`                | `< ?`                 |
| 不小于           | `notLessThan(C column, V value)`             | `>= ?`                |
| 小于等于         | `lessThanOrEqual(C column, V value)` | `<= ?`                |
| 不小于等于       | `notLessThanOrEqual(C column, V value)` | `> ?`                 |
| 介于??之间       | `between(C column, V value, V value1)`       | `Between ? AND ?`     |
| 不介于??之间     | `notBetween(C column, V value, V value1)`    | `NOT Between ? AND ?` |
| 在集合里         | `in(C column, V[] values)`                   | `IN(?,?,?)`           |
|                    | `in(C column, Collection<V> values)`         | `IN(?,?,?)`           |
| 不在集合里       | `notIn(C column, V[] values)`                | `NOT IN(?,?,?)`       |
|                   | `notIn(C column, Collection<V> values)`      | `NOT IN(?,?,?)`       |
| 字符串全匹配     | `like(C column, String value)`               | `LIKE ?`            |
| 字符串不全匹配   | `notLike(C column, String value)`            | `NOT LIKE ?`        |
| 字符串前缀匹配   | `startsWith(C column, String value)` | `LIKE ?%`             |
| 字符串前缀不匹配 | `notStartsWith(C column, String value)` | `NOT LIKE ?%`         |
| 字符串后缀匹配   | `endsWith(C column, String value)`  | `LIKE %?`             |
| 字符串后缀不匹配 | `notEndsWith(C column, String value)` | `NOT LIKE %?`         |
| 字符串包含 | `contains(C column, String value)` | `LIKE %?%` |
| 字符串不包含 | `notContains(C column, String value)` | `NOT LIKE %?%` |

`CriteriaMapper`方法中的入参类型,继承了`ToColumn<C>`,`C`代表列的抽象,根据实现类的不同`C`代表类不同的意义.在运行时最终会转换成数据库真实的列名称.

基础的等于条件可以直接设置实体对象属性然后添加到`Criteria`.

#### Mapper方法中的条件入参

| Mapper                     | 条件入参             |
| -------------------------- | -------------------- |
| 查询(SelectCriteriaMapper) | CriteriaQuery<T, C>  |
| 更新(UpdateCriteriaMapper) | CriteriaUpdate<T, C> |
| 删除(DeleteCriteriaMapper) | CriteriaDelete<T, C> |

| 条件入参实现类      | 功能                         | 列名参数的形式                |
| ------------------- | ---------------------------- | ----------------------------- |
| `LambdaQuery<T>`    | Lambda形式的查询条件构造器   | 实体类属性的get方法Lambda形式 |
| `LambdaUpdate<T>`   | Lambda形式的更新条件构造器   | 实体类属性的get方法Lambda形式 |
| `LambdaDelete<T>`   | Lambda形式的删除条件构造器   | 实体类属性的get方法Lambda形式 |
| `PropertyQuery<T>`  | 属性名称形式的查询条件构造器 | 实体类属性名称                |
| `PropertyUpdate<T>` | 属性名称形式的更新条件构造器 | 实体类属性名称                |
| `PropertyDelete<T>` | 属性名称形式的删除条件构造器 | 实体类属性名称                |
| `ColumnQuery<T>`    | 真实列名形式的查询条件构造器 | 数据库真实列名                |
| `ColumnUpdate<T>`   | 真实列名形式的更新条件构造器 | 数据库真实列名                |
| `ColumnDelete<T>`   | 真实列名形式的删除条件构造器 | 数据库真实列名                |

条件构造中有组条件的概念(`GroupCriterion`),顾名思义就是一组条件的组合,默认的条件连接条件为`OR`.

`Where`中通过直接添加条件的方式如调用`equal(C column, V value)`方法的条件都会默认添加到第一组条件集合.默认连接为`AND`

#### 快速构建条件入参实现类

使用构建类`CriteriaBuilder`

##### 示例

使用实体类型构建

```java
LambdaQuery<Student> query=CriteriaBuilder.lambdaQuery(Student.class);
        query.equalTo(Student::getStatus,StatusEnum.ON)
        .between(Student::getAge,10,30)
        .lessThan(Student::getCardBalance,new BigDecimal("200"));

        List<Student> students=mapper.selectList(query);
```

使用实体对象构建

```java
Student student=new Student();
        student.setAge(10);
        student.setAddress("驻马店");

        LambdaQuery<Student> query=CriteriaBuilder.lambdaQuery(student);
        query.equalTo(Student::getStatus,StatusEnum.ON)
        .lessThan(Student::getCardBalance,new BigDecimal("200"));
```

### JPA式方法名实现查询

##### 说明

和Jpa一样根据方法名定义方法,注意参数个数要对的上.舍去了部分支持字段.

[Jpa方法名规范](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)

#### 支持的操作:

| 方法前缀         | 操作类型     | 返回类型          |
| ---------------- | ------------ | ----------------- |
| `find|get|query` | 查询         | List<T>,T         |
| `count`          | 总数查询     | long              |
| `exists`         | 是否存在查询 | Optional<Integer> |
| `delete|remove`  | 删除         | int               |
| `logicDelete`    | 软删除       | int               |

#### 支持方法名操作关键字

| 关键字                 | 示例                                                         | sql                                                          |
| :--------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| `And`                  | `findByLastnameAndFirstname`                                 | `… where x.lastname = ?1 and x.firstname = ?2`               |
| `Or`                   | `findByLastnameOrFirstname`                                  | `… where x.lastname = ?1 or x.firstname = ?2`                |
| `Is`, `Equals`         | `findByFirstname`,`findByFirstnameIs`,`findByFirstnameEquals` | `… where x.firstname = ?1`                                   |
| `Between`              | `findByStartDateBetween`                                     | `… where x.startDate between ?1 and ?2`                      |
| `LessThan`             | `findByAgeLessThan`                                          | `… where x.age < ?1`                                         |
| `LessThanEqual`        | `findByAgeLessThanEqual`                                     | `… where x.age <= ?1`                                        |
| `GreaterThan`          | `findByAgeGreaterThan`                                       | `… where x.age > ?1`                                         |
| `GreaterThanEqual`     | `findByAgeGreaterThanEqual`                                  | `… where x.age >= ?1`                                        |
| `After`                | `findByStartDateAfter`                                       | `… where x.startDate > ?1`                                   |
| `Before`               | `findByStartDateBefore`                                      | `… where x.startDate < ?1`                                   |
| `IsNull`, `Null`       | `findByAge(Is)Null`                                          | `… where x.age is null`                                      |
| `IsNotNull`, `NotNull` | `findByAge(Is)NotNull`                                       | `… where x.age not null`                                     |
| `Like`                 | `findByFirstnameLike`                                        | `… where x.firstname like ?1`                                |
| `NotLike`              | `findByFirstnameNotLike`                                     | `… where x.firstname not like ?1`                            |
| `StartingWith`         | `findByFirstnameStartingWith`                                | `… where x.firstname like ?1` (parameter bound with appended `%`) |
| `EndingWith`           | `findByFirstnameEndingWith`                                  | `… where x.firstname like ?1` (parameter bound with prepended `%`) |
| `Containing`           | `findByFirstnameContaining`                                  | `… where x.firstname like ?1` (parameter bound wrapped in `%`) |
| `Not`                  | `findByLastnameNot`                                          | `… where x.lastname <> ?1`                                   |
| `In`                   | `findByAgeIn(Collection<Age> ages)`                          | `… where x.age in ?1`                                        |
| `NotIn`                | `findByAgeNotIn(Collection<Age> ages)`                       | `… where x.age not in ?1`                                    |

##### 示例

```java
public interface StudentMapper extends BaseMapper<Student, String> {

    List<Student> findByNameStartsWithAndAgeBetween(String name, Integer ageStart, Integer ageEnd);

    List<Student> findByNameStartsWithOrAgeBetween(String name, Integer ageStart, Integer ageEnd);

    long countByNameStartsWithAndAgeBetween(String name, Integer ageStart, Integer ageEnd);

    Optional<Integer> existsByNameStartsWithAndAgeBetween(String name, Integer ageStart, Integer ageEnd);

    int deleteByNameStartsWithAndAgeBetween(String name, Integer ageStart, Integer ageEnd);

    int logicDeleteByNameStartsWithAndAgeBetween(String name, Integer ageStart, Integer ageEnd);

}
```



### 全新Count查询	

#### 基于`SelectMapper#adapterCount`

##### 说明

在原查询方法上开启Count查询.

##### 示例

```java
List<Student> findByNameStartsWithAndAgeBetween(String name,Integer ageStrrt,Integer ageEnd);

long count=studentMapper.countP3(studentMapper::findByNameStartsWithAndAgeBetween,"王",15,19);

```

###### 注意

调用的方法除第一个方法引用参数外其他的参数类型要和原方法对应.



### 全新分页

##### 说明

与传统的插件形式的实现方法不同,在原查询方法上只需简单配置即可开启全新分页.

支持数据库

- MYSQL
- ORACLE
- SQLSERVER
- SQLSERVER2012
- POSTGRESQL
- DB2

数据库不在支持的列表?

配置`mybatis.no-sugar.sql-build.dialect-class`即可支持新的数据库类型.

##### 使用

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



### 软删除

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

### 乐观锁

##### 说明

更新的时候带上当前版本的下一个版本,如果同一条数据同时被多人更新,肯定只会有一个更新成功.

##### 使用

在版本列上添加`@Version`

##### 生效条件

NoSugar自带的update方法且更新时参数上乐观锁标识的列有值.

##### 支持类型

| 类型            | 默认值                 | 更新值     |
| :-------------- |---------------------| ---------- |
| Integer       | 0                   |+1|
| Long          | 0                   | +1       |
| Date | 插入时时间               |当前时间|
| Timestamp | 插入时时间 |当前时间|
| LocalDateTime | 插入时时间 |当前时间|

##### 示例

```java
@Version
@Column(name = "version", nullable = false)
private Integer version;
```



### 动态表名

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



### 批处理增强模式

##### 说明

插入或者更新可以不用逐条执行,变成批量执行.

##### 使用

需做以下步骤即可开启批处理模式

1. 在`Mapper`类上配置`@SpeedBatch`.
2. 需要批处理的`Mapper`方法上添加`@SpeedBatch`.
3. 数据库需要打开批处理模式.

##### 示例

```java
List<Student> students = new ArrayList<>();
for (int i = 0; i < 50000; i++) {
    Student student = new Student();
    student.setName("d" + i);
    student.setAge(0);
    student.setSex(0);
    student.setSno(1);
    student.setPhone("186");
    student.setAddress("南京");
    student.setCardBalance(new BigDecimal("0"));
    student.setStatus(StatusEnum.ON);
    student.setCreatedAt(LocalDateTime.now());
    student.setUpdatedAt(LocalDateTime.now());
    students.add(student);
}
studentMapper.insertBatchMode(students, 1000, false);
```





