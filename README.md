 ![](https://gitee.com/NoSugarIce/document-gallery/raw/master/mybatis-nosugar/NoSugar-log.svg)

## 为什么要有NoSugar

Mybatis的简单轻巧使得很多人和项目的喜爱,但不像Jpa使用那么简单.一张表的基础的操作方法还是需要额外的工作才能正常使用.可以使用MyBatis-Generator进行简便的开发.最开始是构建一个功能类似与Generator的模板代码生成的项目A对MyBatis的支持工作,但是每次更改表结构的时候,代码还是需要重新生成一遍,还是有点麻烦.部分项目是Jpa,机子配置不怎么样,每次启动都巨慢,每天花费大量时间等待程序启动.于是NoSugar就开发了.


## NoSugar简介

NoSugar目前仅支持单表,希望单表的简单操作不需要开发人员去进行一些额外的开发就可以直接使用.NoSugar只做Mybatis增强这一件事.部分项目使用的是Jpa(Hibernate),Hibernate是个很牛X的框架,但是使用的过程中还是有些众口难调的地方.所以NoSugar会带有在Jpa使用遗憾上的改进.NoSugar并没有实现Jpa,时间精力有限,只实现了Jpa根据方法名查询的功能.当前项目只是预览阶段,有很多Bug还待完善,基础代码已经完成,但还没有全功能测试.



## 功能概览

- 使用简单,不影响原有项目,无需修改原Mybatis类声明,没有重构任何Mybatis基础配置类,只需增加一行配置即可开启
- 无缝增强现有Mybatis项目 (+功能),即使现在的项目在使用其他Mybatis框架依旧可增强
- 部分功能如分页,count查询,Jpa方式的根据方法名查询,可以单独选用(实现方式并非Mybatis插件接口)
- 性能非常丝滑,和原Mybatis无差别
- 基础的增删改查
- 查询条件构造
- 插入时主键策略
- 批处理增强模式
- 全新的分页方式,无需插件
- 一种没遇到的count查询方法
- 软删除
- 乐观锁
- 动态表名
- 根据方法名实现条件查询(效果如同Jpa)


#### 部分功能演示

```java
//Mapper中的一个普通查询方法(根据方法名查询,自动构建)
@Provider(value = Provider.Type.PAGE, adapter = Provider.Adapter.COUNT)
List<Student> findByNameStartsWithAndAgeBetween(String name, Integer ageStart, Integer ageEnd);

//全新的通用分页方式(非插件实现)
Page<Student> page = studentMapper.selectPageP3(new PageImpl<>(2), studentMapper::findByNameStartsWithAndAgeBetween, "王", 15, 19);

//全新的通用Count查询(非插件实现)
long count = studentMapper.selectAdapter((FunS.Param3<String, Integer, Integer, List<Student>>) studentMapper::findByNameStartsWithAndAgeBetween
                , "王", 15, 19);

```


## 运行环境

##### 基础运行环境

1. JDK1.8+
2. Maven

3. MyBatis

##### 选择性环境

- Mybatis-Spring
- MyBatis-Spring-Boot

##### 受限功能

部分功能依赖于Mybatis-Spring,如果在非Mybatis-Spring环境,部分功能受限.

| 功能           | 依赖的环境                                          |
| -------------- | --------------------------------------------------- |
| 批处理增强模式 | Mybatis-Spring                                      |
|                | 必须指定factoryBean为MybatisMapperFactoryBean.class |

###### 注意:

NoSugar项目Maven引用Spring相关的依赖作用范围是`provided`,使用时需注意有没有引入以下两个库.

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>${spring.version}</version>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-tx</artifactId>
    <version>${spring.version}</version>
</dependency>
```

## 快速使用

### Maven依赖

```xml
<dependency>
    <groupId>com.nosugarice</groupId>
    <artifactId>mybatis-nosugar-spring</artifactId>
    <version>${nosugar.version}</version>
</dependency>
```

后续会添加到[maven中央仓库](https://mvnrepository.com/)

- ### 全新使用

只需要在原来`Mybatis-Spring`配置的基础上把`factoryBean`替换成NoSugar中的`MybatisMapperFactoryBean`,即可体验所有功能.

##### 基于`@MapperScan`注解

应用环境

- 配置了注解扫描的Spring环境
- Spring-boot

```java
@MapperScan(basePackages = {"com.xxx"}, sqlSessionFactoryRef = "xxx", factoryBean = MybatisMapperFactoryBean.class)
public class MyBatisBizarkPostConfiguration {
}
```

##### 基于`@Bean`

应用环境

- 配置了注解扫描的Spring环境
- Spring-boot

```java
@Bean
public MapperScannerConfigurer mapperScannerConfigurer() {
    MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
    mapperScannerConfigurer.setBasePackage("com.xxx");
    mapperScannerConfigurer.setSqlSessionFactoryBeanName("xxx");
    mapperScannerConfigurer.setMapperFactoryBeanClass(MybatisMapperFactoryBean.class);
    return mapperScannerConfigurer;
}
```

##### 基于`spring.xml`

应用环境

- 基于Xml配置的Spring环境

```xml
<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
    <property name="basePackage" value="com.bizark.post.dao.mapper.post"/>
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
    <property name="mapperFactoryBeanClass" value="MybatisMapperFactoryBean"/>
</bean>
```

- ### 增强使用

和其他Mybatis框架同时存在.

应用环境

- 配置了注解扫描的Spring环境
- Spring-boot

##### 受限功能

- 批处理增强模式

```java
@Configuration
@MapperProvider(basePackages = {"xxx"})
public class MyBatisConfiguration {
}    
```

###### 注意:

增强使用模式部分功能受限,`MapperProvider`是从已经配置Mybatis环境中寻找相匹配的Mapper,并不是从项目中.



### 继承基础功能Mapper接口类

`BaseMapper`内置了基础的方法,有可能因为主键,软删除等配置有的内置的方法是失效的,这个时候可以参考`BaseMapper`实现自己的基础`Mapper`.

| Mapper接口                                              | 支持的功能         |
| ------------------------------------------------------- | ------------------ |
| com.nosugarice.mybatis.mapper.BaseMapper                | 默认继承了所有功能 |
| com.nosugarice.mybatis.mapper.select.SelectPageMapper   | 分页,Count查询     |
| com.nosugarice.mybatis.mapper.function.MethodNameMapper | 根据方法名查询     |

Mapper接口

```java
import com.nosugarice.mybatis.annotation.SpeedBatch;
import com.nosugarice.mybatis.mode.Student;

/**
 * @author dingjingyang@foxmail.com
 * @date 2021-6-8
 */
@SpeedBatch
public interface StudentMapper extends BaseMapper<Student, String> {
}
```


## 使用参考

待续...

- 示例基础数据

- 配置

- 插入时主键策略

- 增删改查

- 查询条件构造

- 根据方法名实现查询

- 全新Count查询

- 全新分页

- 软删除

- 乐观锁

- 动态表名

- 批处理增强模式


## 计划或非计划

- 全功能测试预计夏季结束时同步完成
- 多对一,一对多关联查询(不一定实现)
- join连表查询(不一定实现)

## 常见问题

当前版本仅是样例版,功能测试还没完成

当前测试环境是MySql,其他数据库暂未开始测试

如果有类名或方法名等命名闹笑话的请勿介意,作者英语不及格

...


## 相关参考资料

- [Mybatis](https://mybatis.org/mybatis-3/zh/index.html)
- [Mybatis-Spring](http://mybatis.org/spring/index.html)
- [Mybatis-spring-boot](http://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/#)
- [Jpa](https://spring.io/projects/spring-data-jpa)
- [Hibernate](http://hibernate.org/orm/)

## License

[Apache-2.0](LICENSE) @ NoSugarIce
