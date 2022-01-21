## JPA式方法名实现查询

##### 说明

和JPA一样根据方法名定义方法,注意参数个数要和条件对应,参数类型和属性类型对应.舍去了部分支持字段.

[Jpa方法名规范](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation)

### 支持的操作:

| 方法前缀             | 操作类型     | 返回类型              |
|------------------| ------------ |-------------------|
| `find\get\query` | 查询         | List\<T\>,T       |
| `count`          | 总数查询     | long              |
| `exists`         | 是否存在查询 | Optional<Integer> |
| `delete\remove`  | 删除                | int               |
| `logicDelete`    | 软删除       | int               |

### 支持方法名操作关键字

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
