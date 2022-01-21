## 查询条件构造

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

### Mapper方法中的条件入参

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

### 快速构建条件入参实现类

使用构建类`CriteriaBuilder`

##### 示例

使用实体类型构建

```java
LambdaQuery<Student> query = CriteriaBuilder.lambdaQuery(Student.class);
query.equalTo(Student::getStatus, StatusEnum.ON)
        .between(Student::getAge, 10, 30)
        .lessThan(Student::getCardBalance, new BigDecimal("200"));

List<Student> students = mapper.selectList(query);
```

使用实体对象构建

```java
Student student = new Student();
student.setAge(10);
student.setAddress("驻马店");
LambdaQuery<Student> query = CriteriaBuilder.lambdaQuery(student);
query.equalTo(Student::getStatus, StatusEnum.ON)
        .lessThan(Student::getCardBalance, new BigDecimal("200"));

List<Student> students = mapper.selectList(query);
```