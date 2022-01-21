## 运行环境

### 基础运行环境

1. JDK1.8+
2. Maven
3. MyBatis

### 选择性环境

- Mybatis-Spring
- MyBatis-Spring-Boot

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

### 全新使用

NoSugar只负责在程序启动的时候把增强的部分加载到Mybatis.启动的部分依旧交由官方的Mybatis-Spring,MyBatis-Spring-Boot负责.

只需要在原来`Mybatis-Spring`配置的基础上把`factoryBean`替换成NoSugar中的`MybatisMapperFactoryBean`,即可开启所有功能.

##### 基于`@MapperScan`注解

应用环境

- 配置了注解扫描的Spring环境
- Spring-boot

```java
@MapperScan(basePackages = {"com.xxx"}, factoryBean = MybatisMapperFactoryBean.class)
public class MyBatisConfiguration {
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
    <property name="basePackage" value="com.xxx"/>
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
    <property name="mapperFactoryBeanClass" value="MybatisMapperFactoryBean"/>
</bean>
```

### 增强使用

和其他Mybatis框架同时存在.

应用环境

- 配置了注解扫描的Spring环境
- Spring-boot


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

| Mapper接口                                              | 支持的功能                     |
| ------------------------------------------------------- | ------------------------------ |
| com.nosugarice.mybatis.mapper.BaseMapper                | 增删改查的集合                 |
| com.nosugarice.mybatis.mapper.WithLogicDeleteBaseMapper | 增删改查的集合(附加软删除接口) |
| com.nosugarice.mybatis.mapper.select.SelectPageMapper   | 分页查询                       |
| com.nosugarice.mybatis.mapper.select.SelectCountMapper  | Count查询                      |
| com.nosugarice.mybatis.mapper.select.SelectExistsMapper | Exists查询                     |
| com.nosugarice.mybatis.mapper.function.JpaMapper        | JPA式根据方法名查询            |

##### Mapper接口实现BaseMapper
```java
@SpeedBatch
public interface StudentMapper extends BaseMapper<Student, String> {
}
```
