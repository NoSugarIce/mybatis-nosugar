## 乐观锁

##### 说明

更新的时候带上当前版本的下一个版本,如果同一条数据同时被多人更新,肯定只会有一个更新成功.

### 使用

在版本列上添加`@Version`

##### 生效条件

NoSugar自带的update方法且更新时参数上乐观锁标识的列有值.

#### 支持类型

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
