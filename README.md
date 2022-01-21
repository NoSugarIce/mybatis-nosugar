![](https://gitee.com/NoSugarIce/document-gallery/raw/master/mybatis-nosugar/NoSugar-log.svg)

## 为何是NoSugar

当年刚出道的时候写的Mybatis模板Xml已然跟不上时代.其他工具又不顺手.

天下武功,无坚不破,唯快不破.开发此项目的原因之一就是需要上手快一点,启动快一点,运行快一点,内存占用小一点.

希望为开发者提供简单易用功能完善的Api.NoSugar只做Mybatis增强这一件事.NoSugar并没有完全实现JPA,时间精力有限,只实现了JPA根据方法名查询的功能.

## 功能概览

- 无糖配方
- 开放大量接口给与开发者很大的自由度,让开发者根据自己的程序适配.不必千篇一律
- 性能非常丝滑,大部分功能超越动态标签,参数越多性能提升越明显
- 使用简单,不影响原有项目,无需修改原Mybatis类声明,没有重构任何Mybatis基础配置类,只需增加一个属性配置即可开启
- 无缝增强现有Mybatis项目(+功能),即使现在的项目在使用其他Mybatis框架依旧可增强
- 部分功能如分页,Count查询,JPA方式的根据方法名查询,可以单独选用
- 基础的增删改查
- 条件构造
- 插入时主键策略
- 批处理增强模式
- 全新的通用分页方式,无需插件
- 全新的通用Count查询方法
- 软删除
- 乐观锁
- 动态表名
- 更易用的值处理器
- Jpa式根据方法名查询,删除


#### 部分功能演示

```java
//Mapper中的一个普通查询方法(根据方法名查询,自动构建)
List<Student> findByNameStartsWithAndAgeBetween(String name,Integer ageStart,Integer ageEnd);

//全新的通用分页方式(非插件实现)
Page<Student> page=studentMapper.selectPageP3(new PageImpl<>(2),studentMapper::findByNameStartsWithAndAgeBetween,"王",15,19);

//全新的通用Count查询(非插件实现)
long count=studentMapper.countP3(studentMapper::findByNameStartsWithAndAgeBetween,"王",15,19);
```

## 使用参考

可参考源码[test](https://github.com/NoSugarIce/mybatis-nosugar/tree/master/mybatis-nosugar-test/src/test/java/com/nosugarice/mybatis/test) 模块

- [快速开始](doc/quickStart.md)
- [配置](doc/config.md)
- [插入时主键策略](doc/idGenerated.md)
- [增删改查](doc/USER_GUIDE.md)
- [查询条件构造](doc/criterion.md)
- [JPA式方法名实现查询](doc/jpa.md)
- [全新Count查询](doc/count.md)
- [全新分页](doc/page.md)
- [软删除](doc/logicDelete.md)
- [乐观锁](doc/version.md)
- [动态表名](doc/dynamicTableName.md)
- [批处理增强模式](doc/batch.md)

## 计划或非计划

- 全功能测试

## 常见问题

当前版本仅是样例版,功能测试还没完成

当前主要环境是MySql,其他数据库暂未开始测试

如果有类名或方法名等命名闹笑话的请勿介意,作者英语不及格

编码五分钟,命名两小时...部分类名就直接复用JPA中的类名.

...

## 相关参考资料

- [Mybatis](https://mybatis.org/mybatis-3/zh/index.html)
- [Mybatis-Spring](http://mybatis.org/spring/index.html)
- [Mybatis-spring-boot](http://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/#)
- [Jpa](https://spring.io/projects/spring-data-jpa)
- [Hibernate](http://hibernate.org/orm/)

## License

[Apache-2.0](LICENSE) @ NoSugarIce
