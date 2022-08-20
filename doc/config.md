## 配置

配置可以使用两种方式加载进NoSugar

- 使用Mybatis原生Properties配置
- 把配置类对象加载到Spring环境


###### 注意:

- Spring环境中的配置优先级高于properties的形式.同一个配置类仅能在Spring 环境中配置一次.
- 所有基于Properties配置的Class,都要提供公共的无参构造函数.配置注册到Spring环境的配置类的则没有这个限制.
- Java类转换数据库表映射关系优先使用`Java Persistence API`相关注解配置表名列属性等,如果不存在`Java Persistence API相关`注解,名称默认以驼峰转下划线策略.


### 功能开关配置
NoSugar配置使用`mybatis.no-sugar`作为前缀.

| 前缀:`switch`             | 说明                                                         | 类型                | 默认    |
| :------------------------ | ------------------------------------------------------------ | ------------------- | ------- |
| `include-mapper-builders` | 把Mapper方法转换成SQL加载到MyBatis环境中的处理类,全限定类名,以逗号分隔,默认内置处理器,无特殊需求不用配置. | String[ClassName..] |         |
| `exclude-mapper-builders`   | 排除部分处理类,全限定类名,以逗号分隔.无特殊需求不用配置.     | String[ClassName..] |         |
| `logic-delete`            | 开启软删除                                                   | `boolean`           | `true`  |
| `version`                 | 开启乐观锁功能                                               | `boolean`           | `true`  |
| `lazy-builder`            | 开启懒加载                                                   | `boolean`           | `false` |
| `speed-batch`             | 开启批处理增强模式                                           | `boolean`           | `true`  |

### 实体类映射配置

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


### 生成SQL配置

| 前缀:`sql-build` | 说明          | 类型                       | 默认                                      |
| --- |:------------| -------------------------- |-----------------------------------------|
| `dialect-factory-class`  | 数据库方言工厂类    | `Class<? extends DialectFactory>` | DefaultDialectFactory(性能不高,如有性能要求请自己实现) |
| `dialect-class` | 数据库方言实现类    | `Class<? extends Dialect>` | 根据数据库连接自动判断(第一优先级)                      |
| `runtime-dialect` | 运行时动态获取方言实现 | `Boolean` | false                                   |

注:

`AccessType->javax.persistence.AccessType`

`NameStrategyType->com.nosugarice.mybatis.support.NameStrategyType`

`NameStrategy->com.nosugarice.mybatis.support.NameStrategy`

`Dialect->com.nosugarice.mybatis.dialect.Dialect`

`DialectFactory->com.nosugarice.mybatis.dialect.DefaultDialectFactory`

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

