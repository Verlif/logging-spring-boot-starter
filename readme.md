# Logging

日志服务与API接口日志服务  
* 自动注入来使用`LogService`的日志服务
* 对接口方法或Controller类标记`@LogIt`来对接口进行日志处理（调用方法前与调用方法后）

## 使用

1. 启用服务

   在任意配置类上使用`@EnableLogService`注解启用日志服务，随后便可使用`LogService`与`@LogIt`注解。

### 通过自动注入来使用`LogService`

```java
@Autowired
private LogService logService;

public void useIt() {
    logService.debug("debug");
    logService.info("info");
    logService.warn("warn");
    logService.error("error");
    logService.log(LogLevel.INFO, "info");
}
```

`LogService`使用的是接口注入模式，需要替换其中的功能只需要实现`LogService`接口并添加到Bean即可完成替换。

```java
@Component
public final class MyLogService implements LogService {
    // 自行完成方法实现
}
```

### 通过`@LogIt`来添加接口日志

在`api`方法上或是`controller类`上标记`@LogIt`注解（方法上的注解优先于类上的注解），即可完成此接口访问或是其下所有接口的访问日志接入。  
注解的必填项是`message`，可选项为
* `type`（类型），用于区分日志类型
* `sync`（同步模式），使用当前线程同步调用日志处理器方法。
* `handler`（日志处理器），用于选择日志处理方法

```java
@LogIt(message = "有人登录啦啦啦", type = Login.class, handler = DefaultApiLogHandler.class)
@Operation(summary = "登录")
@PostMapping
public BaseResult<?> login(@RequestBody LoginInfo loginInfo){
    return loginService.login(loginInfo);
}
```

### 通过`ApiLogHandler`完成日志逻辑

组件内置了一个默认的接口日志处理器：

```java
@Component
public class DefaultApiLogHandler implements ApiLogHandler {

    @Autowired
    private LogService logService;

   @Override
   public void onLog(Method method, LogIt logIt, Object[] obs, long time) {
      logService.log(logIt.level(), method.getName() + " >> " + logIt.message());
   }
   
    @Override
    public void onReturn(Method method, LogIt logIt, Object o, long time) {
        logService.log(logIt.level(), method.getName() + " return >> " + o + " at " + time);
    }

}
```

开发者要使用自定义的接口日志处理器只需要实现`ApiLogHandler`接口，并注入Bean池即可使用。  
使用时，将`@LogIt`注解`handler()`改为自己的实现类即可。

## 添加依赖

1. 添加Jitpack仓库源

maven

```xml
<repositories>
   <repository>
       <id>jitpack.io</id>
       <url>https://jitpack.io</url>
   </repository>
</repositories>
```

2. 添加依赖

maven

```xml
   <dependencies>
       <dependency>
           <groupId>com.github.Verlif</groupId>
           <artifactId>logging-spring-boot-starter</artifactId>
           <version>2.6.6-2.0</version>
       </dependency>
   </dependencies>
```

## 配置文件

接口日志的配置属性如下：
```yaml
station:
  # API日志配置
  api-log:
    # 是否开启API日志功能
    enabled: true
    # 当API日志功能开启后，允许的日志等级（与LogLevel枚举类对应），使用英文,隔开。为空则允许全部
    level: debug, info, warning, error
    # 当API日志功能开启后，允许的日志类型（短类名），使用英文,隔开。为空则允许全部
    type: Get, Post
    # 日志线程信息
    pool-info:
      # 日志线程池最大线程数
      max: 200
      # 日志等待队列长度
      length: 1000
```
