# Spring Boot REST API项目

作为REST API纯后端项目，接口统一返回JSON。

## 目录结构

项目根目录：

```shell
rest-spring
├── README.md # 说明文档
├── core # 核心模块
├── docs # 关于项目的一些辅助资料
├── lombok.config # Lombok配置文件
├── pom.xml # 父POM，用于维护子模块
└── web # Web服务模块
```

Java源码目录：

```shell
.
├── Application.java # Spring Boot启动类
├── annotation # 存放自定义注解
├── aspect # 存放切面代码
├── config # 存放自定义配置类
│   └── CaffeineCacheConfig.java # 配置类以`Config`结尾
├── constant # 存放常量、枚举类
├── controller # 存放控制器类
│   └── ResourceController.java # 控制器类以`Controller`结尾
├── exception # 存放自定义异常类
│   └── TokenAuthException.java # 异常类以`Exception`结尾
├── rest # 存放完成REST API统一异常处理及结果封装的实现
├── security # 存放Token 鉴权认证机制的实现
├── mapper # 存放MyBatis SQL映射器（数据访问层，即DAO）
│   └── UserMapper.java #  MyBatis接口以`Mapper`结尾
├── domain # 存放领域模型类，可作为MyBatis映射器的结果对象
├── service # 存放业务逻辑接口及实现类
│   ├── impl # 存放数据库实体映射类
    │   ├── UserServiceImpl.java # 接口实现类以`Impl`结尾
│   └── UserService.java # 接口以`Service`结尾
└── util # 存放工具类和辅助代码
    └── JwtUtils.java # 工具类以`Utils`结尾
```

## 分层注意点

- `Controller`层传参禁止使用`Map`，当超过3个参数时，应该封装为`Query`查询对象
- `Controller`层里只编写参数校验代码，业务逻辑全部放到`Service`层代码中去做
- `Service`层做实际业务逻辑，可以按照功能模块做好定义和区分，相互可以调用
- 功能模块`Service`之间引用时，不要渗透到`DAO`层（或者`mapper`层），基于`Service`层进行调用和复用比较合理

## Final变量

局部变量，方法参数，推荐都使用`final`关键字修饰，除非该变更或参数确实需要再赋值。

## 编码规范

Java中常用到的命名形式共有三种：

- 首字母大写的大驼峰命名（UpperCamelCase）：用于类名
- 首字母小写的小驼峰命名（lowerCamelCase）：用于方法名和局部变量名
- 全部大写的并用下划线分割单词（UPPER_CAMEL_CONSTANT_CASE）：用于常量名和枚举属性名

### 通用命名规则

- 尽量不要使用拼音。尤其杜绝拼音和英文混用。对于一些通用的表示或者难以用英文描述的可以采用拼音，一旦采用拼音就坚决不能和英文混用
- 以**两个空格**作为**缩进**
- 命名过程中尽量不要出现特殊的字符，除常量中的下划线`_`分隔符
- 妙用介词，如for（可以用同音的4代替）、to（可用同音的2代替）、from、with、of等

### 代码注释

类注释：

```java
/**
 * 类的介绍：这是一个用来做什么事情的类，有哪些功能，用到的技术……
 * 
 * @author 作者姓名
 * @see <a href="">可补充外部的参考资料链接</a>
 */
class Foo {
}
```

属性注释：

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
   * 方法的详细说，能干嘛，怎么实现的，注意事项……
   *
   * @param xxx 参数1的使用说明，能否为null
   * @return 返回结果的说明，不同情况下会返回怎样的结果
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

- 数据库表名和字段名：**单数**形式，多个单词以下划线`_`分隔
- URL：全部小写，多个单词用中划线`-`分隔
  - 试情况使用**单复数**形式
- 项目名：全部小写，多个单词用中划线`-`分隔
  - 统一使用**单数**形式
- 包名：全部小写，多个名称（若语义需包含多个单词则直接连接到一起即可）使用英文点`.`分隔
  - 统一使用**单数**形式
- 类名：单词首字母大写
  - 统一使用**单数**形式，除了工具类以`utils`结尾
  - 测试类：以要测试的类开头，以`Test`结尾
  - 抽象类：以`Abstract`开头
  - 工具类：以`Utils`结尾
  - 异常类：以`Exception`结尾
  - 控制器类：以 `Controller`结尾
  - 业务逻辑接口：以`Service`结尾
  - 接口实现类：以要实现的接口开头，再以`Impl`结尾
  - 数据库实体映射对象：以`Entity`结尾
  - 数据查询对象：以`Query`结尾
  - 数据传输对象：以`Dto`结尾
- 方法名：首字母小写，多个单词组成时，后续单词首字母都要大写，其余都为小写（不考虑是否为特有名词缩写）
  - 用来判断真伪的方法：
    - `is`为前缀：对象是否符合期待的状态
    - `can`为前缀：对象能否执行所期待的动作
    - `should`为前缀：调用方执行某个命令或方法是好还是不好, 应不应该, 或者说推荐还是不推荐
    - `has`为前缀：对象是否持有所期待的数据和属性
    - `needs`为前缀：调用方是否需要执行某个命令或方法
  - 用来检查的方法：
    - `ensure`为前缀：检查是否为期待的状态，不是则抛出异常或返回error code
    - `validate`为前缀：检查是否为正确的状态，不是则抛出异常或返回error code
  - 按需执行的方法：
    - `IfNeeded`为后缀：需要的时候执行，不需要的时候什么都不做
    - `try`为前缀：尝试执行，失败时抛出异常或是返回error code
    - `OrDefault`为后缀：尝试执行，失败时返回默认值
    - `OrElse`为后缀：尝试执行，失败时返回实际参数中指定的值
    - `force`为前缀：强制尝试执行，失败时抛出异常或是返回error code
  - 异步/同步相关方法：
    - `blocking`为前缀：线程阻塞方法
    - `InBackground`为后缀：执行在后台的线程
    - `Async`为后缀：异步方法
    - `Sync`为后缀：对应已有异步方法的同步方法
    - `schedule`为前缀或单独使用：将任务放入队列
    - `start`为前缀或单独使用：开始异步任务
    - `stop`为前缀或单独使用：停止异步任务
  - 回调方法：
    - `on`为前缀：事件发生时执行
    - `before`为前缀：事件发生前执行
    - `after`为前缀：事件发生后执行
  - 与集合操作相关的方法：
    - `contains`：检查是否包含某项目
    - `add`：添加
    - `insert`：插入到索引位
    - `put`：添加与key对应的元素
    - `remove`：移除元素
    - `enqueue`：添加到队列的最末位
    - `dequeue`：从队列头部取出并移除
    - `push`：添加到栈顶
    - `pop`：从栈顶取出并移除
    - `peek`：从栈顶取出但不移除
    - `find`：查询符合条件的项目
  - 与数据相关的方法：
    - `initial`为前缀：初始化数据
    - `create`为前缀：新创建，并方法内涉及存储
    - `generate`为前缀：新创建，但方法内不涉及存储
    - `save`为前缀：持久化保存
    - `load`为前缀：本地读取
    - `fetch`为前缀：远程读取
    - `update`为前缀：更新既有某物
    - `remove`为前缀：删除
    - `from`为中部：从既有的某物新建，或是从其他的数据新建
    - `to`为前缀：转换为指定类型数据返回
  - DAO层（Mapper）的方法：
    - `saveXxx`：新增
    - `deleteXxxByXxx`：删除
    - `updateXxx`：更新
    - `findXxxByXxx`：查询
- 变量名：同方法名
  - POJO中的布尔变量一律不要加`is`前缀，数据库中的布尔字段全部都要加`is_`前缀
- 常量名：全部大写，多个单词用下划线`_`分隔
  - 全局常量：`public static final`
  - 类内常量：`private static final`
  - 局部常量（方法内或参数）：同变量名
