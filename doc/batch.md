## 批处理增强模式

##### 说明

插入或者更新可以不用逐条执行,变成批量执行.

### 使用

依赖于Mybatis-Spring

需做以下步骤即可开启批处理模式

1. `MybatisMapperFactoryBean`设置为`MybatisMapperFactoryBean.class`
2. 在`Mapper`类上配置`@SpeedBatch`.
3. 需要批处理的`Mapper`方法上添加`@SpeedBatch`.
4. 数据库需要打开批处理模式.

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
