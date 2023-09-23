## 插入时主键策略

根据实体注解`@Id;@GeneratedValue`
判断该实体是否支持自增主键.如果支持则检查当前数据库是否支持自增主键.如果支持则使用`Jdbc3KeyGenerator`
作为主键生成器;否则根据实体元数据中的主键生成语句构造出一个`SelectKeyGenerator`.
如果实体不支持自增主键,但支持使用自定义主键生成器,则根据实体元数据中的主键生成语句构造出一个`CustomizeKeyGenerator`.

### 数据库支持自增主键

在需要自增的主键列上添加`@GeneratedValue(strategy = GenerationType.IDENTITY)`

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id", nullable = false)
private Integer id;
```

### 从数据库中查询自增主键

查询语句可以为

- 查询最后生成的id(需要数据库支持).如DB2 的`IDENTITY_VAL_LOCAL()`
- 查询序列的方式(需要数据库支持序列),如Oracle的`SELECT sequence_name.NEXTVAL FROM DUAL`
- 查询表

数据库支持[查询最后生成的id]在需要自增的主键列上添加`@GeneratedValue(strategy = GenerationType.IDENTITY)`.

如果数据库不支持[查询最后生成的id]并且`generator`没有设置查询语句会报错.系统会自动使用数据库默认的[查询最后生成id]
的语句查询并赋值,如果`generator`有值,则会优先使用`generator`返回的语句,

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "SELECT sequence_name.NEXTVAL FROM DUAL")
@Column(name = "id", nullable = false)
private Integer id;
```

#### 自定义主键填充

在需要自动填充的主键列上添加`@GeneratedValue(generator = "uuid")`,`generator`值为定义的配置主键生成类注册名称,NoSugar自带一个`uuid` 策略,可以自己实现雪花等算法然后配置.

###### 基于`Spring-boot` -`application.properties`

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
