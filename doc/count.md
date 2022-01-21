## 全新Count查询

### 基于`SelectMapper#adapterCount`

##### 说明

在原查询方法上开启Count查询.

##### 示例

```java
List<Student> findByNameStartsWithAndAgeBetween(String name,Integer ageStrrt,Integer ageEnd);

long count=studentMapper.countP3(studentMapper::findByNameStartsWithAndAgeBetween,"王",15,19);
```

###### 注意

调用的方法除第一个方法引用参数外其他的参数类型要和原方法对应.

## 全新exists查询

### 基于`SelectMapper#adapterExists`

##### 说明

在原查询方法上开启Exists查询.

##### 示例

```java
Optional<Integer> optional = mapper.existsP3(studentMapper::findByNameStartsWithAndAgeBetween,"王",15,19);
Assertions.assertTrue(optional.isPresent());
```

###### 注意

调用的方法除第一个方法引用参数外其他的参数类型要和原方法对应.