# Spring Boot REST API 项目

作为 REST API 纯后端项目，接口统一返回 JSON.

## 目录结构

项目根目录:

```shell
rest-spring
├── README.md # 说明文档
├── core # 核心模块
├── docs # 关于项目的一些辅助资料
├── lombok.config # Lombok 配置文件
├── pom.xml # 父 POM, 用于维护子模块
└── web # Web 服务模块
```

Java 源码目录:

```shell
.
├── Application.java # Spring Boot 启动类
├── annotation # 存放自定义注解
├── aspect # 存放切面代码
├── config # 存放配置类
│   └── CaffeineCacheConfig.java # 配置类以 `Config` 结尾
├── constant # 存放常量、枚举类
│   ├── ErrorCodeEnum # 枚举类以 `Enum` 结尾
│   └── Mappings.java
├── controller # 存放控制器类
│   └── ResourceController.java # 控制器类以 `Controller` 结尾
├── exception # 存放自定义异常类
│   └── TokenAuthException.java # 异常类以 `Exception` 结尾
├── filter # 存放过滤器, 拦截器相关代码
├── mapper # 存放数据访问层接口 (基于 MyBatis)
│   └── UserMapper.java #  MyBatis 接口以 `Mapper` 结尾
├── model # 存放数据模型类
│   ├── dto # 存放数据传输对象类, 用于 Service 层处理
│       ├── UserDto.java # 以 `Dto` 结尾
│   └── entity # 存放数据库实体映射类, 用于 DAO 层处理
│       └──  UserEntity.java # 以 `Entity` 结尾
├── service # 存放业务逻辑接口及实现类
│   ├── impl # 存放数据库实体映射类
    │   ├── UserServiceImpl.java # 接口实现类以 `Impl` 结尾
│   └── UserService.java # 接口以 `Service` 结尾
└── util # 存放工具类和辅助代码
    └── JwtUtils.java # 工具类以 `Utils` 结尾
```

## 分层注意点

- `Controller` 层传参禁止使用 `Map`, 当超过3个参数时, 应该封装为 `Query` 查询对象
- `Controller` 层里只编写参数校验代码, 业务逻辑全部放到 `Service` 层代码中去做
- `Service` 层做实际业务逻辑, 可以按照功能模块做好定义和区分, 相互可以调用
- 功能模块 `Service` 之间引用时, 不要渗透到 `DAO` 层 (或者 `mapper` 层), 基于 `Service` 层进行调用和复用比较合理
- 业务逻辑层 `Service` 和数据库 `DAO` 层的操作对象不要混用. `Controller` 层的数据对象不要直接渗透到 `DAO` 层 (或者 `mapper` 层); 同理数据表实体对象 `Entity` 也不要直接传到 `Controller` 层进行输出或展示

## 编码规范

> 项目中所有标点符号 (包含注释), 统一使用英文半角标点符号.

Java 中常用到的命名形式共有三种:

- 首字母大写的大驼峰命名 (UpperCamelCase): 用于类名
- 首字母小写的小驼峰命名 (lowerCamelCase): 用于方法名和局部变量名
- 全部大写的并用下划线分割单词的 UPPER_CAMEL_CONSTANT_CASE: 用于常量名和枚举属性名

### 通用命名规则

- 尽量不要使用拼音. 尤其杜绝拼音和英文混用. 对于一些通用的表示或者难以用英文描述的可以采用拼音, 一旦采用拼音就坚决不能和英文混用
- 命名过程中尽量不要出现特殊的字符, 常量除外
- 尽量不要和 JDK 或者框架中已存在的类重名，也不能使用 Java 中的关键字命名
- 妙用介词, 如 for (可以用同音的4代替), to (可用同音的2代替), from, with, of 等

### 代码注释

类注释:

```java
/**
 * 类的介绍: 这是一个用来做什么事情的类, 有哪些功能, 用到的技术...
 * 
 * @author 作者姓名
 * @see <a href="">参考</a>
 */
class Foo {
}
```

属性注释:

```java
class Foo {

  /** 提示信息 */
  private String userName;
}
```

方法注释:

```java
class Foo {
  
  /**
   * 方法的详细说明，能干嘛，怎么实现的，注意事项...
   *
   * @param xxx 参数1的使用说明, 能否为 null
   * @return 返回结果的说明, 不同情况下会返回怎样的结果
   * @throws Exception 说明什么情况下会抛出异常
   */ 
  String bar(String xxx) throws Exception {
    throw new Exception("测试异常");
  }
}
```

其他说明:
  - 枚举类的各个属性值都要使用**属性注释**
  - 双斜杠和星号之后要用1个空格分隔

### 命名规范

- 项目名: 全部小写, 多个单词用中划线 `-` 分隔
  - 如: `spring-boot-starter-json`
- 包名: 全部小写, 多个名称 (若语义需包含多个单词则直接连接到一起即可) 使用英文点 `.` 分隔
  - 统一使用**单数**形式
  - 前缀使用顶级域名的反写 (`com.公司名.项目名.模块名.xxx`)
  - 如: `org.springframework`
- 类名: 单词首字母大写
  - 若有复数含义, 则可以使用复数形式
  - 使用**名词或名词短语**. 接口还可以使用 **形容词或形容词短语**
  - 如: `WebMvcConfigurer`, `Callable`
  - 分类:
    - 测试类: 以要测试的类开头, 以 `Test` 结尾
    - 抽象类: 以 `Abstract` 开头
    - 枚举类: 以 `Enum` 结尾
    - 工具类: 以 `Utils` 结尾
    - 异常类: 以 `Exception` 结尾
    - 控制器类: 以 `Controller` 结尾
    - 业务逻辑接口: 以 `Service` 结尾
    - 接口实现类: 以要实现的接口开头, 再以 `Impl` 结尾
    - 数据库实体映射对象: 以 `Entity` 结尾
    - 数据查询对象: 以 `Query` 结尾
    - 数据传输对象: 以 `Dto` 结尾
- 方法名: 首字母小写, 多个单词组成时, 后续单词首字母都要大写, 其余都为小写，不论是否为特有名词缩写
  - 如: `configureContentNegotiation()`
  - 用来判断真伪的方法:
    - `is` 为前缀: 对象是否符合期待的状态
    - `can` 为前缀: 对象能否执行所期待的动作
    - `should` 为前缀: 调用方执行某个命令或方法是好还是不好, 应不应该, 或者说推荐还是不推荐
    - `has` 为前缀: 对象是否持有所期待的数据和属性
    - `needs` 为前缀: 调用方是否需要执行某个命令或方法
  - 用来检查的方法:
    - `ensure` 为前缀: 检查是否为期待的状态, 不是则抛出异常或返回 error code
    - `validate` 为前缀: 检查是否为正确的状态, 不是则抛出异常或返回 error code
  - 按需执行的方法:
    - `IfNeeded` 为后缀: 需要的时候执行, 不需要的时候什么都不做
    - `try` 为前缀: 尝试执行, 失败时抛出异常或是返回 error code
    - `OrDefault` 为后缀: 尝试执行, 失败时返回默认值
    - `OrElse` 为后缀: 尝试执行, 失败时返回实际参数中指定的值
    - `force` 为前缀: 强制尝试执行, 失败时抛出异常或是返回 error code
  - 异步/同步相关方法:
    - `blocking` 为前缀: 线程阻塞方法
    - `InBackground` 为后缀: 执行在后台的线程
    - `Async` 为后缀: 异步方法
    - `Sync` 为后缀: 对应已有异步方法的同步方法
    - `schedule` 为前缀或单独使用: 将任务放入队列
    - `start` 为前缀或单独使用: 开始异步任务
    - `stop` 为前缀或单独使用: 停止异步任务
  - 回调方法:
    - `on` 为前缀: 事件发生时执行
    - `before` 为前缀: 事件发生前执行
    - `after` 为前缀: 事件发生后执行
  - 操作对象生命周期的方法:
    - `initialize`: 初始化
    - `pause`: 暂停
    - `destroy`: 销毁
  - 与集合操作相关的方法:
    - `contains`: 检查是否包含某项目
    - `add`: 添加
    - `insert`: 插入到索引位
    - `put`: 添加与 key 对应的元素
    - `remove`: 移除元素
    - `enqueue`: 添加到队列的最末位
    - `dequeue`: 从队列头部取出并移除
    - `push`: 添加到栈顶
    - `pop`: 从栈顶取出并移除
    - `peek`: 从栈顶取出但不移除
    - `find`: 查询符合条件的项目
  - 与数据相关的方法:
    - `create` 为前缀: 新创建, 并方法内涉及存储
    - `generate` 为前缀: 新创建, 但方法内不涉及存储
    - `save` 为前缀: 保存
    - `load` 为前缀: 读取
    - `fetch` 为前缀: 远程读取
    - `update` 为前缀: 更新既有某物
    - `remove` 为前缀: 删除
    - `from` 为中部: 从既有的某物新建, 或是从其他的数据新建. 如: `fromConfig`
    - `to` 为前缀: 转换. 如: `toString`
- 变量名: 同方法名. 如: `JsonObjectRequest`
  - POJO 中的布尔变量一律不要加 `is` 前缀, 数据库中的布尔字段全部都要加 `is_` 前缀
- 常量名: 全部大写，多个单词用下划线 `_` 分隔. 如: `INTERNAL_SERVER_ERROR`
  - 全局常量: `public static final`
  - 类内常量: `private static final`
  - 局部常量 (方法内或参数): 同变量名